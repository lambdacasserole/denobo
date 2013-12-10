package denobo;

import java.util.HashMap;

/**
 * Represents a portal, acting as a routing engine between one or more 
 * child agents.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Portal extends MetaAgent {

    /**
     * Holds a collection of this portal's child agents.
     */
    final private HashMap<String, MetaAgent> childAgents;

    /**
     * Holds a message history logger used to prevent backwards message propagation.
     */
    final private MessageHistory messageHistory;
    
    /**
     * Initialises a new instance of a portal.
     * 
     * @param name  the name of the portal
     */
    public Portal(String name) {
        
        super(name);
        childAgents = new HashMap<>();
        messageHistory = new MessageHistory();
        
    }
   
    /**
     * Adds an agent to this portal.
     * 
     * @param agent the agent to add to the portal
     */
    public void addAgent(MetaAgent agent) {
        
        childAgents.put(agent.getName(), agent);
        agent.registerParentPortal(this);
        
    }
    
    /**
     * Removes an agent from this portal.
     * 
     * @param agent the agent to remove from the portal
     */
    public void removeAgent(MetaAgent agent) {
        
        agent.unregisterParentPortal(this);
        childAgents.remove(agent.getName());
        
    }
    
    @Override
    public void handleMessage(Message message) {
        
        // Reject messages that have previously passed through this node.
        if (messageHistory.hasMessage(message.getId())) {
            return;
        }
        
        // Record the ID of this message in the history.
        messageHistory.update(message.getId());
        
        if (childAgents.containsKey(message.getTo())) {
        
            // Pass to child for processing if we have the recipent as a subnode.
            childAgents.get(message.getTo()).queueMessage(message);
            
        } else {

            // Broadcast to child portals.
            for (MetaAgent agent : childAgents.values()) {
                if (agent instanceof Portal) { agent.queueMessage(message); }
            }
            
        }
        
    }

}
