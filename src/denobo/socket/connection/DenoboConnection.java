package denobo.socket.connection;

import denobo.Message;
import denobo.MessageSerializer;
import denobo.socket.SocketAgent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
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
     * An enum of initial states a {@link DenoboConnection} can initially
     * be in.
     */
    public enum InitialState {
        
        /**
         * The state where the remote end of this connection instantiated the
         * connection and we are waiting for them to 'Greet' us first.
         */
        WAIT_FOR_GREETING,
        
        /**
         * The state where we instantiated the connection and it is our
         * responsibility to 'Greet' the remote end first.
         */
        INITIATE_GREETING,
        
        /**
         * The state where the remote end of this connection instantiated the
         * connection but we have reached our connection threshold and we need
         * to gracefully tell them and close the connection.
         */
        TOO_MANY_PEERS
    }

    /**
     * Holds the {@Link Socket} used to send and receive data.
     */
    private final Socket connection;
    
    /**
     * Holds the {@link SocketAgent} that this DenoboConnection belongs to.
     */
    private final SocketAgent parentAgent;
    
    /**
     * The observers that this DenoboConnection will notify in response to 
     * connection events.
     */
    private final List<DenoboConnectionObserver> observers;

    /**
     * The {@link BufferedReader} object to use for efficiently reading any data 
     * we have received from this connection.
     */
    private final BufferedReader connectionReader;
    
    /**
     * Holds a {@link BufferedWriter} object for writing to the connection's 
     * underlying socket.
     */
    private final BufferedWriter connectionWriter;

    /**
     * Holds the {@link PacketSerializer} used to read and write to and from 
     * this connection.
     */
    private final PacketSerializer packetSerializer;
    
    /**
     * A {@link Thread} that handles waiting for data to be received from this 
     * connection.
     */
    private Thread receiveThread;
    
    /**
     * A boolean flag that is used to signal {@link DenoboConnection#receiveThread} 
     * to terminate and prevent any more actions from occurring on this
     * connection.
     */
    private volatile boolean disconnected;
    
    /**
     * The current {@link DenoboConnectionState} of this DenoboConnection.
     */
    private volatile DenoboConnectionState state;
    
    //private Compressor compressor;
    
    /**
     * The lock object that we use for waiting for a poke reply and notifying
     * when we get the reply.
     */
    private final Object pokeLock;
    
    /**
     * Indicates whether this connection has sent a poke packet and we are 
     * expecting a poke packet back.
     */
    private boolean pokeSent;
    
    /**
     * An indicator to the poke method that this connection received a poke 
     * packet back.
     */
    private boolean pokeReturned;

    
    

    /**
     * Creates a {@link DenoboConnection} that will handle receiving data from a socket.
     *
     * @param parent        the SocketAgent instance this connection belongs to
     * @param connection    the connection to handle receiving data from
     * @param initialState  the initial state this connection will be in
     * @throws IOException  if an I/O error occurs whilst setting up the connection
     */
    public DenoboConnection(SocketAgent parent, Socket connection, InitialState initialState) throws IOException {
        
        this.parentAgent = parent;
        this.connection = connection;
        this.observers = new CopyOnWriteArrayList<>();
        this.pokeLock = new Object();
        //this.compressor = new DummyCompressor();
     
        
        switch (initialState) {
            
            case WAIT_FOR_GREETING:
                
                state = new WaitForGreetingState();
                break;
                
            case INITIATE_GREETING:
                
                state = new InitiateGreetingState();
                break;
                
            case TOO_MANY_PEERS:
                
                state = new TooManyPeersState();
                break;
                
            default:
                
                System.out.println("Unknown initial state");
            
        }

        
        // PacketSerializer to be used for message serialization and packet I/O.
        packetSerializer = new DenoboPacketSerializer();
        
        // Get I/O streams.
        connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        connectionWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        state.handleConnectionEstablished();
        
    }

    @Override
    public String toString() {
        return getLocalAddress() + ":" + getLocalPort() 
                + " ----> "
                + getRemoteAddress() + ":" + getRemotePort();
    }
    
    /**
     * Adds an observer to the list of observers to be notified of events for this
     * DenoboConnection.
     * 
     * @param observer  the observer to add
     * @return          true if it was successfully added to he list of observers, 
     *                  otherwise false
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
     * Returns the local address this connection is bound to.
     * 
     * @return the local IP address this connection is bound to
     */
    public String getLocalAddress() {
        return connection.getLocalAddress().getHostAddress();
    }
    
    /**
     * Returns the local port the connected socket in this connection is bound to.
     * 
     * @return the local port address this connection is bound to
     */
    public int getLocalPort() {
        return connection.getLocalPort();
    }

    /**
     * Returns the port number the remote peer is using to connect to this
     * connection on.
     * 
     * @return  the remote port number
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
     * Starts waiting to receive data through this connection.
     */
    public void startRecieveThread() {
        
        /* 
         * Don't bother starting the thread again if there is already one. Not
         * thread safe as there is a race condition but more than one thread
         * shouldn't be executing this anyway.
         */
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
     * The loop that will wait for any data to be received on this connection 
     * and delegates any received data to be processed.
     */
    private void receiveLoop() {                

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
                
                    state.handleReceivedPacket(nextPacket);
                    
                }
                
            }
            
        } catch (StreamCorruptedException ex) {
            
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
            
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

            /*
             * Need to close connectionWriter first as connectionReader causes
             * deadlock if we try to close that first. (Probably to do with some
             * internal lock statement)
             */
            connectionWriter.close();
            connectionReader.close();

            /*
             * Close the socket to the connection which will cause an exception
             * to be thrown by receiveThread.
             */
            connection.close();

            /*
             * If the thread executing this isn't the receiveThread, block and
             * wait for the receiveThread to finish executing
             */
            if (Thread.currentThread() != receiveThread) {
                /* 
                 * Wait for the receive thread to terminate. Check if receiveThread
                 * is null because it can be if startRecieveThread wasn't called
                 * because it might not have been needed such as when telling
                 * this connection that we can't service them so we firstly
                 * tell them then close.
                 */
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
     * @param message the message to send 
     */
    public void send(Message message) {

        state.handleSendMessage(message);
        
    }
    
    /**
     * Sends a packet over this connection.
     * @param packet    the packet to send
     */
    protected void send(Packet packet) {

        try {

            packetSerializer.writePacket(connectionWriter, packet);
            
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            
        }
        
    }
    
    /**
     * Sends a POKE packet to the remote peer connected to this connection which 
     * should reply with one back.
     * <p>
     * This process is measured to give an indicator to how healthy the connection
     * to this peer is. The lower the number, the faster packets get there and
     * the faster they are getting processed.
     * 
     * @param timeout   the maximum time to wait for a reply in milliseconds.
     * @return          the total round time it taken for us to send a packet
     *                  and receive one back in milliseconds.
     * @throws TimeoutException     if we did not receive a reply before the 
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
                /*
                 * Check if we have waited the elapsed time or longer with still
                 * no reply.
                 */
                if ((System.currentTimeMillis() - startTime) >= timeout) {
                    throw new TimeoutException();
                }

                // Go to sleep until we are notified of a reply
                try {
                    pokeLock.wait(timeout);
                } catch (InterruptedException ex) { 
                    System.out.println(ex.getMessage());
                }
            }
            
            // Reset variables
            pokeSent = false;
            pokeReturned = false;
            
            // Return how long the process took
            return (System.currentTimeMillis() - startTime);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Represents a state that a DenoboConnection can be in.
     *
     * @author Alex Mullen
     */
    public abstract class DenoboConnectionState {
    
        /**
         * Handles the moments after the connection has been established and
         * communication can begin.
         * 
         */
        public void handleConnectionEstablished() {

        }

        /**
         * Handles a received packet from a connection.
         * 
         * @param packet the packet that was received
         */
        public void handleReceivedPacket(Packet packet) {

        }

        /**
         * Handles a request to send a message to this connected peer. 
         * <p>
         * It is useful to override this to prevent messages been sent until 
         * authentication has occurred.
         * 
         * @param message the message to send
         */
        public void handleSendMessage(Message message) {

            send(new Packet(PacketCode.PROPAGATE, MessageSerializer.serialize(message)));

        }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
        
    /**
     * This represents the state a connection in when a peer has connected to us 
     * but them connecting to us has resulting in us exceeding our maximum peer limit so
     * we'll gracefully accept their connection request and tell them that we we've
     * reached the peer limit.
     *
     * @author Alex Mullen
     */
    public class TooManyPeersState extends DenoboConnectionState {

        @Override
        public void handleConnectionEstablished() {

            send(new Packet(PacketCode.TOO_MANY_PEERS));
            disconnect();
            
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This represents the state a connection is in when we have have initialized a
     * connection to another peer. 
     * <p>
     * It is our responsibility to send a GREETINGS packet to them to initiate 
     * a session.
     *
     * @author Alex Mullen
     */
    public class InitiateGreetingState extends DenoboConnectionState {

        @Override
        public void handleConnectionEstablished() {

            System.out.println("sending a GREETINGS packet to " + getRemoteAddress()
                                + ":" + getRemotePort());

            send(new Packet(PacketCode.GREETINGS));
            
        }

        @Override
        public void handleSendMessage(Message message) {

            // Don't send messages to this peer until authentication has been performed

        }

        @Override
        public void handleReceivedPacket(Packet packet) {

            // Process packet according to status code.
            switch(packet.getCode()) {

                case ACCEPTED:

                    System.out.println(getRemoteAddress()
                        + ":" + getRemotePort() + " has accepted our "
                            + "GREETINGS request");

                    state = new AuthenticatedState();
                    break;

                case CREDENTIALS_PLZ:

                    System.out.println(getRemoteAddress()
                        + ":" + getRemotePort() + " is asking for credentials...");
                    
                    
                    // Ask/retrieve the credentials to use
                    final DenoboConnectionCredentials credentials = 
                            parentAgent.getConfiguration().credentialsHandler.credentialsRequested(DenoboConnection.this);
                    
                    if (credentials == null) {
                        send(new Packet(PacketCode.NO_CREDENTIALS));
                        disconnect();
                    } else {
                        send(new Packet(PacketCode.CREDENTIALS, credentials.getPassword()));                    
                        state = new AwaitingAuthenticationState();
                    }
                    
                    break;


                    /*
                     * All these mean we need to disconnect anyway so just let them
                     * fall through to the default.
                     */

                case TOO_MANY_PEERS:  

                    System.out.println(getRemoteAddress()
                        + ":" + getRemotePort() + " has declined our connection"
                            + " request because it has reached its peer limit.");
                    break;

                case NO:
                    
                case NOT_A_SERVER:
                    
                default:

                    // TODO: Bad status code that we weren't expecting.
                    disconnect();
                    
            }
            
        }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This represents the state a connection is in when the other end of the
     * connection initiated the connection. 
     * <p>
     * This side of the connection needs to wait for a GREETINGS packet from the 
     * other end before a session is allowed.
     *
     * @author Alex Mullen
     */
    public class WaitForGreetingState extends DenoboConnectionState {

        @Override
        public void handleSendMessage(Message message) {

            // Don't send messages to this peer until authentication has been performed

        }

        @Override
        public void handleReceivedPacket(Packet packet) {

            // Process packet according to status code.
            switch(packet.getCode()) {

                case GREETINGS:
                    System.out.println(getRemoteAddress()
                        + ":" + getRemotePort() + " has sent us a GREETINGS packet");
                    
                    String password = parentAgent.getConfiguration().password;
                    if ((password != null) && (!password.isEmpty())) {
                        send(new Packet(PacketCode.CREDENTIALS_PLZ));
                        state = new WaitingForCredentialsState();
                    } else {
                        send(new Packet(PacketCode.ACCEPTED));
                        state = new AuthenticatedState();
                    }
                    break;

                default:

                    // TODO: Bad status code that we weren't expecting.
                    disconnect();
                    
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This represents the state a connection is in when the other end of the
     * connection initiated the connection and we need some valid credentials
     * before we will let them proceed any further.
     * 
     * @author Alex Mullen
     */
    public class WaitingForCredentialsState extends DenoboConnectionState {
        
        @Override
        public void handleSendMessage(Message message) {
            
            // Don't send messages to this peer until authentication has been performed
            
        }

        @Override
        public void handleReceivedPacket(Packet packet) {
            
            switch (packet.getCode()) {
                
                case CREDENTIALS:

                    // Check password
                    if (packet.getBody().equals(parentAgent.getConfiguration().password)) {
                        send(new Packet(PacketCode.ACCEPTED));
                        state = new AuthenticatedState();
                    } else {
                        send(new Packet(PacketCode.BAD_CREDENTIALS));
                        disconnect();
                    }
                    break;
                
                default:
                    
                    // TODO: Bad status code that we weren't expecting.
                    disconnect();
                    
            }
            
        }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This represents the state where the peer who initiated the connection has
     * submitted some credentials as a reply to a CREDENTIALS_PLZ packet and is
     * waiting for authentication.
     * 
     * @author Alex Mullen
     */
    public class AwaitingAuthenticationState extends DenoboConnectionState {
        
        @Override
        public void handleSendMessage(Message message) {
            
            // Don't send messages to this peer until authentication has been performed
            
        }

        @Override
        public void handleReceivedPacket(Packet packet) {
            
            switch (packet.getCode()) {
                
                case ACCEPTED:
                    
                    System.out.println("the credentials we sent were accepted");
                    state = new AuthenticatedState();
                    break;
                    
                    /*
                     * All these mean we need to disconnect anyway so just let them
                     * fall through to the default.
                     */
                    
                case BAD_CREDENTIALS:
                    
                    System.out.println("we sent some bad credentials");
                    
                case NO:

                default:
                    
                    disconnect();
                
            }

        }      

    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This represents the state of a connection has completed the hand-shake.
     *
     * @author Alex Mullen
     */
    public class AuthenticatedState extends DenoboConnectionState {

        /**
         * Creates a new AuthenticatedState instance then notifies the outer
         * DenoboConnection's observers that this connection has passed
         * authentication.
         */
        public AuthenticatedState() {
            
            for (DenoboConnectionObserver currentObserver : observers) {
                currentObserver.connectionAuthenticated(DenoboConnection.this);
            }
                    
        }
        
        
        @Override
        public void handleReceivedPacket(Packet packet) {

            // Process packet according to status code.
            switch(packet.getCode()) {

                case PROPAGATE:

                    final Message deserializedMessage = MessageSerializer.deserialize(packet.getBody());
                    for (DenoboConnectionObserver currentObserver : observers) {
                        currentObserver.messageReceived(DenoboConnection.this, deserializedMessage); 
                    }
                    break;

                default:

                    // TODO: Bad status code that we weren't expecting.
                    disconnect();
                    
            }
        }
    }    
    
    ////////////////////////////////////////////////////////////////////////////

}
