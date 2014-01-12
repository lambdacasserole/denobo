package denobo.socket;

import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.DenoboConnection;
import denobo.Agent;
import denobo.Message;
import denobo.Route;
import denobo.RoutingWorkerListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
     * A list of SocketAgentObserver instances observing events occurring for 
     * this SocketAgent.
     */
    private final List<SocketAgentObserver> observers;
    
    /**
     * A map of RoutingWorkerListener instances that we notify if we find a 
     * remote route to a destination.
     */
    // TODO: We can't be having this being public
    public final HashMap<String, List<RoutingWorkerListener>> remoteDestinationFoundCallbacks;

    /**
     * The DenoboConnectionObserver that will observe each DenoboConnection that
     * is connected to this SocketAgent.
     */
    private final DenoboConnectionObserver connectionObserver;
    
    /**
     * A Semaphore for limiting the number of connections permitted to be 
     * connected to this SocketAgent.
     * <p>
     * This limit includes incoming and outgoing connections.
     */
    private final Semaphore connectionsPermits;
    
    /**
     * The ServerSocket instance we listen and accept incoming connection 
     * requests on.
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
     * A status variable we use to indicate that this SocketAgent is 
     * advertising.
     */
    private volatile boolean advertising;
    
    /**
     * An instance of SocketAgentConfiguration that holds the configuration
     * options for this SocketAgent.
     */
    private final SocketAgentConfiguration configuration;

    
    /* ---------- */
    
    
    /**
     * Instantiates a new instance of a SocketAgent with the specified 
     * configuration options.
     * 
     * @param name              the name to be assigned to the SocketAgent
     * @param cloneable         whether or not the agent is cloneable
     * @param configuration     the configuration options to use
     */
    public SocketAgent(String name, boolean cloneable, SocketAgentConfiguration configuration) {
        
        super(name, cloneable);
        
        Objects.requireNonNull(configuration, "Configuration cannot be null.");
        
        // Check max connections.
        final int maxConnections = configuration.maximumConnections;
        if (maxConnections < 1) { 
            throw new IllegalArgumentException("Maximum number of connections"
                    + " cannot be less than 1.");
        }

        this.configuration = configuration;
        
        connectionsPermits = new Semaphore(maxConnections, false);
        connections = Collections.synchronizedList(new ArrayList<DenoboConnection>());
        observers = new CopyOnWriteArrayList<>();
        connectionObserver = new SocketAgentDenoboConnectionObserver();
        remoteDestinationFoundCallbacks = new HashMap<String, List<RoutingWorkerListener>>();
        
    }
    
    /**
     * Instantiates a {@link SocketAgent} with the specified name and cloneable
     * option.
     * <p>
     * This constructs a SocketAgent that uses no security and does not limit 
     * the number of connections it can handle.
     *
     * @param name          the name to be assigned to the SocketAgent
     * @param cloneable     whether or not the agent is cloneable
     */
    public SocketAgent(String name, boolean cloneable) {

        this(name, cloneable, new SocketAgentConfiguration());

    }
    
    /**
     * Instantiates a non-cloneable SocketAgent with the specified name.
     * <p>
     * This constructs a non-cloneable SocketAgent that uses no security and
     * does not limit the number of connections it can handle.
     * 
     * @param name the name to be assigned to the SocketAgent
     */
    public SocketAgent(String name) {
        this(name, false);
    }

    
    /* ---------- */
    
    
    /**
     * Returns the configuration for this SocketAgent.
     * 
     * @return the configuration
     */
    public SocketAgentConfiguration getConfiguration() {
        return configuration;
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
        return observers.add(Objects.requireNonNull(observer, "The observer to"
                + " add is null"));
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
        return observers.remove(Objects.requireNonNull(observer, "The observer"
                + " to remove is null"));
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
     * @throws IOException  if an I/O error occurred whilst creating the socket 
     *                      to listen for connections on.
     */
    public void startAdvertising(int portNumber) throws IOException {

        // Port number range check.
        if (portNumber < 0 || portNumber > 0xFFFF) {
            throw new IllegalArgumentException("Port number out of range: " 
                    + portNumber);
        }
        
        // Stop advertising in case we already are/
        stopAdvertising();

        serverSocket = new ServerSocket(portNumber);

        advertising = true;
        
        // Notify any observers that this SocketAgent has started advertising.
        for (SocketAgentObserver currentObserver : observers) {
            currentObserver.advertisingStarted(this, portNumber);
        }          

        // Start the acceptThread to start accepting connection requests.
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
     * @return  the port currently advertising on or the last port advertised
     *          on, -1 if it has never advertised yet
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
                    
                    final DenoboConnection acceptedConnection = new DenoboConnection(this, acceptedSocket, DenoboConnection.InitialState.WAIT_FOR_GREETING);
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
                    new DenoboConnection(this, acceptedSocket, 
                            DenoboConnection.InitialState.TOO_MANY_PEERS);
                    
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
     * @param hostname      the host name of the machine hosting the remote 
     *                      agent
     * @param portNumber    the port number the remote agent is listening on
     * @return              true if the connection was successfully made,
     *                      otherwise false
     */
    public boolean addConnection(String hostname, int portNumber) {
        
        // Fail to connect if we're at our connection limit.
        if (!connectionsPermits.tryAcquire()) {
            return false;
        }
        
        try {

            // Attempt to connect.
            final Socket newSocket = new Socket();
            newSocket.connect(new InetSocketAddress(hostname, portNumber));

            // Create new connection.
            final DenoboConnection addedConnection = new DenoboConnection(this, 
                    newSocket, DenoboConnection.InitialState.INITIATE_GREETING);
            addedConnection.addObserver(connectionObserver);
            connections.add(addedConnection);

            // Notify any observers that we have connected.
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddSucceeded(this, addedConnection, 
                        hostname, portNumber);
            }
            
            // Begin recieve pump on connection.
            addedConnection.startRecieveThread();
            
            return true;

        } catch (IOException ex) {

            /* 
             * Release the permit we acquired for this connection since we 
             * failed to connect.
             */
            connectionsPermits.release();
            
            // Notify any observers that we failed to connect.
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionAddFailed(this, hostname, portNumber);
            }
            
            return false;

        }
        
    }
    
    /**
     * Returns a snapshot copy of all currently connected connections to this
     * SocketAgent.
     * <p>
     * This represents a snapshot copy of the all current DenoboConnection 
     * instances that are connected to this SocketAgent at the time of this
     * method being invoked. Modifications to the returned list do not effect 
     * the internal list held in this SocketAgent instance and changes to the 
     * internal list after this method call are not reflected in the returned
     * list.
     * 
     * @return  the unmodifiable list of connections.
     */
    public List<DenoboConnection> getConnections() {
        synchronized (connections) {
            return new ArrayList<>(connections);
        }
    }

    /**
     * Closes and removes any {@link DenoboConnection} instances that are
     * connected to this SocketAgent.
     */
    public void removeConnections() {

        /* 
         * Close any connections we have. We make a copy because the original 
         * list will get modified when an event is thrown everytime we close a 
         * connection which will remove that connection from the list we are 
         * iterating which will result in a ConcurrentModificationException.
         */
        final DenoboConnection[] connectionsListCopy;
        
        synchronized (connections) {
            
            connectionsListCopy = connections.toArray(
                    new DenoboConnection[connections.size()]);

            /* 
             * Remove all the connections from our collection since we've 
             * already copied it and it's much faster clearing it than removing
             * each one individually, especially if the List implementation is a
             * CopyOnWriteArrayList.
             */
            connections.clear();    
            
        }
            
        // Disconnect all connections.
        for (DenoboConnection currentConnection : connectionsListCopy) {
            currentConnection.disconnect();
        }
        
    }

    /**
     * Shutdown this SocketAgent. 
     * <p>
     * No more incoming connection requests will be accepted and any current 
     * connections are terminated and removed. This is a full shutdown that 
     * prevents this SocketAgent being used again.
     */
    @Override
    public void shutdown() {

        /*
         * Shutdown our layer first to prevent any more connections or messages
         * coming through.
         */ 
        
        // Stop anyone else from connecting.
        stopAdvertising();

        /* 
         * Remove any current connections so we don't receive any more messages
         * from any connections.
         */ 
        removeConnections();
        
        // Superclass shutdown code can now execute.
        super.shutdown();
        
    }
    
    /**
     * Searches for a route to a remote agent.
     * 
     * @param destinationAgentName  the name of the agent to route to
     * @param localRoute            the local route taken to reach this
     *                              SocketAgent instance
     * @param listeners             the listeners to notify if a route is found
     */
    public void routeToRemote(String destinationAgentName, Route localRoute, List<RoutingWorkerListener> listeners) {
        
        remoteDestinationFoundCallbacks.put(destinationAgentName, listeners);
        for (DenoboConnection currentConnection : connections) {
            currentConnection.routeToRemote(destinationAgentName, localRoute);
        }
        
    }
    
    public void invalidateRemote(String agent1, String agent2, List<String> visitedNodes) {
        
        for (DenoboConnection currentConnection : connections) {
            currentConnection.invalidateRemote(agent1, agent2, visitedNodes);
        }
        
    }

    @Override
    public boolean handleMessage(Message message) {
        
        // Store the name of the next agent.
        final String nextAgentName = message.getRoute().peek();
        
        // Handle the case that the agent is local.
        if (super.handleMessage(message)) { return true; } 
        
        // Handle the case that the agent is remote.
        for (DenoboConnection currentConnection : connections) {
            if (currentConnection.getRemoteAgentName().equals(nextAgentName)) {
                currentConnection.send(message);
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * Anonymous class that a SocketAgent creates to observe all 
     * {@link DenoboConnection} instances that are connected.
     */
    private class SocketAgentDenoboConnectionObserver implements DenoboConnectionObserver {

        @Override
        public void connectionAuthenticated(DenoboConnection connection) {

            System.out.println(connection.getRemoteAddress() + ":" 
                    + connection.getRemotePort() + " Authenticated");
            
        }

        @Override
        public void connectionShutdown(DenoboConnection connection) {

            // Release the connection limit permit this connection used.
            connectionsPermits.release();

            // Remove connection.
            connections.remove(connection);

            // Notify any observers.
            for (SocketAgentObserver currentObserver : observers) {
                currentObserver.connectionClosed(SocketAgent.this, connection);
            }
            
        }

        @Override
        public void messageReceived(DenoboConnection connection, Message message) {

            /* 
             * Let our message queue deal with the message. 
             */ 
            queueMessage(message);
            
        }
        
    }
    
}
