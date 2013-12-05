package denobo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user agent, which can send messages and may not have children.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class UserAgent extends MetaAgent {
   
    /**
     * Holds a list of message handlers observing messages passed to the agent.
     */
    private final List<MessageHandler> handlers;
    
    /**
     * Initialises a new instance of a user agent.
     * @param name      the name of the new user agent
     * @param cloneable whether or not the new user agent is cloneable
     */
    public UserAgent(String name, boolean cloneable) {
        super(name, cloneable);
        handlers = new ArrayList<>();
    }
    
    /**
     * Adds a message handler to listen for messages passed to this agent.
     * 
     * @param handler   the message handler to add as an observer
     */
    public void addMessageHandler(MessageHandler handler) {
        handlers.add(handler);
    }
    
    /**
     * Removes a message handler that is currently listening for messages 
     * passed to this agent.
     * 
     * @param handler   the message handler to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(handler);
    }
    
    /**
     * Sends a message from this agent to another.
     * 
     * @param to        the name of the recipient agent
     * @param message   the message to send
     */
    public void sendMessage(String to, String message) {
        
        // Check for route to recipient agent and send.
        for(Portal portal : portals) {
            if(portal.hasRouteToAgent(to)) {
                portal.queueMessage("from=" + getName() + "&to=" + to + "&" + message);
                break;
            }
        }
        
    }
    
    @Override
    public boolean hasRouteToAgent(String name) {
        return name.equals(getName());
    }
    
    @Override
    public void handleMessage(String message) {
        
        final ArgumentList args = new ArgumentList(message);
                
        // Extract from and to parameters.
        final String to = args.getValue("to");
        final String from = args.getValue("from");
        
        //  Remove from and to parameters.
        args.removeParam("to");
        args.removeParam("from");
        
        // Pass message to each handler.
        for (MessageHandler handler : handlers) {
            handler.messageRecieved(to, from, args.toString());
        }
        
    }
    
}
