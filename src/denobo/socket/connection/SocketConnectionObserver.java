package denobo.socket.connection;

import denobo.Message;

/**
 * Implemented by entities that wish to observe a {@link SocketConnection} for incoming data.
 * 
 * @author Alex Mullen, Saul Johnson
 */
public interface SocketConnectionObserver {
    
    /**
     * Invoked when a {@link SocketConnection} has passed authentication.
     * 
     * @param connection    the {@link SocketConnection} that passed authentication
     */
    public void connectionAuthenticated(SocketConnection connection);
    
    /**
     * Invoked when a {@link SocketConnection} has been disconnected or connection
     * was lost.
     * 
     * @param connection    the {@link SocketConnection} that connection was lost to
     */
    public void connectionShutdown(SocketConnection connection);
    
    
    /**
     * Invoked when a message was received from a {@link SocketConnection}.
     * 
     * @param connection    the {@link SocketConnection} we received the message from
     * @param message       the message received
     */
    public void messageReceived(SocketConnection connection, Message message);
}
