package denobo.compression.huffman;

/**
 * Represents a stream of bits with write capabilities.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class BitOutputStream {
    
    /**
     * The {@link BitSequence} object that underlies the stream.
     */
    private final BitSequence seq;
   
    /**
     * Initialises a new instance of a bit output stream.
     */
    public BitOutputStream() {
         seq = new BitSequence();
    }
    
    /**
     * Writes a {@link BitSequence} to the stream.
     * 
     * @param value the {@link BitSequence} to write to the stream
     */
    public void write(BitSequence value) {
        seq.append(value);
    }
    
    /**
     * Writes a bit to the stream.
     * 
     * @param value the value of the bit to write
     */
    public void write(boolean value) {
        seq.append(value);
    }
    
    /**
     * Gets the length of the stream.
     * 
     * @return  the length of the stream
     */
    public int length() {
        return seq.getLength();
    }
    
    /**
     * Returns the stream as a byte array.
     * 
     * @return  the stream as a byte array
     */
    public byte[] toArray() {
        return seq.toArray();
    }
    
}
