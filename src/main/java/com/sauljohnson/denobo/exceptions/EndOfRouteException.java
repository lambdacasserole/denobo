package com.sauljohnson.denobo.exceptions;

import com.sauljohnson.denobo.Route;

/**
 * Represents an error caused by attempting to get the next agent name in a route after its end has been reached.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class EndOfRouteException extends RoutingException {
    
    /**
     * Initializes a new instance of an error caused by attempting to get the next agent name in a route after its end
     * has been reached.
     * @param message   additional user-specified information about the exception
     * @param route     the route that raised the exception
     */
    public EndOfRouteException(String message, Route route) {
        super(message, route);
    }
    
    /**
     * Initializes a new instance of an error caused by attempting to get the next agent name in a route after its end
     * has been reached.
     * @param route the route that raised the exception
     */
    public EndOfRouteException(Route route) {
        this("Couldn't get next agent name in route because its end has been reached.", route);
    }
}
