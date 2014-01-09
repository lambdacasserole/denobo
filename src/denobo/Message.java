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
     * The name of the recipient Actor.
     */
    private final String recipient;
    
    /**
     * The name of the originating Actor.
     */
    private final String originator;
    
    /**
     * The route this message should take to reach its destination Actor.
     */
    private final RoutingQueue route;
    
    /**
     * The Message data.
     */
    private final String data;
    
    /**
     * Initialises a new instance of a Message.
     * 
     * @param id            the unique identifier for the new Message
     * @param route         the route this message should take to reach its 
     *                      destination Actor
     * @param data          the Message data
     */
    public Message(String id, RoutingQueue route, String data) {
        this.id = id;
        this.route = route;
        this.recipient = route.peekEnd();
        this.originator = route.peekStart();
        this.data = data;
    }
     
    /**
     * Initialises a new instance of a Message.
     * 
     * @param route         the route this message should take to reach its 
     *                      destination Actor
     * @param data          the Message data
     */
    public Message(RoutingQueue route, String data) {
        this(UniqueIdFactory.getId(), route, data);
    }
    
    /**
     * Initialises a new instance of a Message that is a clone of the given
     * Message.
     * 
     * @param message   the Message instance to clone.
     */
    public Message(Message message) {
        this(message.getId(), message.getRoute(), message.getData());
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
    public final String getRecipient() {
        return recipient;
    }
    
    /**
     * Gets the name of the originating Actor.
     * 
     * @return  the name of the originating Actor
     */
    public final String getOriginator() {
        return originator;
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
     * Gets the route this message should take to reach its destination Actor.
     * 
     * @return  the route this message should take to reach its destination 
     *          Actor
     */
    public final RoutingQueue getRoute() {
        return route;
    }
    
    /**
     * Polls the next actor name from the routing queue associated with this
     * message and returns it.
     * 
     * @return  the next actor name from the routing queue
     */
    public String getNextAgentName() {
        return route.pollActorName();
    }
    
}
