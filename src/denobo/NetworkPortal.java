package denobo;

import java.io.IOException;
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

    private List<DenoboConnection> connections; 
    
    private ServerSocket serverSocket;
    private Thread acceptThread;
    
    private boolean disconnecting;
    
    public NetworkPortal(String name, int portNumber) {
        
        super(name);
        connections = new ArrayList<>();
        
        try {
            
            serverSocket = new ServerSocket(portNumber);
            acceptThread = new Thread() {
                @Override
                public void run() {
                    listenForConnections();
                }
            };
            acceptThread.start();
            
        } catch(IOException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
        
    }
    
    private void addRunningConnection(Socket s)  {
        final DenoboConnection newConnection = new DenoboConnection(s);
        newConnection.addObserver(this);
        connections.add(newConnection);

        newConnection.startRecieveThread();
    }
    
    /**
     * Listens for connections on the server port and adds connections to a list
     * when they are requested by connection clients.
     */
    private void listenForConnections() {
        
        while (!disconnecting) {
            try {
                
                System.out.println("Socket server open on port [" 
                        + serverSocket.getLocalPort() + "] and listening...");
                
                final Socket acceptedSocket = serverSocket.accept();
                
                System.out.println("Socket server open on port [" 
                        + serverSocket.getLocalPort() + "] dispensed a socket on port [" 
                        + acceptedSocket.getPort() + "]."); 
                
                addRunningConnection(acceptedSocket);
                
            } catch (IOException ex) {
            
                // TODO: Handle exception.
                System.out.println(ex.getMessage());
                
            }
        }
        
        disconnecting = false;
        
    }
    
    /**
     * Connects this {@link NetworkPortal} to a remote network portal.
     * 
     * @param hostName      the host name of the machine hosting the remote portal
     * @param portNumber    the port number the remote portal is listening on
     */
    public void connect(String hostName, int portNumber) {
        try {
            
            addRunningConnection(new Socket(hostName, portNumber));
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
    }

    public void disconnect() {
        try {
            
            // First prevent anyone else from connecting.
            disconnecting = true;
            serverSocket.close();
            
            // Wait for the connection accepting thread to terminate.
            acceptThread.join();
            
            // Close any connections we have.
            for (DenoboConnection currentConnection : connections) {
                currentConnection.disconnect();
            }
            
            // Remove all the connections from our collection.
            connections.clear();
            
        } catch (IOException | InterruptedException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
    }

    @Override
    public void handleMessage(Message message) {
        
        super.handleMessage(message);
        for(DenoboConnection connection : connections) {
            connection.send(message);
        }
        
    }

    @Override
    public boolean hasRouteToAgent(String name) {
        return true;
    }

    @Override
    public void connectionAuthenticated(DenoboConnection connection) {
        
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        System.out.println("Authenticated");
        
    }

    @Override
    public void messageReceived(DenoboConnection connection, DenoboPacket packet) {
                
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        // Received a message (just printing the output for now)
        System.out.println(packet.getBody());
    }
}
