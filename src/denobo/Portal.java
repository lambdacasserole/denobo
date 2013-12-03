package denobo;

import java.util.HashMap;

/**
 *
 * @author Saul
 */
public class Portal extends MetaAgent {

    private HashMap<String, MetaAgent> childAgents;

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
