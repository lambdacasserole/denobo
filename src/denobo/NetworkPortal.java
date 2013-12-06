package denobo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
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
            System.out.println(ex.getMessage());
        }
    }
    
    public void connect(String hostName, int portNumber) {
        try {
            Socket socket = new Socket(hostName, portNumber);
            // Handshake.
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Listens for connections on the server port and adds 
     */
    private void listenForConnections() {
        disconnecting = false;
        while(!disconnecting) {
            try {
                Socket acceptedSocket = serverSocket.accept();
                connections.add(new DenoboConnection(acceptedSocket, this));
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        disconnecting = false;
    }

    public void disconnect() {
        try {
            // first prevent anyone else from connecting 
            disconnecting = true;
            serverSocket.close();
            
            // wait for the connection accepting thread to terminate
            acceptThread.join();
            
            // close any connections we have
            for (DenoboConnection currentConnection : connections) {
                currentConnection.disconnect();
            }
            
            // remove all the connections from our collection
            connections.clear();
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void handleMessage(String message) {
        // Outgoing.
    }

    @Override
    public boolean hasRouteToAgent(String name) {
        return false;
    }

    
    
    @Override
    public void onAuthenticated(DenoboConnection connection) {
        
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        
    }

    @Override
    public void onReceivedMessage(DenoboConnection connection, String message) {
                
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        // Received a message (just printing the output for now)
        System.out.println(message);
    }
}
