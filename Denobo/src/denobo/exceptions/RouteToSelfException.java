package denobo.exceptions;

import denobo.RoutingWorker;

/**
 * Represents an error encountered when the originator of a routing worker 
 * attempts to map a route to itself.
 * 
 * @author Saul Johnson
 */
public class RouteToSelfException extends RoutingException {
    
    /**
     * The routing worker that threw the exception.
     */
    private RoutingWorker worker;

    /**
     * Initialises a new instance of an error encountered when the originator 
     * of a routing worker attempts to map a route to itself.
     * 
     * @param message   additional user-specified information about the
     *                  exception
     * @param worker    the routing worker that raised the exception
     */
    public RouteToSelfException(String message, RoutingWorker worker) {
        super(message, null);
        this.worker = worker;
    }
    
    /**
     * Initialises a new instance of an error encountered when the originator 
     * of a routing worker attempts to map a route to itself.
     * 
     * @param worker    the routing worker that raised the exception
     */
    public RouteToSelfException(RoutingWorker worker) {
        this("Agent '" + worker.getOrigin().getName() 
                + "' illegally spawned a routing worker to itself.", worker);
    }
    
    /**
     * Gets the routing worker that threw the exception.
     * @return  the routing worker that threw the exception
     */
    public RoutingWorker getWorker() {
        return worker;
    }
    
}
