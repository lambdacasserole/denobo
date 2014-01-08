package denobo;

/**
 * Implemented by classes that wish to be notified when a {link RoutingWorker}
 * finishes calculating the optimal route to an actor.
 * 
 * @author Saul Johnson
 */
public interface RoutingWorkerListener {
    
    /**
     * Called when the routing worker has finished calculating the optimal route
     * to an actor.
     * 
     * @param destinationActorName  the name of the destination actor
     * @param route                 the optimal route to the actor
     */
    void routeCalculated(String destinationActorName, RoutingQueue route);
    
}
