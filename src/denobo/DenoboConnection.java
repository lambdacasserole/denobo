package denobo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bidirectional communication line between two socket agents.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DenoboConnection implements Runnable {

    /**
     * The socket to handle receiving data from.
     */
    private final Socket connection;

    /**
     * A thread that handles waiting for data to be received from this connection.
     */
    private Thread receiveThread;
    
    /**
     * The observers that we notify whenever certain events occur.
     */
    private final List<DenoboConnectionObserver> observers;
    
    /**
     * A boolean flag that is used to signal to the receive thread to terminate
     * and prevent any actions from occurring anymore on this object in this state.
     */
    private boolean disconnected;
    
    /**
     * The BufferedReader object to use for efficiently reading any data we have
     * received from this connection.
     */
    private BufferedReader connectionReader;
    
    /**
     * The BufferedWriter object to use for efficiently sending data through this
     * connection. This is a more efficient way of sending data as multiple send requests 
     * are buffered then sent in one batch.
     */
    private PrintWriter connectionWriter;

    /**
     * Creates a ConnectionHandler that will handle receiving data from a
     * socket.
     *
     * @param connection The connection to handle receiving data from
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
                DenoboPacket nextPacket;
                
                // TODO: Fix this code, very hacky.
                String buffer = connectionReader.readLine();
                System.out.println("Got data: " + buffer);
                if(buffer.equals(DenoboPacket.PACKET_HEADER)) {
                    
                    // Parse out status code.
                    String[] statusCodeField = connectionReader.readLine().split(":");
                    int statusCode = Integer.parseInt(statusCodeField[1]);
                    
                    // Parse out body length.
                    String[] bodyLengthField = connectionReader.readLine().split(":");
                    int bodyLength = Integer.parseInt(bodyLengthField[1]);
                    
                    // Parse out payload.
                    char[] packetBody = new char[bodyLength];
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
            
            // Close the socket to the connection which will cause an exception
            // to be thrown by receiveThread.
            connection.close();
            
            // Wait for the receive thread to terminate.
            receiveThread.join();

            // Close I/O streams.
            connectionReader.close();
            connectionWriter.close();
            
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
        connectionWriter.println(message.toString());
        System.out.println("Writing data to port [" + connection.getPort() + "]...");
        
        // WORK OUT WHY: This fixes the bug of data not getting received because for
        // some reason the data isn't been send until we flush the stream
        connectionWriter.flush();
    }
    
}
