package denobo.socket;

import denobo.socket.connection.SocketConnection;
import denobo.Message;

/**
 * A wrapper class for a Message that we received from a SocketConnection instance.
 * This will be used to hold the SocketConnection we received the message from
 so we know who not to broadcast it to.
 * 
 * @author Alex Mullen
 */
public class SocketAgentMessage extends Message {

    /**
     * The SocketConnection instance we received this Message from.
     */
    private final SocketConnection receivedFrom;
    
    /**
     * Creates a SocketAgentMessage.
     * 
     * @param receivedFrom  The SocketConnection we received the Message from.
     * @param message       The Message we received.
     */
    public SocketAgentMessage(SocketConnection receivedFrom, Message message) {
        super(message.getId(), message.getFrom(), message.getRecipients(), message.getData());
        this.receivedFrom = receivedFrom;
    }
    
    /**
     * Returns the SocketConnection instance we received this Message from.
     * 
     * @return The SocketConnection instance
     */
    public SocketConnection getReceivedFrom() {
        return receivedFrom;
    }
}
