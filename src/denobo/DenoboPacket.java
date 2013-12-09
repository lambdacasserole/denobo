package denobo;

/**
 * Represents a packet of data passed between DenoboConnection objects.
 * 
 * @author Saul
 */
public class DenoboPacket {
        
    /**
     * Holds a status code representing the type of packet.
     */
    private final int statusCode;
    
    /**
     * Holds the packet payload.
     */
    private final String body;

    /**
     * Initialises a new instance of a network portal packet.
     * 
     * @param statusCode    the status code contained within the packet
     * @param body          the packet payload.
     */
    public DenoboPacket(int statusCode, String body) {
        
        this.statusCode = statusCode;
        this.body = body;
        
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getBody() {
        return body;
    }
    
}
