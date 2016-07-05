package com.sauljohnson.denobo.exceptions;

import com.sauljohnson.denobo.Route;

/**
 * Represents a generic error encountered during routing.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RoutingException extends RuntimeException {

    private final Route route;
   
    /**
     * Initializes a new instance of a generic error encountered during routing.
     * @param message   additional user-specified information about the exception
     * @param route     the route that raised the exception
     */
    public RoutingException(String message, Route route) {
        super(message);
        this.route = route;
    }
    
    /**
     * Gets the route that raised the exception.
     * @return  the route that raised the exception
     */
    public Route getRoute() {
        return route;
    }
    
}
