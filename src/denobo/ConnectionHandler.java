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
public class ConnectionHandler implements Runnable {

    /**
     * The socket to handle receiving data from.
     */
    private final Socket connection;
    
    /**
     * The BufferedReader object to use for reading data from this connection.
     */
    private BufferedReader connectionReader;
    
    /**
     * The BufferedWriter object to use for sending data through this connection.
     * This is a more efficient way of sending data as multiple send requests
     * are buffered then sent in one batch.
     */
    private BufferedWriter connectionWriter;
    

    /**
     * Creates a ConnectionHandler that will handle receiving data from a
     * socket.
     *
     * @param connection The connection to handle receiving data from
     */
    public ConnectionHandler(Socket connection) {
        this.connection = connection;
        try {
            connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connectionWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = connectionReader.readLine()) != null) {            
            
                // parse line
                
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
                    
        // connection has closed
            
        // remove from portal lists

    }
}
