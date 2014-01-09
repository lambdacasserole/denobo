package denobo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An agent that provides a basic concrete implementation of an Actor as part of
 * a multi-agent system.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Agent extends Actor {

    /**
     * A list of {@link MessageHandler} objects observing Messages passed to the 
     * agent.
     */
    private final List<MessageHandler> handlers;

    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of an agent.
     *
     * @param name      the name of the agent
     * @param cloneable whether or not the agent is cloneable
     */
    public Agent(String name, boolean cloneable) {

        super(name, cloneable);
        handlers = new CopyOnWriteArrayList<>();
        
    }

    /**
     * Initialises a new instance of a non-cloneable agent.
     *
     * @param name the name of the agent
     */
    public Agent(String name) {
        this(name, false);
    }

    /**
     * Adds a {@link MessageHandler} to listen for messages passed to this
     * agent.
     *
     * @param handler the {@link MessageHandler} to add as an observer
     */
    public void addMessageHandler(MessageHandler handler) {
        handlers.add(Objects.requireNonNull(handler, "The message handler " + 
                "to add cannot be null."));
    }

    /**
     * Removes a {@link MessageHandler} that is currently listening for messages
     * passed to this agent.
     *
     * @param handler the {@link MessageHandler} to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(Objects.requireNonNull(handler, "The message handler "
                + "to  remove cannot be null."));
    }

    @Override
    protected void handleMessage(Message message) {

        /* 
         * If we are cloneable, this method will possibly be executing in
         * multiple threads.
         */
        
        /* 
         * Let any handlers know of the message received even though it may
         * not be intended for us.
         */
        for (MessageHandler handler : handlers) {
            handler.messageIntercepted(this, message);
        }
        
        /* 
         * If this Agent is the intended recipient of the message, alert each
         * registered message handler.
         */
        if (message.getRecipient().equals(this.getName())) {
            for (MessageHandler handler : handlers) {
                handler.messageRecieved(this, message);
            }
        } else {
            
            // Otherwise, forward it on.
            forwardMessage(message);
            
        }
        
    }
}
