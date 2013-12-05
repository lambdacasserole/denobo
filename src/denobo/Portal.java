package denobo;

import java.util.HashMap;
import java.util.Map.Entry;

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
        boolean result = false;
        for(Entry<String, MetaAgent> agentEntry : childAgents.entrySet()) {
            result = result || agentEntry.getValue().hasRouteToAgent(name);
        }
        
        return result;
        
    }
    
    @Override
    public void handleMessage(String message) {
        
        ArgumentList args = new ArgumentList(message);
        String recipientName = args.getValue("to");
        
        // Check whether or not we have a child agent with a matching name.
        if(childAgents.containsKey(recipientName)) {
            childAgents.get(recipientName).queueMessage(message);
        } else {
            System.out.println("Warning: agent '" + recipientName + "' not " 
                    + "found in portal.");
        }
        
    }
    
}
