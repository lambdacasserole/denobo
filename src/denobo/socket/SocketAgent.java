package denobo.socket;

import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.DenoboConnection;
import denobo.Agent;
import denobo.Message;
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
     * A list of DenoboConnection instances that we have connected to this
     * SocketAgent.
     */
    private final List<DenoboConnection> connections;

    /**
     * A list of {@link SocketAgentObserver} objects observing events occurring
     * for this SocketAgent.
     */
    private final List<SocketAgentObserver> observers;

    /**
     * The DenoboConnectionObserver that will observe each DenoboConnection that
     * is connected to this SocketAgent.
     */
    private final DenoboConnectionObserver connectionObserver;
    
    /**
     * For saving the maximum number of connections this SocketAgent is allowed.
     * <p>
     * Don't use this for limiting any connections. This is simply for retrieval
     * purposes only.
     */
    private final int maxConnections;
    
    /**
     * A Semaphore for limiting the number of connections permitted to be 
     * connected to this SocketAgent.
     * <p>
     * This limit includes incoming and outgoing connections.
     */
    private final Semaphore connectionsPermits;
    
    /**
     * The ServerSocket instance we listen and accept connection requests on.
     */
    private ServerSocket serverSocket;

    /**
     * The Thread that simply sits and waits for connection requests for it to
     * accept and add to {@link SocketAgent#connections}.
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
    private volatile boolean advertising;
    

    
    /**
     * Instantiates a {@link SocketAgent} with the specified name and cloneable
     * option.
     *
     * @param name              the name to be assigned to the SocketAgent
     * @param cloneable         whether or not the agent is cloneable
     * @param maxConnections    the maximum number of connections from that this
     *                          SocketAgent should permit
     */
    public SocketAgent(String name, boolean cloneable, int maxConnections) {
        
        super(name, cloneable);
        
        if (maxConnections < 1) { 
            throw new IllegalArgumentException("Maximum number of connections is less than 1: " + maxConnections);
        }
        
        this.maxConnections = maxConnections;
        connectionsPermits = new Semaphore(maxConnections, false);
        connections = Collections.synchronizedList(new ArrayList<DenoboConnection>(maxConnections));
        observers = new CopyOnWriteArrayList<>();
        connectionObserver = new SocketAgentDenoboConnectionObserver();

    }
    
    /**
     * Instantiates a non-cloneable {@link SocketAgent} with the specified name.
     *
     * @param name              the name to be assigned to the SocketAgent
     * @param maxConnections    the maximum number of connections from that this
     *                          SocketAgent should permit
     */
    public SocketAgent(String name, int maxConnections) {
        this(name, false, maxConnections);
    }

    /**
     * Returns the maximum number of connections this SocketAgent is permitted 
     * to have.
     * 
     * @return the maximum number of connections permitted
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Adds a {@link SocketAgentObserver} to the list of observers to be 
     * notified of events from this SocketAgent.
     *
     * @param observer the observer object to add
     * @return true if it was successfully added to he list of observers,
     * otherwise false is returned
     */
    public boolean addObserver(SocketAgentObserver observer) {
        return observers.add(Objects.requireNonNull(observer, "The observer to add is null"));
    }

    /**
     * Removes a {@link SocketAgentObserver} from the list of observers for this 
     * SocketAgent.
     *
     * @param observer the observer to remove
     * @return true if the observer to remove was found and removed, otherwise
     * false is returned
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
     * @param portNumber    the port number to listen for connection requests on
     * @throws IOException  if an I/O error occurred whilst creating the socket to
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
        
        // Notify any observers that this SocketAgent has started advertising
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
     * @return true if this SocketAgent is advertising, otherwise false
     */
    public boolean isAdvertising() {
        return advertising;
    }
    
    /**
     * Returns the local port this SocketAgent is advertising on or the last port
     * it was advertising on if it is now not advertising. 
     * <p>
     * If it has never advertised, -1 is returned.
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
            } catch (IOException ex) { 
                System.out.println(ex.getMessage());
            }
        }
 
        // Wait for the connection accepting thread to terminate.
        if (acceptThread != null) {
            try {
                acceptThread.join();
            } catch (InterruptedException ex) { 
                System.out.println(ex.getMessage());
            }
        }
       
        shutdownAcceptThread = false;
        advertising = false;
       
        /*
         * Notify any observers that this SocketAgent has stopped advertising
         * if it was previously advertising
         */
        
        if (serverSocket != null) {
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.advertisingStopped(this, serverSocket.getLocalPort());
            }
        }
       
    }

    /**
     * Listens for connections on the server port and accepts incoming connection
     * requests.
     */
    private void acceptConnectionsLoop() {

        while (!shutdownAcceptThread) {
            try {

                final Socket acceptedSocket = serverSocket.accept();
                if (connectionsPermits.tryAcquire()) {
                    
                    final DenoboConnection acceptedConnection = new DenoboConnection(acceptedSocket, DenoboConnection.InitialState.WAIT_FOR_GREETING);
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
                    // many peers already connected
                    new DenoboConnection(acceptedSocket, DenoboConnection.InitialState.TOO_MANY_PEERS);
                    
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
     * @param hostname      the host name of the machine hosting the remote agent
     * @param portNumber    the port number the remote agent is listening on
     */
    public void addConnection(String hostname, int portNumber) {
        
        if (!connectionsPermits.tryAcquire()) {
            // Reached connection limit
            // TODO: Maybe notify the caller in some way
            return;
        }
        
        try {

            final Socket newSocket = new Socket();

            // attempt to connect
            newSocket.connect(new InetSocketAddress(hostname, portNumber));

            final DenoboConnection addedConnection = new DenoboConnection(newSocket, DenoboConnection.InitialState.INITIATE_GREETING);
            addedConnection.addObserver(connectionObserver);
            connections.add(addedConnection);

            // notify any observers that we have connected
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddSucceeded(this, addedConnection, hostname, portNumber);
            }
            
            addedConnection.startRecieveThread();

        } catch (IOException ex) {

            // Release the permit we acquired for this connection since we failed
            // to connect
            connectionsPermits.release();
            
            // notify any observers that we failed to connect
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddFailed(this, hostname, portNumber);
            }

        }
        
    }
    
    /**
     * Returns a snapshot copy of all currently connected connections to this
     * SocketAgent.
     * <p>
     * This represents a snapshot copy of the all current DenoboConnection objects
     * that are connected to this SocketAgent at the time of this method being
     * invoked. Modifications to the returned list do not effect the internal 
     * list held in this SocketAgent instance and changes to the internal list
     * after this method call are not reflected in the returned list.
     * 
     * @return the unmodifiable list of connections.
     */
    public List<DenoboConnection> getConnections() {
        
        synchronized (connections) {
            return new ArrayList<>(connections);
        }
        
    }

    /**
     * Closes and removes any DenoboConnection objects this SocketAgent has attached.
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
     * Shutdown this SocketAgent. 
     * <p>
     * No more incoming connection requests will
     * be accepted and any current connections are terminated and removed. This
     * is a full shutdown that prevents this SocketAgent being used again.
     */
    @Override
    public void shutdown() {

        /*
         * Shutdown our layer first to prevent any more connections or messages
         * coming through.
         */ 
        
        // Stop anyone else from connecting
        stopAdvertising();

        /* 
         * Remove any current connections so we don't receive anymore messages
         * from any connections
         */ 
        removeConnections();
        
        // Super class shutdown code can now execute
        super.shutdown();
        
    }
    

    @Override
    protected void broadcastMessage(Message message) {
        
        /* 
         * Super class behaviour is still required (broadcasting to any locally
         * connected Actor's)
         */
        super.broadcastMessage(message);
        
        // Now broadcast to any Actor's who are connected to us via a socket.
        
        /* 
        * If the Message instance is of type SocketAgentMessage, we can find out
        * who originally send us the Message so that we know not to pass it back
        * to them.
        */
        if (message instanceof SocketAgentMessage) {
            
            final DenoboConnection connectionRecievedFrom = ((SocketAgentMessage) message).getReceivedFrom();
            // Broadcast to connected peers.
            synchronized (connections) {
                for (DenoboConnection connection : connections) {
                    
                    /*
                     * Check if we received the message from one of our connections,
                     * and if it was then we don't need to bother broadcasting it
                     * back to that connection.
                     */
                    if (connection != connectionRecievedFrom) {
                        connection.send(message);
                    }
                }
            }

        } else {
            /*
             * The message was probably internal so we need to broadcast it to
             * everyone connected to us.
             */
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

            /* 
             * Let our message queue deal with the message. We wrap the messsage in
             * a SocketAgentMessage so that we know not to broadcast this message
             * back to the Agent who sent us the message originally
             */ 
            queueMessage(new SocketAgentMessage(connection, message));
            
        }
    }
    
}
