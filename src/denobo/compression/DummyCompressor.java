package denobo.compression;

/**
 * A non-functioning compressor that neither compresses nor decompresses data.
 * 
 * @author Saul Johnson
 */
public class DummyCompressor implements Compressor {

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }
    
}
