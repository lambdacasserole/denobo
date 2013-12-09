package denobo;

/**
 *
 * @author Saul Johnson
 */
public class DenoboProtocol {
        
    /**
     * Holds the packet header magic number for this version of the software.
     */
    public static final String PACKET_HEADER = "DENOBO v0.9 (BENSON)";
    
    public String serializePacket(DenoboPacket packet) {
        
        final StringBuilder sb = new StringBuilder();
        sb.append(PACKET_HEADER).append("\n");
        sb.append("status-code:").append(packet.getStatusCode()).append("\n");
        sb.append("body-length:").append(packet.getBody().length()).append("\n");
        sb.append(packet.getBody());
        return sb.toString();
        
    }
    
    
}
