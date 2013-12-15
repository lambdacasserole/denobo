package denobo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Agent that provides a basic concrete implementation of an Actor as part of
 * a multi-agent system.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
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

    /**
     * Initialises a new instance of a portal.
     *
     * @param name      the name of the portal
     * @param cloneable whether or not the agent is cloneable
     */
    public Agent(String name, boolean cloneable) {

        super(name, cloneable);
        messageHistory = new SynchronizedMessageHistory(new MessageHistory());
        handlers = Collections.synchronizedList(new ArrayList<MessageHandler>());

    }

    /**
     * Initialises a new instance of a portal that isn't cloneable.
     *
     * @param name the name of the portal
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
        handlers.add(handler);
    }

    /**
     * Removes a {@link MessageHandler} that is currently listening for messages
     * passed to this agent.
     *
     * @param handler the {@link MessageHandler} to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Sends a {@link Message} from this {@link Agent} to another.
     *
     * @param to the name of the recipient {@link Agent}
     * @param message the message to send
     */
    public void sendMessage(String to, String message) {

        final Message propagatingMessage = new Message(getName(), to, message);

        // This is needed but when connecting to ourselves, we won't receive
        // any messages because we've already 'handled' the message. This is
        // intended behaviour.
        messageHistory.update(propagatingMessage.getId());
 
        handleMessage(propagatingMessage);
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
        
        // Pass message to each handler
        if (message.hasRecipient(this)) {
            synchronized (handlers) {
                for (MessageHandler handler : handlers) {
                    handler.messageRecieved(this, message);
                }
            }
        }

        // Broadcast to peers.
        broadcastMessage(message);
    }
}
