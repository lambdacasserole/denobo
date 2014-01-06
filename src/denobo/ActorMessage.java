package denobo;

/**
 * A wrapper class for a {@link Message} that we received from an {@link Actor}
 * instance.
 * <p>
 * This will be used to hold the Actor we received the message from so we know
 * who not to broadcast it to.
 * 
 * @author Alex Mullen, Saul Johnson
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
     * Initialises a new instance of an ActorMessage.
     * 
     * @param receivedFrom  the Actor we received the Message from
     * @param message       the Message we received
     */
    public ActorMessage(Actor receivedFrom, Message message) {
        super(message.getId(), message.getFrom(), message.getRecipients(), message.getData());
        this.receivedFrom = receivedFrom;
        this.rawMessage = message;
        this.wrapperType = MessageWrapperType.ACTOR_MESSAGE;
    }
    
    /**
     * Returns the {@link Actor} instance we received this {@link Message} from.
     * 
     * @return  the Actor instance
     */
    public Actor getReceivedFrom() {
        return receivedFrom;
    }
    
    /**
     * Returns the raw un-wrapped Message instance.
     * 
     * @return  the un-wrapped Message instance
     */
    public Message getRawMessage() {
        return rawMessage;
    }
    
}
