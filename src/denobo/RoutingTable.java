package denobo;

import java.util.HashMap;

/**
 * Represents a table of destination actors and optimal routes that should be
 * taken by messages to reach them.
 * 
 * @author Saul Johnson
 */
public class RoutingTable {
    
    /**
     * The map of actors names to routes.
     */
    private final HashMap<String, RoutingQueue> table;
    
    /**
     * Initialises a new instance of a routing table.
     */
    public RoutingTable() {
        table = new HashMap<>();
    }
    
    /**
     * Gets whether or not this routing table contains an entry to reach the 
     * actor with the specified name.
     * 
     * @param actorName the name of the destination actor
     * @return          true if the table has a route for the actor, otherwise
     *                  false
     */
    public boolean hasRoute(String actorName) {
        return table.containsKey(actorName);
    }
    
    /**
     * Adds a route to the table.
     * 
     * @param actorName the name of the destination actor
     * @param queue     the routing queue that represents the route to the actor 
     */
    public void addRoute(String actorName, RoutingQueue queue) {
        
        /* 
         * Remove any previous, less efficient routes or stop right now if the
         * proposed new route is less efficient
         */ 
        if (hasRoute(actorName)) {
            if (table.get(actorName).getSize() <= queue.getSize()) {
                return;
            } else {
                table.remove(actorName);
            }
        }
        
        table.put(actorName, queue);
        
    }
    
    /**
     * Gets the route to an actor.
     * 
     * @param actorName the name of the actor to get the route to
     * @return          the route to the actor
     */
    public RoutingQueue getRoute(String actorName) {
        return table.get(actorName);
    }
    
}
