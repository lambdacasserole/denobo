package denobo.socket.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Saul Johnson
 */
public interface PacketSerializer {

    public void writePacket(BufferedWriter writer, Packet packet) throws IOException;
    
    public Packet readPacket(BufferedReader reader) throws IOException;
    
}
