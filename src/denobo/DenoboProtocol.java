package denobo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Saul Johnson
 */
public class DenoboProtocol implements Protocol {
        
    /**
     * Holds the packet header magic number for this version of the software.
     */
    public static final String PACKET_HEADER = "DENOBO v0.9 (BENSON)";
    
    @Override
    public void writePacket(PrintWriter writer, DenoboPacket packet) {
        
        final StringBuilder sb = new StringBuilder();
        sb.append(PACKET_HEADER).append("\n");
        sb.append("status-code:").append(packet.getStatusCode()).append("\n");
        sb.append("body-length:").append(packet.getBody().length()).append("\n");
        sb.append(packet.getBody());
        
        writer.write(sb.toString());
        writer.flush();
        
    }
    
    @Override
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

    @Override
    public String serializeMessage(Message message) {
        return "from=" + message.getFrom() + "&to=" + message.getTo() + "&msg=" + message.getMessage();
    }

    @Override
    public Message deserializeMessage(String string) {
        String to = null, from = null, message = null;
                
        final String[] pairSplitter = string.split("&");
        for (String str : pairSplitter) {
            final String[] nameValueSplitter = str.split("=");
            if (nameValueSplitter[0].equals("to")) {
                to = nameValueSplitter[1];
            } else if (nameValueSplitter[0].equals("from")) {
                from = nameValueSplitter[1];
            } else if (nameValueSplitter[0].equals("msg")) {
                message = nameValueSplitter[1];
            } else {
                // TODO: Handle invalid string parameter
            }
        }

        return new Message(from, to, message);
    }
    
    
}
