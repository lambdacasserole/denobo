package denobo.socket.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Saul Johnson
 */
public interface PacketSerializer {

    public void writePacket(PrintWriter writer, Packet packet);
    
    public Packet readPacket(BufferedReader reader) throws IOException;
    
}
