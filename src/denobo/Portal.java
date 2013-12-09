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
     * Initialises a new instance of a portal.
     * 
     * @param name  the name of the portal
     */
    public Portal(String name) {
        
        super(name);
        childAgents = new HashMap<>();
        
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
    public boolean hasRouteToAgent(String name) {
        
        // Check routing to destination agent.
        for (MetaAgent agent : childAgents.values()) {
            if (agent.hasRouteToAgent(name)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    @Override
    public void handleMessage(Message message) {
        
        // Check whether or not we have a child agent with a matching name.
        for(MetaAgent agent : childAgents.values()) {
            if(agent.hasRouteToAgent(message.getTo())) {
                agent.queueMessage(message);
                break;
            }
        }
        
    }
    
}
