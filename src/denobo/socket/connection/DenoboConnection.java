package denobo.socket.connection;

import denobo.socket.connection.state.DenoboConnectionState;
import denobo.Message;
import denobo.MessageSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a bidirectional communication line between two {@link NetworkPortal} objects.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DenoboConnection {

    /**
     * Holds the socket used to send and receive data.
     */
    private final Socket connection;
    
    /**
     * The observers that we notify in response to connection events.
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
    private volatile boolean disconnected;
    
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
     * Holds the packetSerializer used to read and write to and from this connection.
     */
    private final PacketSerializer packetSerializer;
    
    /**
     * The current state of this DenoboConnection.
     */
    private DenoboConnectionState state;

    /**
     * Creates a {@link DenoboConnection} that will handle receiving data from a socket.
     *
     * @param connection    the connection to handle receiving data from
     * @param initialState  the initial state this connection will be in
     */
    public DenoboConnection(Socket connection, DenoboConnectionState initialState) {
        
        // Store reference to socket and initialise observer list.
        this.connection = connection;
        this.state = initialState;
        this.observers = new CopyOnWriteArrayList<>();
     
        // PacketSerializer to be used for message serialization and packet I/O.
        packetSerializer = new DenoboPacketSerializer();
        
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
     * Adds an observer to the list of observers to be notified of events.
     * 
     * @param observer  the observer to add
     * @return          true if it was successfully added to he list of observers, otherwise false
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
     * Returns all the observers watching this DenoboConnection.
     * 
     * @return      The list of observers
     */
    public List<DenoboConnectionObserver> getObservers() {
        return observers;
    }

    /**
     * Returns the port number the remote peer is using to connect to us on.
     * 
     * @return      The port number
     */
    public int getRemotePort() {
        return connection.getPort();
    }
    
    /**
     * Returns the remote IP address of the remote peer for this connection.
     * 
     * @return  the remote IP address of the remote peer for this connection
     */
    public String getRemoteAddress() {
        return connection.getInetAddress().getHostAddress();
    }

    /**
     * Sets the current state handler for this connection.
     * 
     * @param newState      The new state for this connection
     */
    public void setState(DenoboConnectionState newState) {
        this.state = newState;
    }
    
    /**
     * Starts waiting to receive data through this connection.
     */
    public void startRecieveThread() {
        
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveLoop();
            }
        });
        receiveThread.start();
        
    }

    /**
     * The loop that will wait for any data to be received and delegating the
     * received data to be processed.
     */
    private void receiveLoop() {                
        
        System.out.println("Waiting for data on port [" + connection.getPort() + "]...");

        state.handleConnectionEstablished(this);
        
        try {
            
            while (!disconnected) {

                // Read a packet
                final Packet nextPacket = packetSerializer.readPacket(connectionReader);
                if (nextPacket == null) { 
                    break;
                }
                
                state.handleReceivedPacket(this, nextPacket);
                
            }
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }
        
        // Connection has closed, clean up.
        disconnect();

    }
    
    /**
     * Disconnects and frees up any resources used by this DenoboConnection.
     */
    public void disconnect() {

        // TODO: Make this method block until it is completely finished.
        
        // If we're already disconnected, don't try again. (Not purely thread safe)
        if (disconnected) { return; }

        // Specify that we're now disconnected.
        disconnected = true;

        try {

            // Close I/O streams.

            // commented out this because for some reason it causes the program to hang.
            //connectionReader.close();
            connectionWriter.close();

            // Close the socket to the connection which will cause an exception
            // to be thrown by receiveThread.
            connection.close();

            // If the thread executing this isn't the receiveThread, block and
            // wait for the receiveThread to finish executing
            if (Thread.currentThread() != receiveThread) {
                // Wait for the receive thread to terminate.
                receiveThread.join();
            }

        } catch (IOException | InterruptedException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
        }

        // Notify any observers that this connection has been shut down.
        for (DenoboConnectionObserver currentObserver : observers) {
            currentObserver.connectionShutdown(this);
        }  

    }
    
    /**
     * Sends a message over this connection.
     * 
     * @param message   the message to send 
     */
    public void send(Message message) {

        send(new Packet(PacketCode.PROPAGATE, MessageSerializer.serialize(message)));
        
    }
    
    /**
     * Sends a packet over this connection.
     * @param packet    the packet to send
     */
    public void send(Packet packet) {
        
        System.out.println("Writing data to port [" + connection.getPort() + "]...");
        
        packetSerializer.writePacket(connectionWriter, packet);
        
    }
}
