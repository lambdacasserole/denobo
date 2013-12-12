package denobo;

import java.util.ArrayList;
import java.util.List;

public class Agent extends Actor {

    /**
     * Holds a list of {@link MessageHandler} objects observing messages passed to the agent.
     */
    private final List<MessageHandler> handlers;
    
    /**
     * Holds a message history logger used to prevent backwards message propagation.
     */
    final protected MessageHistory messageHistory;
    
    /**
     * Initialises a new instance of a portal.
     * 
     * @param name      the name of the portal
     * @param cloneable 
     */
    public Agent(String name, boolean cloneable) {
        
        super(name, cloneable);
        messageHistory = new MessageHistory();
        handlers = new ArrayList<>();
        
    }
    
    public Agent(String name) {
        this(name, false);
    }
    
    /**
     * Adds a {@link MessageHandler} to listen for messages passed to this agent.
     * 
     * @param handler   the {@link MessageHandler} to add as an observer
     */
    public void addMessageHandler(MessageHandler handler) {
        handlers.add(handler);
    }
    
    /**
     * Removes a {@link MessageHandler} that is currently listening for messages 
     * passed to this agent.
     * 
     * @param handler   the {@link MessageHandler} to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(handler);
    }
    
    /**
     * Sends a {@link Message} from this {@link Agent} to another.
     * 
     * @param to        the name of the recipient {@link Agent}
     * @param message   the message to send
     */
    public void sendMessage(String to, String message) {
        
        Message propagatingMessage = new Message(getName(), to, message);
        
        // Broadcast to all parents.
        for (Actor actor : connectedActors) {
            actor.queueMessage(propagatingMessage);
        }
        
    }
    
    @Override
    protected boolean handleMessage(Message message) {
        
        // Reject messages that have previously passed through this node.
        if (messageHistory.hasMessage(message)) { return false; }
        
        // Record the ID of this message in the history.
        messageHistory.update(message.getId());
        
        // Pass message to each handler if this node is the recipient.
        if(message.hasRecipient(this)) {
            for (MessageHandler handler : handlers) {
                handler.messageRecieved(message);
            }
        }
        
        // Broadcast to peers.
        for (Actor actor : connectedActors) {
            actor.queueMessage(message);
        }
                    
        return true;
        
    }

}
