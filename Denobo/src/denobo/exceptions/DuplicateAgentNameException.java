package denobo.exceptions;

import denobo.Route;

/**
 * Represents an error caused when duplicate agent names are encountered during 
 * routing.
 * 
 * @author Saul Johnson
 */
public class DuplicateAgentNameException extends RoutingException {
    
    /**
     * Initialises a new instance of an caused when duplicate agent names are 
     * encountered during routing.
     * 
     * @param message   additional user-specified information about the
     *                  exception
     * @param route     the route that raised the exception
     */
    public DuplicateAgentNameException(String message, Route route) {
        super(message, route);
    }
    
    /**
     * Initialises a new instance of an caused when duplicate agent names are 
     * encountered during routing.
     * 
     * @param route the route that raised the exception
     */
    public DuplicateAgentNameException(Route route) {
        this("Route contains illegal duplicate agent names.", route);
    }
    
}
