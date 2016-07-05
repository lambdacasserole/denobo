package com.sauljohnson.denobo.exceptions;

import com.sauljohnson.denobo.RoutingWorker;

/**
 * Represents an error encountered when the originator of a routing worker attempts to map a route to itself.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RouteToSelfException extends RoutingException {

    private RoutingWorker worker;

    /**
     * Initializes a new instance of an error encountered when the originator of a routing worker attempts to map a
     * route to itself.
     * @param message   additional user-specified information about the exception
     * @param worker    the routing worker that raised the exception
     */
    public RouteToSelfException(String message, RoutingWorker worker) {
        super(message, null);
        this.worker = worker;
    }
    
    /**
     * Initializes a new instance of an error encountered when the originator of a routing worker attempts to map a
     * route to itself.
     * @param worker    the routing worker that raised the exception
     */
    public RouteToSelfException(RoutingWorker worker) {
        this("Agent '" + worker.getOrigin().getName() + "' illegally spawned a routing worker to itself.", worker);
    }
    
    /**
     * Gets the routing worker that threw the exception.
     * @return  the routing worker that threw the exception
     */
    public RoutingWorker getWorker() {
        return worker;
    }
}
