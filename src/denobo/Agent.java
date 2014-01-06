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
     * Holds a list of {@link MessageHandler} objects observing messages passed
     * to the agent.
     */
    private final List<MessageHandler> handlers;

    /**
     * Holds a message history logger used to prevent backwards message
     * propagation.
     */
    private final MessageHistory messageHistory;

    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of an agent.
     *
     * @param name      the name of the agent
     * @param cloneable whether or not the agent is cloneable
     */
    public Agent(String name, boolean cloneable) {

        super(name, cloneable);
        messageHistory = new SynchronizedMessageHistory(new MessageHistory());
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
        handlers.add(Objects.requireNonNull(handler, "The message handler to add is null"));
    }

    /**
     * Removes a {@link MessageHandler} that is currently listening for messages
     * passed to this agent.
     *
     * @param handler the {@link MessageHandler} to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(Objects.requireNonNull(handler, "The message handler to remove is null"));
    }

    /**
     * Sends a {@link Message} from this {@link Agent} to another.
     *
     * @param to the name of the recipient {@link Agent}
     * @param message the message to send
     */
    public void sendMessage(String to, String message) {

        // TODO: Validation
        
        final Message propagatingMessage = new Message(getName(), to, message);

        messageHistory.update(propagatingMessage.getId());
 
        // We could put the message in the message queue but if the queue is
        // busy, it could be a while before the message gets propogated so we
        // just deal with it now.
        handleMessage(propagatingMessage);
        //queueMessage(propagatingMessage);
        
    }

    @Override
    protected boolean shouldHandleMessage(Message message) {

        // Remember, this method will always be executed in a single thread.
        
        // Reject messages that have previously passed through this node previously.
        if (messageHistory.hasMessage(message)) {
            // We have already handled this message previously so let the caller 
            // know we don't need to handle the message.
            return false;
        }

        // Record the ID of this message in the history.
        messageHistory.update(message.getId());
        return true;
        
    }

    @Override
    protected void handleMessage(Message message) {

        // If we are cloneable, this method will possibly be executing in
        // multiple threads.
        
        // Let any handlers know of the message received even though it may
        // not be intended for us.
        for (MessageHandler handler : handlers) {
            handler.messageIntercepted(this, message);
        }
        
        // Pass message to each handler if the message has this Agent in the
        // receipients.
        if (message.hasRecipient(this)) {
            for (MessageHandler handler : handlers) {
                handler.messageRecieved(this, message);
            }
        }

        // Broadcast to peers.
        broadcastMessage(message);
        
    }
}
