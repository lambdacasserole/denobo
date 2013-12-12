package denobo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a networking-enabled agent portal.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class NetworkPortal extends Portal implements DenoboConnectionObserver {

    /**
     * The list of connections that we have connected to us.
     */
    private final List<DenoboConnection> connections;
    
    /**
     * The list of observers we need to notify on certain events.
     */
    private final List<NetworkPortalObserver> observers;
    
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
     * A status variable we use to indicate that the acceptThread should abort.
     */
    private boolean shutdown;
    
    
    
    
    /**
     * Creates a {@link NetworkPortal} with the specified name.
     * 
     * @param name The name assigned to the NetworkPortal
     */
    public NetworkPortal(String name) {
        super(name);
        connections = new ArrayList<>();
        observers = new ArrayList<>();
    }
    
    /**
     * Sets up allowing incoming connection requests to be accepted.
     * 
     * @param portNumber    The port number to listen for connection requests on
     */
    public void advertiseConnection(int portNumber) {
        
        // shutdown in case we are already advertising
        shutdown();
                
        try {
            serverSocket = new ServerSocket(portNumber);
            acceptThread = new Thread() {
                @Override
                public void run() {
                    acceptConnectionsLoop();
                }
            };
            acceptThread.start();
        } catch(IOException ex) {
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
                System.out.println("Socket server open on port [" 
                        + serverSocket.getLocalPort() + "] and listening...");
                
                final Socket acceptedSocket = serverSocket.accept();
                
                // notify any observers
                for (NetworkPortalObserver currentObserver : observers) {
                    currentObserver.incomingConnectionAccepted(acceptedSocket.getInetAddress().getHostAddress(), acceptedSocket.getPort());
                }
                
                System.out.println("Socket server open on port [" 
                        + serverSocket.getLocalPort() + "] dispensed a socket on port [" 
                        + acceptedSocket.getPort() + "]."); 
                
                addRunningConnection(acceptedSocket);
                
            } catch (IOException ex) {
                // TODO: Handle exception.
                System.out.println(ex.getMessage());
            }
        }
        
        shutdown = false;
    }
    
    /**
     * Connects this {@link NetworkPortal} to a remote network portal.
     * 
     * @param hostName      the host name of the machine hosting the remote portal
     * @param portNumber    the port number the remote portal is listening on
     */
    public void addConnection(String hostName, int portNumber) {
        try {
            
            final Socket newSocket = new Socket();
            final InetSocketAddress address = new InetSocketAddress(hostName, portNumber);
            
            // attempt to connect
            newSocket.connect(address);
            
            // notify any observers that we have connected
            for (NetworkPortalObserver currentObserver : observers) {
                currentObserver.connectionAddSucceeded(hostName, portNumber);
            }
            
            addRunningConnection(newSocket);
            
        } catch (IOException ex) {
            // notify any observers that we failed to connect
            for (NetworkPortalObserver currentObserver : observers) {
                currentObserver.connectionAddFailed(hostName, portNumber);
            }
        }
    }
    
    private void addRunningConnection(Socket s)  {
        final DenoboConnection newConnection = new DenoboConnection(s);
        newConnection.addObserver(this);
        connections.add(newConnection);
        newConnection.startRecieveThread();
    }
    
    /**
     * Shuts down this NetworkPortal. No more incoming connection requests will be
     * accepted and any current connections are terminated and removed.
     */
    public void shutdown() {

        shutdown = true;
        
        try {
            // First prevent anyone else from connecting.
            if (serverSocket != null) { serverSocket.close(); }
            
            // Wait for the connection accepting thread to terminate.
            if (acceptThread != null) { acceptThread.join(); }

        } catch (IOException | InterruptedException ex) {
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
        }
             
        // Close any connections we have.
        // we make a copy because the original list will get modified when an
        // event is thrown everytime we close a connection which will remove that
        // connection from the list we are iterating which will result in a 
        // ConcurrentModificationException
        final ArrayList<DenoboConnection> connectionsListCopy = new ArrayList<>(connections);
        for (DenoboConnection currentConnection : connectionsListCopy) {
            currentConnection.disconnect();     
        }
            
        // Remove all the connections from our collection. (Even though they
        // should all be removed from the connectionShutdown event anyway) 
        connections.clear();
    }
    
    @Override
    protected void handleMessage(Message message) {
        
        super.handleMessage(message);
        for (DenoboConnection connection : connections) {
            connection.send(message);
        }
    }
    

    /**
     * Adds an observer to the list of observers to be notified of events.
     * 
     * @param observer      The observer to add
     * @return true if it was successfully added to he list of observers,
     * otherwise false is returned
     */
    public boolean addObserver(NetworkPortalObserver observer) {
        return observers.add(observer);
    }
    
    /**
     * Removes an observer from the list of observers for this NetworkPortal.
     * 
     * @param observer  the observer to remove
     * @return          true if the observer to remove was found and removed, otherwise false
     */
    public boolean removeObserver(NetworkPortalObserver observer) {
        return observers.remove(observer);
    }
    
    /**
     * Removes all observers from this NetworkPortal.
     */
    public void removeObservers() {
        observers.clear();
    }
    

//    @Override
//    public boolean hasRouteToAgent(String name) {
//        return true;
//    }

    @Override
    public void connectionAuthenticated(DenoboConnection connection) {
        
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        System.out.println("Authenticated");
        
    }

    @Override
    public void connectionShutdown(DenoboConnection connection) {
        
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        System.out.println(connection.getRemoteAddress() + ":" + connection.getRemotePort() 
                + " has disconnected");
        
        // needs syncronising
        connections.remove(connection);
        
        // notify any observers
        for (NetworkPortalObserver currentObserver : observers) {
            currentObserver.connectionClosed(connection.getRemoteAddress(), connection.getRemotePort());
        }
    }
    
    @Override
    public void messageReceived(DenoboConnection connection, DenoboPacket packet) {
                
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        

        System.out.println(packet.getBody());

        
        Message msg = Message.deserialize(packet.getBody());
        
        // TODO: This might result in unecessary broadcasting and loop backs.
        
        for (DenoboConnection currentConnection : connections) {
            // Pointless sending it back to the NetworkPortal who sent it to us
            if (currentConnection != connection) {
                currentConnection.send(msg);
            }
        }
        
        super.handleMessage(msg);
    }
}
