package denobo;

/**
 * Represents an object passed between actors in a multi-agent system in order
 * to communicate with one another.
 * 
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {

    /**
     * The unique identifier for the message.
     */
    private final String id;

    /**
     * The names of the recipient actors.
     */
    private final String[] recipients;
    
    /**
     * The name of the originating actor.
     */
    private final String from;
    
    /**
     * The message data.
     */
    private final String data;
    
    /**
     * Initialises a new instance of a message.
     * 
     * @param id            the unique identifier for the new message
     * @param from          the name of the originating actor
     * @param recipients    the names of the recipient actors
     * @param data          the message data
     */
    public Message(String id, String from, String[] recipients, String data) {
        this.id = id;
        this.recipients = recipients;
        this.from = from;
        this.data = data;
    }
     
    /**
     * Initialises a new instance of a message.
     * 
     * @param from          the name of the originating actor
     * @param recipients    the names of the recipient actors
     * @param data          the message data
     */
    public Message(String from, String[] recipients, String data) {
        this(UniqueIdFactory.getId(), from, recipients, data);
    }
    
    /**
     * Initialises a new instance of a message.
     * 
     * @param id            the unique identifier for the new message
     * @param from          the name of the originating actor
     * @param recipient     the name of the recipient actor
     * @param data          the message data
     */
    public Message(String id, String from, String recipient, String data) {
        this(id, from, new String[] {recipient}, data);
    }
    
    /**
     * Initialises a new instance of a message.
     * 
     * @param from          the name of the originating actor
     * @param recipient     the name of the recipient actor
     * @param data          the message data
     */
    public Message(String from, String recipient, String data) {
        this(from, new String[] {recipient}, data);
    }

    /**
     * Gets the unique identifier for this message.
     * 
     * @return  the unique identifier for this message
     */
    public final String getId() {
        return id;
    }

    /**
     * Gets the names of the recipient actors.
     * 
     * @return  the names of the recipient actors
     */
    public final String[] getRecipients() {
        return recipients;
    }
    
    /**
     * Gets whether or not the given actor is listed as a recipient for this message.
     * 
     * @param actor the actor to check
     * @return      true if the specified actor is a recipient, otherwise false
     */
    public final boolean hasRecipient(Actor actor) {
        for (String currentRecipient : recipients) {
            // TODO: Decide on whether Actors can have null for a name.
            if (actor.getName().equals(currentRecipient)) { return true; }
        }
        return false;
    }
    
    /**
     * Gets the name of the originating actor.
     * 
     * @return  the name of the originating actor
     */
    public final String getFrom() {
        return from;
    }

    /**
     * Gets the message data or payload.
     * 
     * @return  the message data or payload
     */
    public final String getData() {
        return data;
    }
    
}
