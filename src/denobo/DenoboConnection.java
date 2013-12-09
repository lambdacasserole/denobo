package denobo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
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
    private final Thread receiveThread;
    
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
    private ObjectInputStream connectionReader;
    
    /**
     * The BufferedWriter object to use for sending data through this
     * connection. This is a more efficient way of sending data as multiple send
     * requests are buffered then sent in one batch.
     */
    private ObjectOutputStream connectionWriter;

    /**
     * Creates a ConnectionHandler that will handle receiving data from a
     * socket.
     *
     * @param connection The connection to handle receiving data from
     */
    public DenoboConnection(Socket connection) {
        this.connection = connection;
        this.observers = new ArrayList<>();
        
        try {
            connectionReader = new ObjectInputStream(connection.getInputStream());
            connectionWriter = new ObjectOutputStream(connection.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        receiveThread = new Thread(this);
        receiveThread.start();
    }

    @Override
    public void run() {
        
        try {
            while (!disconnected) {
                try {

                    // Let the observers deal with packet.
                    DenoboPacket nextPacket = (DenoboPacket)connectionReader.readObject();
                    for (DenoboConnectionObserver currentObserver : observers) {
                        currentObserver.onReceivedMessage(this, nextPacket); 
                    }
                    
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (IOException ex) {
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
     * @param observer The observer to remove
     * @return true if the observer to remove was found and removed otherwise
     * false is returned if the specified observer was not found in the list of
     * observers
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
        
        if (disconnected) {
            return;
        }
        
        disconnected = true;

        try {
            // close the socket to the connection which will cause an exception
            // to be thrown in the receiveThread.
            connection.close();
            
            // wait for the receive thread to terminate
            receiveThread.join();

            
            connectionReader.close();
            connectionWriter.close();
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void send(DenoboPacket packet) {
        try {
            connectionWriter.writeObject(packet);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
