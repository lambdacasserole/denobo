package denobo;

/**
 * Implemented by entities that wish to observe a {@link DenoboConnection} for incoming data.
 * 
 * @author Alex Mullen, Saul Johnson
 */
public interface DenoboConnectionObserver {
    
    /**
     * Invoked when a {@link DenoboConnection} has passed authentication.
     * 
     * @param connection    the {@link DenoboConnection} that passed authentication
     */
    public void connectionAuthenticated(DenoboConnection connection);
    
    /**
     * Invoked when a message was received from a {@link DenoboConnection}.
     * 
     * @param connection    the {@link DenoboConnection} we received the message from
     * @param packet        the message received
     */
    public void messageReceived(DenoboConnection connection, DenoboPacket packet);
    
}
