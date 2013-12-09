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
    
    /**
     * Listens for connections on the server port and adds connections to a list
     * when they are requested by connection clients.
     */
    private void listenForConnections() {
        disconnecting = false;
        while (!disconnecting) {
            try {
                final Socket acceptedSocket = serverSocket.accept();
                final DenoboConnection denoboConnection = new DenoboConnection(acceptedSocket);
                denoboConnection.addObserver(this);
                connections.add(denoboConnection);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        disconnecting = false;
    }
    
    public void connect(String hostName, int portNumber) {
        try {
            final Socket connectSocket = new Socket(hostName, portNumber);
            final DenoboConnection denoboConnection = new DenoboConnection(connectSocket);
            denoboConnection.addObserver(this);
            // denoboConnection.initialize();
            connections.add(denoboConnection);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
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
    public void handleMessage(Message message) {
        // Outgoing message to dispatch.
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
    public void onReceivedMessage(DenoboConnection connection, DenoboPacket packet) {
                
        ////////////////////////////////////////////////////////////////////////
        // THIS METHOD COULD POTENTIALLY BE EXECUTED BY MULTIPLE THREADS!     //
        ////////////////////////////////////////////////////////////////////////
        
        // Received a message (just printing the output for now)
        System.out.println(packet.getBody());
    }
    
}
