package denobo.compression.huffman;

/**
 * Represents a stream of bits with read capabilities.
 * 
 * @author Saul Johnson
 */
public class BitInputStream {
    
    /**
     * The data being read.
     */
    byte[] data;
    
    /**
     * The index of the current byte.
     */
    private int byteIndex;
    
    /**
     * The index of the current bit within the current byte.
     */
    private int bitIndex;
        
    /**
     * Initialises a new instance of a bit input stream.
     * 
     * @param data  the byte array to read from
     */
    public BitInputStream(byte[] data) {
        
        this.data = data;
        
        byteIndex = 0;
        bitIndex = 0;
        
    }
    
    /**
     * Skips the specified number of bytes.
     * 
     * @param num   the number of bytes to skip
     */
    public void skipBytes(int num) {
        
        byteIndex += num;
        
    }
    
    /**
     * Gets the current position in the stream in bits.
     * 
     * @return  the current position in the stream in bits
     */
    public int getPosition() {
        
        return (byteIndex * Word.SIZE) + bitIndex;
        
    }
    
    /**
     * Returns the next bit from the stream as a boolean value.
     * 
     * @return  the next bit from the stream as a boolean value
     */
    public boolean read() {
         
        final Word currentWord = new Word(data[byteIndex]);
        final boolean value = currentWord.getBit(bitIndex);
        
        bitIndex++;
        if (bitIndex == 8) {
            byteIndex++;
            bitIndex = 0;
        }
        
        return value;
        
    }
    
}
