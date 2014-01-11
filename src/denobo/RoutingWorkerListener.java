package denobo;

/**
 * Implemented by classes that wish to be notified when a {link RoutingWorker}
 * finishes calculating the optimal route to an agent.
 * 
 * @author Saul Johnson
 */
public interface RoutingWorkerListener {
    
    /**
     * Called when the routing worker has finished calculating the optimal route
     * to an agent.
     * 
     * @param destinationAgentName  the name of the destination agent
     * @param route                 the optimal route to the agent
     */
    void routeCalculationSucceeded(String destinationAgentName, Route route);
    
    /**
     * Called when the routing worker has finished but it was unable to calculate
     * a route to the specified destination agent.
     * 
     * @param destinationAgentName  the name of the destination agent
     */
    void routeCalculationFailed(String destinationAgentName);
    
}
