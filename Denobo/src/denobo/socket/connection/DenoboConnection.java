 package denobo.socket.connection;

import denobo.Message;
import denobo.QueryString;
import denobo.Route;
import denobo.compression.Compressor;
import denobo.crypto.DiffieHellmanKeyGenerator;
import denobo.crypto.RC4Drop4096CryptoAlgorithm;
import denobo.socket.SocketAgent;
import denobo.socket.connection.state.DenoboConnectionState;
import denobo.socket.connection.state.client.InitiateGreetingState;
import denobo.socket.connection.state.server.TooManyPeersState;
import denobo.socket.connection.state.server.WaitForGreetingState;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Represents a bidirectional communication line between two {@link SocketAgent} 
 * instances.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DenoboConnection {

    /**
     * An enum of initial states a {@link DenoboConnection} can initially be in.
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
     * Holds the name of the remote SocketAgent on the other end of this
     * connection.
     * <p>
     * This is only acquired after authentication.
     */
    private String remoteAgentName;
    
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
     * The {@link Reader} object to use for efficiently reading any data 
     * we have received from this connection.
     */
    private final Reader connectionReader;
    
    /**
     * Holds a {@link Writer} object for writing to the connection's 
     * underlying socket.
     */
    private final Writer connectionWriter;

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
     * The private key for establishing secure sessions over this connection.
     */
    private final BigInteger privateKey;
    
    /**
     * The public key for establishing secure sessions over this connection.
     */
    private final BigInteger publicKey;
    
    /**
     * The shared key for encrypting transmitted packets once a secure session
     * has been established over this connection.
     */
    private BigInteger sharedKey;
    
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
    
    
    /* ---------- */
    
    
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
     
        switch (initialState) {
            
            case WAIT_FOR_GREETING:
                
                state = new WaitForGreetingState(this);
                break;
                
            case INITIATE_GREETING:
                
                state = new InitiateGreetingState(this);
                break;
                
            case TOO_MANY_PEERS:
                
                state = new TooManyPeersState(this);
                break;
                
            default:
                
                System.out.println("Initial state is invalid.");
                break;
                
        }

        
        // Serialization to be used for message serialization and packet I/O.
        packetSerializer = new DenoboPacketSerializer();
        
        // Get I/O streams.
        connectionReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        connectionWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        
        // Pre-generate a public/private key pair for this agent.
        privateKey = DiffieHellmanKeyGenerator.generatePrivateKey();
        publicKey = DiffieHellmanKeyGenerator.generatePublicKey(privateKey);
        
        state.handleConnectionEstablished();
        
    }
    
    /**
     * Adds an observer to the list of observers to be notified of events for this
     * DenoboConnection.
     * 
     * @param observer  the observer to add
     * @return          true if the observer was successfully added, otherwise 
     *                  false
     */
    public boolean addObserver(DenoboConnectionObserver observer) {
        return observers.add(Objects.requireNonNull(observer, "The observer to "
                + "add cannot be null."));
    }
    
    /**
     * Removes an observer from the list of observers for this DenoboConnection.
     * 
     * @param observer  the observer to remove
     * @return          true if the observer to remove was found and removed, 
     *                  otherwise false
     */
    public boolean removeObserver(DenoboConnectionObserver observer) {
        return observers.remove(Objects.requireNonNull(observer, "The observer to "
                + "remove cannot be null."));
    }
    
    /**
     * Returns the parent SocketAgent instance that this DenoboConnection
     * instance belongs to.
     * 
     * @return the SocketAgent instance
     */
    public SocketAgent getParentAgent() {
        return parentAgent;
    }
    
    /**
     * Gets the private key used for establishing secure sessions over this 
     * connection.
     * 
     * @return  the large integer that represents this connection's private key
     */
    public BigInteger getPrivateKey() {
        return privateKey;
    }
    
    /**
     * Gets the public key used for establishing secure sessions over this 
     * connection.
     * 
     * @return  the large integer that represents this connection's public key
     */
    public BigInteger getPublicKey() {
        return publicKey;
    }
    
    /**
     * Gets the shared key for encrypting transmitted packets once a secure 
     * session has been established over this connection.
     * 
     * @return  the large integer that represents the key that this connection
     *          shares with its remote peer
     */
    public BigInteger getSharedKey() {
        return sharedKey;
    }
    
    /**
     * Sets the shared key for encrypting transmitted packets once a secure 
     * session has been established over this connection.
     * <p>
     * Note that this method will update the key on this side of the connection
     * only. Calling this method on one side of the connection alone may result
     * in the shared secret becoming lost and cause an unstable session state.
     * 
     * @param sharedKey the large integer to use for encrypting transmitted 
     *                  packets 
     */
    public void setSharedKey(BigInteger sharedKey) {
        this.sharedKey = sharedKey;
        final RC4Drop4096CryptoAlgorithm crypto = new RC4Drop4096CryptoAlgorithm();
        crypto.setKey(DiffieHellmanKeyGenerator.generateEncryptionKey(sharedKey));
        packetSerializer.setCryptoAlgorithm(crypto);
    }
    
    /**
     * Sets the compressor used for compressing transmitted packets.
     * 
     * @param compressor    the compressor to use for compressing transmitted
     *                      packets.
     */
    public void setCompressor(Compressor compressor) {
        System.out.println("Switching to compressor: " + compressor.getName());
        packetSerializer.setCompressor(compressor);
    }
    
    /**
     * Removes all observers from this DenoboConnection.
     */
    public void removeObservers() {
        observers.clear();
    }
    
    /**
     * Returns a read-only view of all observers that are currently attached to
     * this DenoboConnection instance.
     * 
     * @return the read-only list of DenoboConnectionObserver instances
     */
    public List<DenoboConnectionObserver> getObservers() {
        return Collections.unmodifiableList(observers);
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
     * Returns the name of the SocketAgent on the remote end of this connection.
     * 
     * @return  the name or null if authentication hasn't occurred for this
     *          connection at the time of this call.
     */
    public String getRemoteAgentName() {
        return remoteAgentName;
    }

    /**
     * Sets the name of the SocketAgent on the remote end of this connection.
     * 
     * @param remoteAgentName the name
     */
    public void setRemoteAgentName(String remoteAgentName) {
        this.remoteAgentName = remoteAgentName;
    }
    
    
    /**
     * Sets the state of this connection.
     * 
     * @param state the new state of the connection
     */
    public void setState(DenoboConnectionState state) {
        this.state = state;
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

                state.handleReceivedPacket(nextPacket);

            }
            
        } catch (StreamCorruptedException ex) {
            
            System.out.println("The stream was corrupted whilst receiving: " + ex.getMessage());
            
        } catch (IOException ex) {
            
            System.out.println("I/O exception whilst receving: " + ex.getMessage());
            
        }
        
        // Connection has closed, clean up.
        disconnect();

    }
    
    /**
     * Disconnects and frees up any resources used by this DenoboConnection.
     */
    public void disconnect() {

        // TODO: Make this method block until it is completely finished.
        
        // If we're already disconnected, don't try again.
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
            
            System.out.println("Exception thrown whilst shutting down connection: " 
                    + ex.getMessage());
            
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
     * 
     * @param packet    the packet to send
     */
    public void send(Packet packet) {

        try {
            synchronized (packetSerializer) {
                packetSerializer.writePacket(connectionWriter, packet);
            }
        } catch (IOException ex) {
            // TODO: Handle exception.
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

        return state.handleSendPoke(timeout);
        
    }
    
    /**
     * Sends a request to this remote agent to try and find a route to the specified
     * agent.
     * 
     * @param destinationAgentName  the name of the agent to route to
     * @param localRoute            the local route taken to reach this SocketAgent
     *                              instance
     * @param usesBacktracking      whether or not to use backtracking if a
     *                              route is found
     * @see                         denobo.RoutingWorker#getUsesBacktracking
     */
    public void routeToRemote(String destinationAgentName, Route localRoute, boolean usesBacktracking) {
        
        /* 
         * Pass the destination agent name and the route we have so far to the
         * remote agent.
         */
        final QueryString query = new QueryString();
        query.add("to", destinationAgentName);
        query.add("localroute", localRoute.serialize());
        query.addAsBoolean("backtracks", usesBacktracking);
        
        send(new Packet(PacketCode.ROUTE_TO, query.toString()));
        
    }
    
    /**
     * Tells this remote agent to invalidate any routing table entries containing
     * the specified two agents.
     * 
     * @param invalidatedAgentNames a list of agent names that have been invalidated
     * @param visitedNodes  a set of Agent names that have already had their
     *                      routing tables updated
     */
    public void invalidateRemote(List<String> invalidatedAgentNames, Set<String> visitedNodes) {
        
        final QueryString query = new QueryString();

        query.addAsCollection("invalidatedagents", invalidatedAgentNames);
        query.addAsCollection("visitedagents", visitedNodes);

        send(new Packet(PacketCode.INVALIDATE_AGENTS, query.toString()));
        
    }
    
    @Override
    public String toString() {
        return getLocalAddress() + ":" + getLocalPort() 
                + " ----> "
                + getRemoteAddress() + ":" + getRemotePort();
    }

}
