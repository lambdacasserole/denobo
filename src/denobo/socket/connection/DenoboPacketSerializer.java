package denobo.socket.connection;

import denobo.QueryString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.Writer;

/**
 * An implementation of PacketSerializer for serializing {@link Packet} objects used in
 * Denobo.
 *
 * @author Saul Johnson, Alex Mullen
 */
public class DenoboPacketSerializer implements PacketSerializer {

    @Override
    public void writePacket(Writer writer, Packet packet) throws IOException {
        
        // Packet start token.
        writer.write("@");
        
        final QueryString queryString = new QueryString();
        queryString.add("code", String.valueOf(packet.getCode().toInt()));
        queryString.add("body", packet.getBody());
        writer.write(queryString.toString());
        
        // Packet end token.
        writer.write("$");
        
        writer.flush();
        
    }
    
    @Override
    public Packet readPacket(BufferedReader reader) throws IOException, StreamCorruptedException {
        
        // Read until starting token.
        while (reader.read() != '@') {
            /* 
             * Read until we hit an '@' sign.
             */
        }
        
        // Read string up until ending token.
        int buffer;
        final StringBuilder sb = new StringBuilder();
        while ((buffer = reader.read()) != '$') {
            sb.append((char) buffer);
        }
        
        final QueryString queryString = new QueryString(sb.toString());
        final String code = queryString.get("code");
        final String body = queryString.get("body");
        
        return new Packet(PacketCode.valueOf(Integer.parseInt(code)), body);
        
    }
    
}
