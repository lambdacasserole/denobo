package denobo.compression;

/**
 * Specifies that implementing classes support data compression and decompression.
 * 
 * @author Saul Johnson
 */
public interface Compressor {
    
    /**
     * Compresses an array of bytes.
     * @param data  the byte array to compress
     * @return      a compressed array of bytes
     */
    public byte[] compress(byte[] data);
    
    /**
     * Decompresses an array of bytes.
     * @param data  the byte array to decompress
     * @return      a decompressed array of bytes
     */
    public byte[] decompress(byte[] data);
    
}
