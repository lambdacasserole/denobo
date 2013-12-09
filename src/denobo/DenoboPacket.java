package denobo;

/**
 * Represents a packet of data passed between DenoboConnection objects.
 * 
 * @author Saul
 */
public class DenoboPacket {
    
    /**
     * Holds the magic number for the packet header for this version.
     */
    public static final String PACKET_HEADER = "DENOBO v0.9 (BENSON)";
    
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
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PACKET_HEADER).append("\n");
        sb.append("status-code:").append(statusCode).append("\n");
        sb.append("body-length:").append(body.length()).append("\n");
        sb.append(body);
        return sb.toString();
    }
    
}
