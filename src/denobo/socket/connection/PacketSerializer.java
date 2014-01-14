package denobo.socket.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.Writer;

/**
 * An interface for serializing {@link Packet} objects.
 *
 * @author Saul Johnson, Alex Mullen
 */
public interface PacketSerializer {

    /**
     * Serializes a Packet object to the specified {@link BufferedWriter}.
     * 
     * @param writer        the BufferedWriter to serialize to
     * @param packet        the Packet to serialize
     * @throws IOException  if an I/O error occurs
     */
    public void writePacket(Writer writer, Packet packet) throws IOException;
    
    /**
     * Deserializes a Packet object from the specified {@link BufferedReader}.
     * 
     * @param reader        the BufferedReader to deserialize from
     * @return              a Packet object from the serialized data, or null if
     *                      the end of the stream has been reached
     * @throws IOException  if an I/O error occurs
     * @throws StreamCorruptedException if control information that was read from
     *                                  a packet violated consistency checks 
     */
    public Packet readPacket(BufferedReader reader) throws IOException, StreamCorruptedException;
    
}
