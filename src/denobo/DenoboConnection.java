package denobo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
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
     * The observer that we notify whenever something occurs.
     */
    private final DenoboConnectionObserver observer;
    
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
     * The BufferedWriter object to use for sending data through this
     * connection. This is a more efficient way of sending data as multiple send
     * requests are buffered then sent in one batch.
     */
    private BufferedWriter connectionWriter;

    
    
    /**
     * Creates a ConnectionHandler that will handle receiving data from a
     * socket.
     *
     * @param connection The connection to handle receiving data from
     */
    public DenoboConnection(Socket connection, DenoboConnectionObserver observer) {
        this.connection = connection;
        this.observer = observer;
        
        try {
            connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connectionWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        disconnected = false;
        receiveThread = new Thread(this);
        receiveThread.start();
    }

    @Override
    public void run() {
        try {
            
            while (!disconnected) {
                final String line = connectionReader.readLine();
                if (line == null) {
                    break;
                }
                
                // parse line

                // let the observer deal with it
                observer.onReceivedMessage(this, line);

            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // connection has closed

        // cleanup
        disconnect();

        // remove from portal lists

    }
    
    public void send() {
        
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
}
