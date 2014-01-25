package denobo;

/**
 * Represents a generic error encountered when a query string undergoing parsing
 * is invalid for some reason.
 * 
 * @author Saul Johnson
 */
public class InvalidQueryStringException extends RuntimeException {
   
    /**
     * The query string that raised the exception.
     */
    private QueryString queryString;
    
    /**
     * Initialises a new instance of a generic error encountered when a query 
     * string undergoing parsing is invalid for some reason.
     * 
     * @param message       additional user-specified information about the
     *                      exception
     * @param queryString   the query string that raised the exception
     */
    public InvalidQueryStringException(String message, QueryString queryString) {
        super(message);
        this.queryString = queryString;
    }
    
    /**
     * Initialises a new instance of a generic error encountered when a query 
     * string undergoing parsing is invalid for some reason.
     * 
     * @param queryString   the query string that raised the exception
     */
    public InvalidQueryStringException(QueryString queryString) {
        this("Query string wasn't valid.", queryString);
    }
    
    /**
     * Gets the query string that raised the exception.
     * 
     * @return  the query string that raised the exception
     */
    public QueryString getQueryString() {
        return queryString;
    }
    
}
