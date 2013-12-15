package denobo;

/**
 * A wrapper class for a Message that we received from a Actor instance.
 * This will be used to hold the Actor we received the message from
 * so we know who not to broadcast it to.
 * 
 * @author Alex Mullen
 */
public class ActorMessage extends Message {

    /**
     * The Actor instance we received this Message from.
     */
    private final Actor receivedFrom;
    
    /**
     * The raw un-wrapped Message.
     */
    private final Message rawMessage;
    
    /**
     * Creates an ActorMessage.
     * 
     * @param receivedFrom  The Actor we received the Message from.
     * @param message       The Message we received.
     */
    public ActorMessage(Actor receivedFrom, Message message) {
        super(message.getId(), message.getFrom(), message.getRecipients(), message.getData());
        this.receivedFrom = receivedFrom;
        this.rawMessage = message;
    }
    
    /**
     * Returns the Actor instance we received this Message from.
     * 
     * @return The Actor instance
     */
    public Actor getReceivedFrom() {
        return receivedFrom;
    }
    
    /**
     * Returns the raw un-wrapped Message instance.
     * 
     * @return The un-wrapped Message instance.
     */
    public Message getRawMessage() {
        return rawMessage;
    }
}
