package denobo.socket.connection;

import denobo.Message;
import denobo.socket.SocketAgent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Represents a bidirectional communication line between two {@link SocketAgent} objects.
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
     * The {@link BufferedReader} object to use for efficiently reading any data we have
     * received from this connection.
     */
    private final BufferedReader connectionReader;
    
    /**
     * Holds a {@link BufferedWriter} object for writing to the connection's underlying socket.
     */
    private final BufferedWriter connectionWriter;

    /**
     * Holds the packetSerializer used to read and write to and from this connection.
     */
    private final PacketSerializer packetSerializer;
    
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
     * The current state of this DenoboConnection.
     */
    private DenoboConnectionState state;

    /**
     * The lock object that we use for waiting for a poke reply and notifying
     * when we get the reply.
     */
    private final Object pokeLock;
    
    /**
     * Indicates whether we have send a poke packet and we are expecting a poke 
     * packet back.
     */
    private boolean pokeSent;
    
    /**
     * An indicator to the poke method that we received a poke packet back.
     */
    private boolean pokeReturned;

    
    

    /**
     * Creates a {@link DenoboConnection} that will handle receiving data from a socket.
     *
     * @param connection    the connection to handle receiving data from
     * @param initialState  the initial state this connection will be in
     * @throws IOException  If an IO error occurs whilst setting up the connection.
     */
    public DenoboConnection(Socket connection, DenoboConnectionState initialState) throws IOException {
        
        // Store reference to socket and initialise observer list.
        this.connection = connection;
        this.state = initialState;
        this.observers = new CopyOnWriteArrayList<>();
        this.pokeLock = new Object();
     
        // PacketSerializer to be used for message serialization and packet I/O.
        packetSerializer = new DenoboPacketSerializer();
        
        // Get I/O streams.
        connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        connectionWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        state.handleConnectionEstablished(this);
    }

    @Override
    public String toString() {
        return getLocalAddress() + ":" + getLocalPort() 
                + " ----> "
                + getRemoteAddress() + ":" + getRemotePort();
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
     * @return      The read-only list of observers
     */
    public List<DenoboConnectionObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }
    
    /**
     * Returns the local address this connection is bound to.
     * 
     * @return  The local IP address this connection is bound to.
     */
    public String getLocalAddress() {
        return connection.getLocalAddress().getHostAddress();
    }
    
    /**
     * Returns the local port the connected socket is bound to.
     * 
     * @return  The local port address this connection is bound to.
     */
    public int getLocalPort() {
        return connection.getLocalPort();
    }

    /**
     * Returns the port number the remote peer is using to connect to us on.
     * 
     * @return  The port number
     */
    public int getRemotePort() {
        return connection.getPort();
    }
    
    /**
     * Returns the remote IP address of the remote peer for this connection.
     * 
     * @return  The remote IP address of the remote peer for this connection
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
        
        // Don't bother starting the thread again if there is already one. Not
        // thread safe as there is a race condition but more than one thread
        // shouldn't be executing this anyway.
        if (receiveThread != null) { return; }
        
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

        try {
            
            while (!disconnected) {

                // Read a packet
                final Packet nextPacket = packetSerializer.readPacket(connectionReader);
                if (nextPacket == null) { 
                    break;
                }
                
                // We handle POKE here because they can be handled in any state
                if (nextPacket.getCode() == PacketCode.POKE) {
                    
                    synchronized (pokeLock) {
                        if (pokeSent) {
                            pokeReturned = true;
                            pokeLock.notify();
                        } else {
                            send(new Packet(PacketCode.POKE));
                        }
                    }
                    
                } else {
                
                    state.handleReceivedPacket(this, nextPacket);
                    
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

            // Need to close connectionWriter first as connectionReader causes
            // deadlock if we try to close that first. (Probably to do with some
            // internal lock statement)
            connectionWriter.close();
            connectionReader.close();

            // Close the socket to the connection which will cause an exception
            // to be thrown by receiveThread.
            connection.close();

            // If the thread executing this isn't the receiveThread, block and
            // wait for the receiveThread to finish executing
            if (Thread.currentThread() != receiveThread) {
                // Wait for the receive thread to terminate. Check if receiveThread
                // is null because it can be if startRecieveThread wasn't called
                // because it might not have been needed such as when telling
                // this connection that we can't service them so we firstly
                // tell them then close.
                if (receiveThread != null) { receiveThread.join(); }
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

        state.handleSendMessage(this, message);
        
    }
    
    /**
     * Sends a packet over this connection.
     * @param packet    the packet to send
     */
    protected void send(Packet packet) {
        
        System.out.println("Writing data to port [" + connection.getPort() + "]...");
        try {
            packetSerializer.writePacket(connectionWriter, packet);
        } catch (IOException ex) {
            //disconnect();
        }
        
    }
    
    /**
     * Sends a POKE packet to the remote peer which should reply with one back.
     * This process is measured to give an indicator to how healthy the connection
     * to this peer is. The lower the number, the faster packets get there and
     * the faster they are getting processed.
     * 
     * @param timeout   The maximum time to wait for a reply in milliseconds.
     * @return          The total round time it taken for us to send a packet
     *                  and receive one back in milliseconds.
     * @throws TimeoutException     If we did not receive a reply before the 
     *                              specified timeout.
     */
    public long poke(long timeout) throws TimeoutException {

        synchronized (pokeLock) {
            
            // Save the current time
            final long startTime = System.currentTimeMillis();
            
            // Send a poke packet
            pokeSent = true;
            send(new Packet(PacketCode.POKE));

            // Check if we have recieved a reply poke
            while (!pokeReturned) {
                // Check if we have waited the elapsed time or longer with still
                // no reply.
                if ((System.currentTimeMillis() - startTime) >= timeout) {
                    throw new TimeoutException();
                }

                // Go to sleep until we are notified of a reply
                try {
                    pokeLock.wait(timeout);
                } catch (InterruptedException ex) { }
            }
            
            // Reset variables
            pokeSent = false;
            pokeReturned = false;
            
            // Return how long the process took
            return (System.currentTimeMillis() - startTime);
        }
    }
}
