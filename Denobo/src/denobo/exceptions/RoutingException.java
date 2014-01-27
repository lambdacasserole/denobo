package denobo.exceptions;

import denobo.Route;

/**
 * Represents a generic error encountered during routing.
 * 
 * @author Saul Johnson
 */
public class RoutingException extends RuntimeException {
   
    /**
     * The route that raised the exception.
     */
    private final Route route;
   
    /**
     * Initialises a new instance of a generic error encountered during routing.
     * 
     * @param message   additional user-specified information about the
     *                  exception
     * @param route     the route that raised the exception
     */
    public RoutingException(String message, Route route) {
        super(message);
        this.route = route;
    }
    
    /**
     * Gets the route that raised the exception.
     * 
     * @return  the route that raised the exception
     */
    public Route getRoute() {
        return route;
    }
    
}
