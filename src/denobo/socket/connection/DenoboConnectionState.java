package denobo.socket.connection;

import denobo.Message;
import denobo.MessageSerializer;

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
     * @param connection    The connection that was established.
     */
    public void handleConnectionEstablished(DenoboConnection connection) {
        
    }
    
    /**
     * Handles a received packet from a connection.
     * 
     * @param connection    The connection that received the packet.
     * @param packet        The packet that was received.
     */
    public void handleReceivedPacket(DenoboConnection connection, Packet packet) {
        
    }
    
    /**
     * Handles a request to send a message to this connected peer. It is useful
     * to override this to prevent messages been sent until authentication has
     * occurred.
     * 
     * @param connection    The connection to send the message to on.
     * @param message       The message to send.
     */
    public void handleSendMessage(DenoboConnection connection, Message message) {
        
        connection.send(new Packet(PacketCode.PROPAGATE, MessageSerializer.serialize(message)));
        
    }
    
    /**
     * A method that allows any subclasses of this class to send packets on a
     * connection without being located in the same class as DenoboConnection
     * since the send(Packet) method for sending packets is protected.
     * 
     * @param connection    The connection to send the packet on.
     * @param packet        The packet to send.
     */
    protected void sendPacket(DenoboConnection connection, Packet packet) {
        
        connection.send(packet);
        
    }
}
