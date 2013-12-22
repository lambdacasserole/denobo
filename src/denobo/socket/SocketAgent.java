package denobo.socket;

import denobo.socket.connection.SocketConnectionObserver;
import denobo.socket.connection.SocketConnection;
import denobo.Agent;
import denobo.Message;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import denobo.socket.connection.state.GreetingState;
import denobo.socket.connection.state.WaitForGreetingState;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

/**
 * Represents an Agent with the ability to use sockets to connect Agents.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class SocketAgent extends Agent implements SocketConnectionObserver {

    /**
     * The list of connections that we have connected to us.
     */
    private final List<SocketConnection> connections;

    /**
     * The list of observers we need to notify on certain events.
     */
    private final List<SocketAgentObserver> observers;

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
     * The maximum number of connections permitted to be connected.
     */
    private Semaphore connectionsPermits;

    /**
     * A status variable we use to indicate that
     * {@link SocketAgent#acceptThread} should abort.
     */
    private volatile boolean shutdown;

    
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
        connectionsPermits = new Semaphore(maxConnections, false);
        connections = Collections.synchronizedList(new ArrayList<SocketConnection>(maxConnections));
        observers = new CopyOnWriteArrayList<>();
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
        return observers.add(observer);
    }

    /**
     * Removes an observer from the list of observers for this SocketAgent.
     *
     * @param observer the observer to remove
     * @return true if the observer to remove was found and removed, otherwise
     * false
     */
    public boolean removeObserver(SocketAgentObserver observer) {
        return observers.remove(observer);
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
     * @param portNumber The port number to listen for connection requests on
     */
    public void advertiseConnection(int portNumber) {

        // shutdown the socket agent related stuff in case we are already
        // advertising
        socketAgentShutdown();

        try {
            serverSocket = new ServerSocket(portNumber);
            acceptThread = new Thread() {
                @Override
                public void run() {
                    acceptConnectionsLoop();
                }
            };
            acceptThread.start();
        } catch (IOException ex) {
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Listens for connections on the server port and adds connections to a list
     * when they are requested by connection clients.
     */
    private void acceptConnectionsLoop() {

        shutdown = false;

        while (!shutdown) {
            try {

                final Socket acceptedSocket = serverSocket.accept();
                if (!connectionsPermits.tryAcquire()) {
                    // Tell them there we cannot accept them because we have too
                    // many peers already connected.
                    final SocketConnection tempConnection = 
                            new SocketConnection(acceptedSocket, null);
                    
                    // TODO: This packet never seems to get there, the conenction
                    // is probably shut down too fast.
                    tempConnection.send(new Packet(PacketCode.TOO_MANY_PEERS));
                    tempConnection.disconnect();
                    continue;
                }
                
                // notify any observers
                for (SocketAgentObserver currentObserver : observers) {
                    currentObserver.incomingConnectionAccepted(this, acceptedSocket.getInetAddress().getHostAddress(), acceptedSocket.getPort());
                }

                addRunningConnection(new SocketConnection(acceptedSocket, new WaitForGreetingState()));

            } catch (IOException ex) {
                // TODO: Handle exception.
                System.out.println(ex.getMessage());
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
            // TODO: Notify the caller in some way
            return;
        }
        
        try {

            final Socket newSocket = new Socket();

            // attempt to connect
            newSocket.connect(new InetSocketAddress(hostName, portNumber));

            // notify any observers that we have connected
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddSucceeded(this, hostName, portNumber);
            }

            addRunningConnection(new SocketConnection(newSocket, new GreetingState()));

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
     * Initializes a SocketConnection. We attach ourselves as an observer to it,
     * add it into our collection of connections then starts up it's receive
     * thread so we can begin receiving data from it.
     *
     * @param newConnection The SocketConnection to initialize.
     */
    private void addRunningConnection(SocketConnection newConnection) {

        newConnection.addObserver(this);
        connections.add(newConnection);
        newConnection.startRecieveThread();
    }

    /**
     * Closes and removes any SocketConnection objects we have attached.
     */
    public void removeConnections() {

        // Close any connections we have.
        // we make a copy because the original list will get modified when an
        // event is thrown everytime we close a connection which will remove that
        // connection from the list we are iterating which will result in a 
        // ConcurrentModificationException
        
        final SocketConnection[] connectionsListCopy;
        
        synchronized (connections) {
            
            connectionsListCopy = connections.toArray(new SocketConnection[connections.size()]);

            // Remove all the connections from our collection since we've already
            // copied it and it's much faster clearing it than removing each one
            // individually - especially if the List implementation is a
            // CopyOnWriteArrayList.
            connections.clear();                    
        }
            
        for (SocketConnection currentConnection : connectionsListCopy) {
            currentConnection.disconnect();
        }
    }
    
    /**
     * Shuts down this SocketAgent. No more incoming connection requests will
     * be accepted and any current connections are terminated and removed. This
     * only shuts down the socket parts and the base class isn't shutdown.
     * Invoking advertiseConnection reverses the shutdown and permits socket
     * connections again.
     */
    private void socketAgentShutdown() {
        
        shutdown = true;
        
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
            
            final SocketConnection connectionRecievedFrom = ((SocketAgentMessage) message).getReceivedFrom();
            // Broadcast to connected peers.
            synchronized (connections) {
                for (SocketConnection connection : connections) {
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
                for (SocketConnection connection : connections) {
                    connection.send(message);
                }
            }
        }
        
    }
    
    // Observer notify Methods
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void connectionAuthenticated(SocketConnection connection) {

        System.out.println(connection.getRemoteAddress() + ":" 
                + connection.getRemotePort() + " Authenticated");
    }

    @Override
    public void connectionShutdown(SocketConnection connection) {

        // Release the connection limit permit this connection used
        connectionsPermits.release();
        
        connections.remove(connection);

        // notify any observers
        for (SocketAgentObserver currentObserver : observers) {
            currentObserver.connectionClosed(this, connection.getRemoteAddress(), connection.getRemotePort());
        }
    }

    @Override
    public void messageReceived(SocketConnection connection, Message message) {

        // Let our message queue deal with the message. We wrap the messsage in
        // a SocketAgentMessage so that we know not to broadcast this message
        // back to the Agent who sent us the message originally.
        queueMessage(new SocketAgentMessage(connection, message));
    }
}
