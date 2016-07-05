package com.sauljohnson.denobo;

/**
 * Represents a message passed between Actors in a multi-agent system in order
 * to communicate with one another.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
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
    private final Route route;
    
    /**
     * The Message data.
     */
    private final String data;
    
    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of a Message.
     * 
     * @param id            the unique identifier for the new Message
     * @param route         the route this message should take to reach its 
     *                      destination Actor
     * @param data          the Message data
     */
    public Message(String id, Route route, String data) {
        this.id = id;
        this.route = route;
        this.recipient = route.last();
        this.originator = route.first();
        this.data = data;
    }
     
    /**
     * Initialises a new instance of a Message.
     * 
     * @param route         the route this message should take to reach its 
     *                      destination Actor
     * @param data          the Message data
     */
    public Message(Route route, String data) {
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
    public final Route getRoute() {
        return route;
    }
    
    /**
     * Returns a serialised representation of this message.
     * 
     * @return  a serialised representation of this message
     */
    public String serialize() {
        final QueryString queryString = new QueryString();
        queryString.add("id", getId());
        queryString.add("route", getRoute().serialize());
        queryString.add("data",  getData());
        return queryString.toString();
    }
    
    /**
     * Deserializes a route out of a string and returns it.
     * 
     * @param string    the string from which to deserialize the route
     * @return          a route instance
     */
    public static Message deserialize(String string) {
        final QueryString queryString = new QueryString(string);
        return new Message(queryString.get("id"), 
                Route.deserialize(queryString.get("route")), queryString.get("data"));
    } 
    
}
