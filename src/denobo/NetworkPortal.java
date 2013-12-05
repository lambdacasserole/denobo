package denobo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class NetworkPortal extends Portal {

    private List<Socket> connections; 
    
    private ServerSocket serverSocket;
    private Thread acceptThread;
    
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
        while(true) {
            try {
                connections.add(serverSocket.accept());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public NetworkPortal(String name, int portNumber) {
        super(name);
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
    
    public void disconnect() {
        
    }
    
    @Override
    public void handleMessage(String message) {
        // Outgoing.
    }

    @Override
    public boolean hasRouteToAgent(String name) {
        return false;
    }
    
}
