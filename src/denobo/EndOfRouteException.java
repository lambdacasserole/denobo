package denobo;

/**
 * Represents an error encountered when a caller attempts to get the next agent 
 * name in a route after its end has been reached.
 * 
 * @author Saul Johnson
 */
public class EndOfRouteException extends RoutingException {
    
    /**
     * Initialises a new instance of an error encountered when a caller attempts 
     * to get the next agent name in a route after its end has been reached.
     * 
     * @param message   additional user-specified information about the
     *                  exception
     * @param route     the route that raised the exception
     */
    public EndOfRouteException(String message, Route route) {
        super(message, route);
    }
    
    /**
     * Initialises a new instance of an error encountered when a caller attempts
     * to get the next agent name in a route after its end has been reached.
     * 
     * @param route the route that raised the exception
     */
    public EndOfRouteException(Route route) {
        this("Couldn't get next agent name in route because its end has been " 
                + "reached.", route);
    }
    
}
