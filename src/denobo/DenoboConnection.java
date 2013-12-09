package denobo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bidirectional communication line between two {@link NetworkPortal} objects.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DenoboConnection implements Runnable {

    /**
     * The socket to handle receiving data from.
     */
    private final Socket connection;
    
    /**
     * The observers that we notify whenever certain events occur.
     */
    private final List<DenoboConnectionObserver> observers;

    /**
     * A thread that handles waiting for data to be received from this connection.
     */
    private Thread receiveThread;
    
    /**
     * A boolean flag that is used to signal {@link DenoboConnection#receiveThread} to terminate
     * and prevent any more actions from occurring on the object.
     */
    private boolean disconnected;
    
    /**
     * The {@link BufferedReader} object to use for efficiently reading any data we have
     * received from this connection.
     */
    private BufferedReader connectionReader;
    
    /**
     * Holds a {@link PrintWriter} object for writing to the connection's underlying socket.
     */
    private PrintWriter connectionWriter;

    /**
     * Creates a {@link ConnectionHandler} that will handle receiving data from a socket.
     *
     * @param connection    the connection to handle receiving data from
     */
    public DenoboConnection(Socket connection) {
        
        // Store reference to socket and initialise observer list.
        this.connection = connection;
        this.observers = new ArrayList<>();
        
        try {
            
            // Get I/O streams.
            connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connectionWriter = new PrintWriter(connection.getOutputStream());
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
        
    }
    
    /**
     * Starts waiting to receive data through this connection.
     */
    public void startRecieveThread() {
        
        receiveThread = new Thread(this);
        receiveThread.start();
        
    }

    @Override
    public void run() {
        
        try {
            while (!disconnected) {
                
                System.out.println("Waiting for data on port [" + connection.getPort() + "]...");
                
                // TODO: Fix this code, very hacky.
                final String buffer = connectionReader.readLine();
                System.out.println("Got data: " + buffer);
                if (buffer.equals(DenoboProtocol.PACKET_HEADER)) {
                    
                    DenoboPacket nextPacket;
                
                    // Parse out status code.
                    final String[] statusCodeField = connectionReader.readLine().split(":");
                    final int statusCode = Integer.parseInt(statusCodeField[1]);
                    
                    // Parse out body length.
                    final String[] bodyLengthField = connectionReader.readLine().split(":");
                    final int bodyLength = Integer.parseInt(bodyLengthField[1]);
                    
                    // Parse out payload.
                    final char[] packetBody = new char[bodyLength];
                    connectionReader.read(packetBody);
                    
                    // Let the observers deal with packet.
                    nextPacket = new DenoboPacket(statusCode, String.valueOf(packetBody));
                    for (DenoboConnectionObserver currentObserver : observers) {
                        currentObserver.messageReceived(this, nextPacket); 
                    }
                    
                }
                
            }
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
        
        // Connection has closed, clean up.
        disconnect();

    }
    
    /**
     * Adds an observer to the list of observers to be notified of events.
     * 
     * @param observer The observer to add
     * @return true if it was successfully added to he list of observers,
     * otherwise false is returned
     */
    public boolean addObserver(DenoboConnectionObserver observer) {
        return observers.add(observer);
    }
    
    /**
     * Removes an observer from the list of observers for this DenoboConnection.
     * 
     * @param observer  the observer to remove
     * @return          true if the observer to remove was found and removed, otherwise false
     */
    public boolean removeObserver(DenoboConnectionObserver observer) {
        return observers.remove(observer);
    }
    
    /**
     * Removes all observers from this DenoboConnection.
     */
    public void removeObservers() {
        observers.clear();
    }

    /**
     * Disconnects and frees up any resources used by this DenoboConnection.
     */
    public void disconnect() {
        
        // If we're already disconnected, don't try again.
        if (disconnected) { return; }
        
        // Specify that we're now disconnected.
        disconnected = true;

        try {

            // Close I/O streams.
            connectionReader.close();
            connectionWriter.close();
            
            // Close the socket to the connection which will cause an exception
            // to be thrown by receiveThread.
            connection.close();
            
            // Wait for the receive thread to terminate.
            receiveThread.join();
            
        } catch (IOException | InterruptedException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
        
    }
    
    /**
     * Sends a message over this connection.
     * 
     * @param message   the message to send 
     */
    public void send(Message message) {
        
        // TODO: Proper serialisation logic.

        // Write serialised message to output stream.
        System.out.println("Writing data to port [" + connection.getPort() + "]...");
        connectionWriter.println(message.toString());
        connectionWriter.flush();
            
    }
    
}
