package denobo.socket.connection;

import denobo.Message;

/**
 * Implemented by entities that wish to observe a {@link DenoboConnection}.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface DenoboConnectionObserver {
    
    /**
     * Invoked when a {@link DenoboConnection} has passed authentication.
     * 
     * @param connection    the {@link DenoboConnection} that passed authentication
     */
    public void connectionAuthenticated(DenoboConnection connection);
    
    /**
     * Invoked when a {@link DenoboConnection} has been disconnected or connection
     * was lost to it.
     * 
     * @param connection    the {@link DenoboConnection} that connection was lost to
     */
    public void connectionShutdown(DenoboConnection connection);
    
    
    /**
     * Invoked when a message was received from a {@link DenoboConnection}.
     * 
     * @param connection    the {@link DenoboConnection} we received the message from
     * @param message       the message received
     */
    public void messageReceived(DenoboConnection connection, Message message);
    
}
