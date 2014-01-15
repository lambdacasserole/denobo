package denobo.compression;

import denobo.compression.lzw.LZWCompressor;

/**
 * Specifies that implementing classes support data compression and decompression.
 * 
 * @author Saul Johnson
 */
public abstract class Compressor {
    
    /**
     * An array of each different type of compressor available in the software.
     */
    public static Compressor[] compressors = new Compressor[] {new DummyCompressor(),
        new BasicCompressor(), new LZWCompressor()};
    
    /**
     * Creates a new instance of the compressor with the specified name.
     * 
     * @param name  the name of the compressor to create
     * @return      a new instance of the compressor with the specified name
     */
    public static Compressor instantiate(String name) {
        for (Compressor current : compressors) {
            if (current.getName().equals(name)) {
                return current.create();
            }
        }
        return null;
    }
        
    /**
     * Gets the name of the compressor.
     * 
     * @return  the name of the compressor
     */
    public abstract String getName();
            
    /**
     * Gets a newly-initialised instance of this compressor.
     * 
     * @return  a newly-initialised instance of this compressor
     */
    protected abstract Compressor create();
    
    /**
     * Compresses an array of bytes.
     * @param data  the byte array to compress
     * @return      a compressed array of bytes
     */
    public abstract byte[] compress(byte[] data);
    
    /**
     * Decompresses an array of bytes.
     * @param data  the byte array to decompress
     * @return      a decompressed array of bytes
     */
    public abstract byte[] decompress(byte[] data);
    
}
