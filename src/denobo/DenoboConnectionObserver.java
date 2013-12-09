package denobo;

/**
 * Implemented by entities that wish to monitor a DenoboConnection.
 * 
 * @author Alex Mullen, Saul Johnson
 */
public interface DenoboConnectionObserver {
    
    /**
     * Invoked when a DenoboConnection has passed authentication.
     * 
     * @param connection The DenoboConnection that passed authentication
     */
    public void connectionAuthenticated(DenoboConnection connection);
    
    /**
     * Invoked when a message was received from a DenoboConnection.
     * 
     * @param connection    the DenoboConnection we received the message from
     * @param packet        the message
     */
    public void messageReceived(DenoboConnection connection, DenoboPacket packet);
    
}
