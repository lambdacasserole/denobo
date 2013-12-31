package denobo.compression.checksum;

/**
 * A checksum calculator that uses the BSD checksum algorithm to produce a 
 * 1-byte checksum for a set of data.
 * 
 * @author Saul Johnson
 */
public class BSDChecksum implements Checksum {

    @Override
    public byte[] compute(byte[] data) {
        
        byte checksum = 0;
        for (byte current : data) {
            checksum = (byte) (((checksum & 0xFF) >>> 1) + ((checksum & 0x1) << 7));
            checksum = (byte) ((checksum + current) & 0xFF);
        }
        return new byte[] {checksum};
        
    }
    
}
