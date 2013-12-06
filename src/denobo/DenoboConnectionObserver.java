package denobo;

/**
 *
 * @author Alex
 */
public interface DenoboConnectionObserver {
    
    /**
     * Invoked when a DenoboConnection has passed authentication.
     * @param connection The DenoboConnection that passed authentication
     */
    public void onAuthenticated(DenoboConnection connection);
    
    /**
     * Invoked when a message was received from a DenoboConnection.
     * @param connection The DenoboConnection we received the message from
     * @param message The message
     */
    public void onReceivedMessage(DenoboConnection connection, String message);
}
