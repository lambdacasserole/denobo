package com.sauljohnson.denobo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a table of destination actors and optimal routes that should be
 * taken by messages to reach them.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RoutingTable {
    
    /**
     * The map of actors names to routes.
     */
    private final Map<String, Route> table;
    
    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of a routing table.
     */
    public RoutingTable() {
        table = Collections.synchronizedMap(new HashMap<String, Route>());
    }
    
    
    /* ---------- */
    
    
    /**
     * Gets whether or not this routing table contains an entry to reach the 
     * actor with the specified name.
     * <p>
     * This checks for a route and returns the whether there is currently
     * a route at the time of calling. After the call there is a possibility
     * that it could have been removed by another thread.
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
    public void addRoute(String actorName, Route queue) {
        
        synchronized (table) {
            /* 
             * Remove any previous, less efficient routes or stop right now if the
             * proposed new route is less efficient
             */ 
            if (hasRoute(actorName)) {
                if (table.get(actorName).size() <= queue.size()) {
                    return;
                } else {
                    table.remove(actorName);
                }
            }

            table.put(actorName, queue);
        }
        
    }
    
    /**
     * Removes any route that contains the specified agent name in it's path.
     * 
     * @param agentName the name of the agent
     */
    public void invalidateAgent(String agentName) {

        synchronized (table) {
            
            // Remove any routes that are a destination to the given agent
            table.remove(agentName);

            /*
             * Go through each Route entry and if any route has the given agent name
             * in its path then remove that route.
             */
            final Iterator<Route> routeIterator = table.values().iterator();
            while (routeIterator.hasNext()) {
                final Route currentRoute = routeIterator.next();
                if (currentRoute.has(agentName)) {
                    routeIterator.remove();
                }
            }
            
        }

    }
    
    /**
     * Clears this routing table.
     */
    public void clear() {
        table.clear();
    }
    
    /**
     * Gets the route to an actor.
     * <p>
     * This method returns a clone of a master route instance.
     * 
     * @param actorName the name of the actor to get the route to
     * @return          the route to the actor
     */
    public Route getRoute(String actorName) {
        
        synchronized (table) {
            final Route foundRoute = table.get(actorName);
            return foundRoute != null ? new Route(foundRoute) : null;
        }
        
    }
    
    @Override
    public String toString() {
        
        final StringBuilder sb = new StringBuilder();
        
        synchronized (table) {
            for (Route currentRoute : table.values()) {
                sb.append(currentRoute.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
}
