package com.sauljohnson.denobo.exceptions;

import com.sauljohnson.denobo.QueryString;

/**
 * Represents a generic error encountered when a query string undergoing parsing is invalid for some reason.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class InvalidQueryStringException extends RuntimeException {

    private QueryString queryString;
    
    /**
     * Initializes a new instance of a generic error encountered when a query string undergoing parsing is invalid for
     * some reason.
     * @param message       additional user-specified information about the exception
     * @param queryString   the query string that raised the exception
     */
    public InvalidQueryStringException(String message, QueryString queryString) {
        super(message);
        this.queryString = queryString;
    }
    
    /**
     * Initializes a new instance of a generic error encountered when a query string undergoing parsing is invalid for
     * some reason.
     * @param queryString   the query string that raised the exception
     */
    public InvalidQueryStringException(QueryString queryString) {
        this("Query string wasn't valid.", queryString);
    }
    
    /**
     * Gets the query string that raised the exception.
     * @return  the query string that raised the exception
     */
    public QueryString getQueryString() {
        return queryString;
    }
}
