package com.sauljohnson.denobo.exceptions;

import com.sauljohnson.denobo.Route;

/**
 * Represents an error caused when duplicate agent names are encountered during routing.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DuplicateAgentNameException extends RoutingException {
    
    /**
     * Initialises a new instance of an caused when duplicate agent names are encountered during routing.
     * @param message   additional user-specified information about the exception
     * @param route     the route that raised the exception
     */
    public DuplicateAgentNameException(String message, Route route) {
        super(message, route);
    }
    
    /**
     * Initialises a new instance of an caused when duplicate agent names are encountered during routing.
     * @param route the route that raised the exception
     */
    public DuplicateAgentNameException(Route route) {
        this("Route contains illegal duplicate agent names.", route);
    }
}
