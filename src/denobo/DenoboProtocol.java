package denobo;

import java.io.BufferedReader;
import java.io.IOException;

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
    
    public DenoboPacket readPacket(BufferedReader reader) throws IOException {
        
        DenoboPacket nextPacket;
        
        // Parse out status code.
        final String[] statusCodeField = reader.readLine().split(":");
        final int statusCode = Integer.parseInt(statusCodeField[1]);

        // Parse out body length.
        final String[] bodyLengthField = reader.readLine().split(":");
        final int bodyLength = Integer.parseInt(bodyLengthField[1]);

        // Parse out payload.
        final char[] packetBody = new char[bodyLength];
        reader.read(packetBody);

        // Let the observers deal with packet.
        nextPacket = new DenoboPacket(statusCode, String.valueOf(packetBody));
        return nextPacket;
                    
    }
    
    
}
