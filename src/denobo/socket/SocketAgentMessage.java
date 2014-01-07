package denobo.socket;

import denobo.socket.connection.DenoboConnection;
import denobo.Message;
import denobo.MessageWrapperType;

/**
 * A wrapper class for a Message that we received from a DenoboConnection instance.
 * This will be used to hold the DenoboConnection we received the message from
 * so we know who not to broadcast it to.
 * 
 * @author Alex Mullen
 */
public class SocketAgentMessage extends Message {

    /**
     * The DenoboConnection instance we received this Message from.
     */
    private final DenoboConnection receivedFrom;
    
    /**
     * Creates a SocketAgentMessage.
     * 
     * @param receivedFrom  the DenoboConnection we received the Message from
     * @param message       the Message we received
     */
    public SocketAgentMessage(DenoboConnection receivedFrom, Message message) {
        super(message);
        this.receivedFrom = receivedFrom;
        this.wrapperType = MessageWrapperType.SOCKET_MESSAGE;
    }
    
    /**
     * Returns the DenoboConnection instance we received this Message from.
     * 
     * @return the DenoboConnection instance
     */
    public DenoboConnection getReceivedFrom() {
        return receivedFrom;
    }
}
