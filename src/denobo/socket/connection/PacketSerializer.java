package denobo.socket.connection;

import denobo.compression.Compressor;
import denobo.crypto.CryptoAlgorithm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
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
     * @param writer        the Writer to serialize to
     * @param packet        the Packet to serialize
     * @throws IOException  if an I/O error occurs
     */
    public void writePacket(Writer writer, Packet packet) throws IOException;
    
    /**
     * Deserializes a Packet object from the specified {@link BufferedReader}.
     * 
     * @param reader        the Reader to deserialize from
     * @return              a Packet object from the serialized data, or null if
     *                      the end of the stream has been reached
     * @throws IOException  if an I/O error occurs
     * @throws StreamCorruptedException if control information that was read from
     *                                  a packet violated consistency checks 
     */
    public Packet readPacket(Reader reader) throws IOException, StreamCorruptedException;
    
    public void setCompressor(Compressor compressor);
    
    public void setCryptoAlgorithm(CryptoAlgorithm crypto);
    
}
