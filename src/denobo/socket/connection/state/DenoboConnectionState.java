package denobo.socket.connection.state;

import denobo.socket.connection.SocketConnection;
import denobo.socket.connection.Packet;

/**
 * Represents a state that a SocketConnection can be in.
 *
 * @author Alex Mullen
 */
public abstract class DenoboConnectionState {
    
    /**
     * Handles the moments after the connection has been established and
     * communication can begin.
     * 
     * @param connection    The connection that was established.
     */
    public void handleConnectionEstablished(SocketConnection connection) {
        
    }
    
    /**
     * Handles a received packet from a connection.
     * 
     * @param connection    The connection that received the packet.
     * @param packet        The packet that was received.
     */
    public abstract void handleReceivedPacket(SocketConnection connection, Packet packet);
}
