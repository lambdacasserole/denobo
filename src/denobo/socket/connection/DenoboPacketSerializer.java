package denobo.socket.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Saul Johnson, Alex Mullen
 */
public class DenoboPacketSerializer implements PacketSerializer {
        
    /**
     * Holds the packet header magic number for this protocol.
     */
    private static final String PACKET_HEADER = "DENOBO v0.9 (BENSON)";
    

    @Override
    public void writePacket(BufferedWriter writer, Packet packet) throws IOException {
        
        final StringBuilder sb = new StringBuilder();
        sb.append(PACKET_HEADER).append("\n");
        sb.append("code:").append(packet.getCode().toInt()).append("\n");
        sb.append("body-length:").append(packet.getBody().length()).append("\n");
        sb.append(packet.getBody());
        
        writer.write(sb.toString());
        writer.flush();
        
    }
    
    @Override
    public Packet readPacket(BufferedReader reader) throws IOException {
        
        if (PACKET_HEADER.equals(reader.readLine())) {
            
            System.out.println("Recieved packet appears valid, magic number: " 
                        + PACKET_HEADER);

            // Parse out status code.
            final String[] statusCodeField = reader.readLine().split(":", 2);
            if (statusCodeField.length != 2) { 
                return null; 
            }
            final int code = Integer.parseInt(statusCodeField[1]);
            
            // Parse out body length.
            final String[] bodyLengthField = reader.readLine().split(":", 2);
            if (bodyLengthField.length != 2) { 
                return null; 
            }
            final int bodyLength = Integer.parseInt(bodyLengthField[1]);

            // Parse out payload.
            final char[] packetBody = new char[bodyLength];
            reader.read(packetBody);

            // Let the observers deal with packet.
            final PacketCode packetCode = PacketCode.valueOf(code);
            if (packetCode == null) {
                return null;
            }
            return new Packet(packetCode, String.valueOf(packetBody));   
            
        } else {
            
            return null;
            
        }
        
    }
    
}
