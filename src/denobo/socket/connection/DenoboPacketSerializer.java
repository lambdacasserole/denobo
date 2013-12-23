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
        
        writer.append(PACKET_HEADER).append("\n");
        writer.append("code:").append(String.valueOf(packet.getCode().toInt())).append("\n");
        writer.append("body-length:").append(String.valueOf(packet.getBody().length())).append("\n");
        writer.append(packet.getBody());
        
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
