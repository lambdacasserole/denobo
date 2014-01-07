package denobo;

/**
 * Represents a message passed between Actors in a multi-agent system in order
 * to communicate with one another.
 * 
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {

    /**
     * The unique identifier for the Message.
     */
    private final String id;

    /**
     * The names of the recipient Actors.
     */
    private final String[] recipients;
    
    /**
     * The name of the originating Actor.
     */
    private final String from;
    
    /**
     * The Message data.
     */
    private final String data;
    
    /**
     * The type of Message wrapper this instance represents, if any.
     */
    protected MessageWrapperType wrapperType;
    
    /**
     * Initialises a new instance of a Message.
     * 
     * @param id            the unique identifier for the new Message
     * @param from          the name of the originating Actor
     * @param recipients    the names of the recipient Actors
     * @param data          the Message data
     */
    public Message(String id, String from, String[] recipients, String data) {
        this.id = id;
        this.recipients = recipients;
        this.from = from;
        this.data = data;
        this.wrapperType = MessageWrapperType.RAW;
    }
     
    /**
     * Initialises a new instance of a Message.
     * 
     * @param from          the name of the originating Actor
     * @param recipients    the names of the recipient Actors
     * @param data          the Message data
     */
    public Message(String from, String[] recipients, String data) {
        this(UniqueIdFactory.getId(), from, recipients, data);
    }
    
    /**
     * Initialises a new instance of a Message.
     * 
     * @param id            the unique identifier for the new Message
     * @param from          the name of the originating Actor
     * @param recipient     the name of the recipient Actor
     * @param data          the message data
     */
    public Message(String id, String from, String recipient, String data) {
        this(id, from, new String[] {recipient}, data);
    }
    
    /**
     * Initialises a new instance of a Message.
     * 
     * @param from          the name of the originating Actor
     * @param recipient     the name of the recipient Actor
     * @param data          the Message data
     */
    public Message(String from, String recipient, String data) {
        this(from, new String[] {recipient}, data);
    }
    
    /**
     * Initialises a new instance of a Message that is a clone of the given
     * Message.
     * 
     * @param message       the Message instance to clone.
     */
    public Message(Message message) {
        // TODO: This doesn't copy the wrapperType. Every message cloned will be
        // of type RAW. Decide on if we want this behaviour.
        this(message.getId(), message.getFrom(), message.getRecipients(), message.getData());
    }

    /* ---------- */
    
    /**
     * Gets the unique identifier for this Message.
     * 
     * @return  the unique identifier for this Message
     */
    public final String getId() {
        return id;
    }

    /**
     * Gets the names of the recipient Actors.
     * 
     * @return  the names of the recipient Actors
     */
    public final String[] getRecipients() {
        return recipients;
    }
    
    /**
     * Gets whether or not the given Actor is listed as a recipient for this 
     * Message.
     * 
     * @param actor the Actor to check
     * @return      true if the specified Actor is a recipient, otherwise false
     */
    public final boolean hasRecipient(Actor actor) {
        for (String currentRecipient : recipients) {
            if (actor.getName().equals(currentRecipient)) { return true; }
        }
        return false;
    }
    
    /**
     * Gets the name of the originating Actor.
     * 
     * @return  the name of the originating Actor
     */
    public final String getFrom() {
        return from;
    }

    /**
     * Gets the Message data or payload.
     * 
     * @return  the Message data or payload
     */
    public final String getData() {
        return data;
    }
    
    /**
     * Gets the type of Message wrapper this instance represents, if any.
     * 
     * @return  the type of Message wrapper this instance represents, if any
     */
    public final MessageWrapperType getWrapperType() {
        return wrapperType;
    }
    
}
