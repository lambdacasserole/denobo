package denobo.compression;

/**
 * A non-functioning compressor that neither compresses nor decompresses data.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DummyCompressor extends Compressor {

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }

    @Override
    public String getName() {
        return "none";
    }

    @Override
    protected Compressor create() {
        return new DummyCompressor();
    }
    
}
