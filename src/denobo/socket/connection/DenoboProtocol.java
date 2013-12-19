package denobo.socket.connection;

import denobo.Message;
import denobo.MessageSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Saul Johnson
 */
public class DenoboProtocol implements Protocol {
        
    /**
     * Holds the packet header magic number for this protocol.
     */
    private static final String PACKET_HEADER = "DENOBO v0.9 (BENSON)";
    
    
    @Override
    public String getPacketHeader() {
        return PACKET_HEADER;
    }
    
    @Override
    public void writePacket(PrintWriter writer, DenoboPacket packet) {
        
        final StringBuilder sb = new StringBuilder();
        sb.append(PACKET_HEADER).append("\n");
        sb.append("status-code:").append(packet.getCode().toInt()).append("\n");
        sb.append("body-length:").append(packet.getBody().length()).append("\n");
        sb.append(packet.getBody());
        
        writer.write(sb.toString());
        writer.flush();
        
    }
    
    @Override
    public DenoboPacket readPacket(BufferedReader reader) throws IOException {
        
        if (PACKET_HEADER.equals(reader.readLine())) {
            
            System.out.println("Recieved packet appears valid, magic number: " 
                        + PACKET_HEADER);

            // Parse out status code.
            final String[] statusCodeField = reader.readLine().split(":");
            final int code = Integer.parseInt(statusCodeField[1]);

            // Parse out body length.
            final String[] bodyLengthField = reader.readLine().split(":");
            final int bodyLength = Integer.parseInt(bodyLengthField[1]);

            // Parse out payload.
            final char[] packetBody = new char[bodyLength];
            reader.read(packetBody);

            // Let the observers deal with packet.
            return new DenoboPacket(PacketCode.valueOf(code), String.valueOf(packetBody));   
            
        } else {
            
            return null;
            
        }
          
    }

    @Override
    public void writeMessage(PrintWriter writer, Message message) {
        writePacket(writer, new DenoboPacket(PacketCode.PROPAGATE, MessageSerializer.serialize(message)));
    }

}
