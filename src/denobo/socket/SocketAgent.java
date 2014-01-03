package denobo.socket;

import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.DenoboConnection;
import denobo.Agent;
import denobo.Message;
import denobo.socket.connection.state.GreetingState;
import denobo.socket.connection.state.TooManyPeersState;
import denobo.socket.connection.state.WaitForGreetingState;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

/**
 * Represents an Agent with the ability to use sockets to connect Agents.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class SocketAgent extends Agent {

    /**
     * The list of connections that we have connected to us.
     */
    private final List<DenoboConnection> connections;

    /**
     * The list of observers we need to notify on certain events.
     */
    private final List<SocketAgentObserver> observers;

    /**
     * The DenoboConnectionObserver that will observer each DenoboConnection that
     * is connected to this SocketAgent.
     */
    private final DenoboConnectionObserver connectionObserver;
    
    /**
     * The maximum number of connections permitted to be connected.
     */
    private final Semaphore connectionsPermits;
    
    /**
     * The socket we listen and accept connection requests on.
     */
    private ServerSocket serverSocket;

    /**
     * The thread that simply sits and waits for connection requests for it to
     * accept and add to our connection list.
     */
    private Thread acceptThread;

    /**
     * A status variable we use to indicate that
     * {@link SocketAgent#acceptThread} should abort.
     */
    private volatile boolean shutdownAcceptThread;

    /**
     * A status variable we use to indicate that this SocketAgent is advertising.
     */
    private boolean advertising;
    

    
    /**
     * Creates a {@link SocketAgent} with the specified name and cloneable
     * option.
     *
     * @param name          The name assigned to the SocketAgent
     * @param cloneable     Whether or not the agent is cloneable
     * @param maxConnections    The maximum number of connections from this Agent
     *                          permitted.
     */
    public SocketAgent(String name, boolean cloneable, int maxConnections) {
        
        super(name, cloneable);
        
        if (maxConnections < 1) { 
            throw new IllegalArgumentException("Maximum number of connections is less than 1: " + maxConnections);
        }
        
        connectionsPermits = new Semaphore(maxConnections, false);
        connections = Collections.synchronizedList(new ArrayList<DenoboConnection>(maxConnections));
        observers = new CopyOnWriteArrayList<>();
        connectionObserver = new SocketAgentDenoboConnectionObserver();

    }
    
    /**
     * Creates a non-cloneable {@link SocketAgent} with the specified name.
     *
     * @param name          The name assigned to the SocketAgent
     * @param maxConnections    The maximum number of connections from this Agent
     *                          permitted.
     */
    public SocketAgent(String name, int maxConnections) {
        this(name, false, maxConnections);
    }


    /**
     * Adds an observer to the list of observers to be notified of events.
     *
     * @param observer The observer to add
     * @return true if it was successfully added to he list of observers,
     * otherwise false is returned
     */
    public boolean addObserver(SocketAgentObserver observer) {
        return observers.add(Objects.requireNonNull(observer, "The observer to add is null"));
    }

    /**
     * Removes an observer from the list of observers for this SocketAgent.
     *
     * @param observer the observer to remove
     * @return true if the observer to remove was found and removed, otherwise
     * false
     */
    public boolean removeObserver(SocketAgentObserver observer) {
        return observers.remove(Objects.requireNonNull(observer, "The observer to remove is null"));
    }

    /**
     * Removes all observers from this SocketAgent.
     */
    public void removeObservers() {
        observers.clear();
    }

    /**
     * Sets up allowing incoming connection requests to be accepted.
     *
     * @param portNumber    The port number to listen for connection requests on
     * @throws IOException  If an I/O error occurred whilst creating the socket to
     *                      listen for connections on.
     */
    public void startAdvertising(int portNumber) throws IOException {

        if (portNumber < 0 || portNumber > 0xFFFF) {
            throw new IllegalArgumentException("Port value out of range: " + portNumber);
        }
        
        // Stop advertising in case we already are
        stopAdvertising();


        serverSocket = new ServerSocket(portNumber);

        advertising = true;
        
        // notify any observers
        for (SocketAgentObserver currentObserver : observers) {
            currentObserver.advertisingStarted(this, portNumber);
        }          

        // Start the acceptThread to start accepting connection requests
        acceptThread = new Thread() {
            @Override
            public void run() {
                acceptConnectionsLoop();
            }
        };
        acceptThread.start();

    }
    
    /**
     * Returns whether this SocketAgent is currently advertising at the time this
     * method was called.
     * 
     * @return true if this SocketAgent is advertising, otherwise false.
     */
    public boolean isAdvertising() {
        return advertising;
    }
    
    /**
     * Returns the local port this SocketAgent is advertising on or the last port
     * it was advertising on if it is now not advertising. If it has never advertised,
     * -1 is returned.
     * 
     * @return  The port currently advertising on or the last port advertised on. -1
     *          if it has never advertised yet.
     */
    public int getAdvertisingPort() {
        return (serverSocket != null) ? serverSocket.getLocalPort() : -1;
    }
    
    /**
     * Stops this SocketAgent from accepting anymore connection requests.
     */
    public void stopAdvertising() {
       
        shutdownAcceptThread = true;
 
        // First prevent anyone else from connecting.
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) { System.out.println(ex.getMessage()); }
        }
 
        // Wait for the connection accepting thread to terminate.
        if (acceptThread != null) {
            try {
                acceptThread.join();
            } catch (InterruptedException ex) { System.out.println(ex.getMessage()); }
        }
       
        shutdownAcceptThread = false;
       
        advertising = false;
       
        // notify any observers if this was previously advertising
        if (serverSocket != null) {
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.advertisingStopped(this, serverSocket.getLocalPort());
            }
        }
       
    }

    /**
     * Listens for connections on the server port and adds connections to a list
     * when they are requested by connection clients.
     */
    private void acceptConnectionsLoop() {

        while (!shutdownAcceptThread) {
            try {

                final Socket acceptedSocket = serverSocket.accept();
                if (connectionsPermits.tryAcquire()) {
                    
                    final DenoboConnection acceptedConnection = new DenoboConnection(acceptedSocket, new WaitForGreetingState());
                    acceptedConnection.addObserver(connectionObserver);  
                    connections.add(acceptedConnection);

                    // notify any observers
                    for (SocketAgentObserver currentObserver : observers) {
                        currentObserver.incomingConnectionAccepted(this, acceptedConnection);
                    }
                    
                    // Start receiving now after we have notified all the observers
                    // so that if the connection disconnects, we are still in sync.
                    // (We are notified of a disconnect when we are receiving)
                    acceptedConnection.startRecieveThread(); 

                } else {
                    
                    // Tell them there we cannot accept them because we have too
                    // many peers already connected.
                    new DenoboConnection(acceptedSocket, new TooManyPeersState());
                    
                }

            } catch (IOException ex) {
                
                // TODO: Handle exception.
                System.out.println("acceptConnectionsLoop: " + ex.getMessage());
                
            }
            
        }
        
    }

    /**
     * Connects this {@link SocketAgent} to a another SocketAgent through a
     * socket.
     *
     * @param hostName the host name of the machine hosting the remote agent
     * @param portNumber the port number the remote agent is listening on
     */
    public void addConnection(String hostName, int portNumber) {
        
        if (!connectionsPermits.tryAcquire()) {
            // Reached connection limit
            // TODO: Maybe notify the caller in some way
            return;
        }
        
        try {

            final Socket newSocket = new Socket();

            // attempt to connect
            newSocket.connect(new InetSocketAddress(hostName, portNumber));

            final DenoboConnection addedConnection = new DenoboConnection(newSocket, new GreetingState());
            addedConnection.addObserver(connectionObserver);
            connections.add(addedConnection);

            // notify any observers that we have connected
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddSucceeded(this, addedConnection, hostName, portNumber);
            }
            
            addedConnection.startRecieveThread();

        } catch (IOException ex) {

            // Release the permit we acquired for this connection since we failed
            // to connect.
            connectionsPermits.release();
            
            // notify any observers that we failed to connect
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddFailed(this, hostName, portNumber);
            }

        }
        
    }
    
    /**
     * Returns a read-only snapshot of all current connected connections to this
     * SocketAgent.
     * 
     * @return The unmodifiable list of connections.
     */
    public List<DenoboConnection> getConnections() {
        
        synchronized (connections) {
            return Collections.unmodifiableList(new ArrayList<>(connections));
        }
        
    }

    /**
     * Initializes a DenoboConnection. We attach ourselves as an observer to it,
     * add it into our collection of connections then starts up it's receive
     * thread so we can begin receiving data from it.
     *
     * @param newConnection The DenoboConnection to initialize.
     */
    private void addRunningConnection(DenoboConnection newConnection) {

        newConnection.addObserver(connectionObserver);  
        connections.add(newConnection);
        newConnection.startRecieveThread();
        
    }

    /**
     * Closes and removes any DenoboConnection objects we have attached.
     */
    public void removeConnections() {

        // Close any connections we have.
        // we make a copy because the original list will get modified when an
        // event is thrown everytime we close a connection which will remove that
        // connection from the list we are iterating which will result in a 
        // ConcurrentModificationException
        
        final DenoboConnection[] connectionsListCopy;
        
        synchronized (connections) {
            
            connectionsListCopy = connections.toArray(new DenoboConnection[connections.size()]);

            // Remove all the connections from our collection since we've already
            // copied it and it's much faster clearing it than removing each one
            // individually - especially if the List implementation is a
            // CopyOnWriteArrayList.
            connections.clear();                    
        }
            
        for (DenoboConnection currentConnection : connectionsListCopy) {
            currentConnection.disconnect();
        }
        
    }
    
    /**
     * Shuts down this SocketAgent. No more incoming connection requests will
     * be accepted and any current connections are terminated and removed. This
     * only shuts down the socket parts and the base class isn't shutdown.
     */
    private void socketAgentShutdown() {
        
        stopAdvertising();

        removeConnections();

    }

    /**
     * Shuts down this SocketAgent. No more incoming connection requests will
     * be accepted and any current connections are terminated and removed. This
     * is a full shutdown that prevents this Agent being used again.
     */
    @Override
    public void shutdown() {

        // Shutdown our layer first to prevent any more connections or messages
        // coming through.
        socketAgentShutdown();
        
        // Super class can perform a shutdown now
        super.shutdown();
        
    }
    

    @Override
    protected void broadcastMessage(Message message) {
        
        // Super class behaviour is still required (broadcasting to any locally
        // connected Actor's)
        super.broadcastMessage(message);
        
        // Now broadcast to any Actor's who are connected to us via a socket.
        
        // If the Message instance is of type SocketAgentMessage, we can find out
        // who originally send us the Message so that we know not to pass it back
        // to them.
        if (message instanceof SocketAgentMessage) {
            
            final DenoboConnection connectionRecievedFrom = ((SocketAgentMessage) message).getReceivedFrom();
            // Broadcast to connected peers.
            synchronized (connections) {
                for (DenoboConnection connection : connections) {
                    // Check if we received the message from one of our connections,
                    // and if it was then we don't need to bother broadcasting it
                    // back to that connection.
                    if (connection != connectionRecievedFrom) {
                        connection.send(message);
                    }
                }
            }

        } else {
            // The message was probably internal so we need to broadcast it to
            // everyone connected to us.
            synchronized (connections) {
                for (DenoboConnection connection : connections) {
                    connection.send(message);
                }
            }
        }
        
    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Anonymous class that a SocketAgent creates to observer all DenoboConnections
     * that are connected.
     */
    private class SocketAgentDenoboConnectionObserver implements DenoboConnectionObserver {

        @Override
        public void connectionAuthenticated(DenoboConnection connection) {

            System.out.println(connection.getRemoteAddress() + ":" 
                    + connection.getRemotePort() + " Authenticated");
        }

        @Override
        public void connectionShutdown(DenoboConnection connection) {

            // Release the connection limit permit this connection used
            connectionsPermits.release();

            connections.remove(connection);

            // notify any observers
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionClosed(SocketAgent.this, connection);
            }
            
        }

        @Override
        public void messageReceived(DenoboConnection connection, Message message) {

            // Let our message queue deal with the message. We wrap the messsage in
            // a SocketAgentMessage so that we know not to broadcast this message
            // back to the Agent who sent us the message originally.
            queueMessage(new SocketAgentMessage(connection, message));
            
        }
    }
    
    
}
