package denobo.compression;

import denobo.compression.checksum.BSDChecksum;
import denobo.compression.checksum.Checksum;
import denobo.compression.lzw.LZWDictionary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;

/**
 * A compressor that uses LZW coding to compress a set of bytes.
 * 
 * @author Saul Johnson
 */
public class LZWCompressor implements Compressor {

    /**
     * Returns the specified integer as a hex string at least two characters long.
     * 
     * @param data  the integer to convert 
     * @return      the specified integer as a hex string
     */
    private String toHex(int data) {
        final StringBuilder sb = new StringBuilder(Integer.toHexString(data));
        while (sb.length() < 2) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
    
    /**
     * Returns the specified 16-bit integer as a byte array.
     * 
     * @param value the 16-bit integer to convert
     * @return      the specified 16-bit integer as a byte array
     */
    private byte[] shortToByteArray(short value) {
        return ByteBuffer.allocate(2).putShort(value).array();
    }
    
    /**
     * Returns the specified byte array as a 16-bit integer.
     * 
     * @param data  the byte array integer to convert
     * @return      the specified byte array integer as a 16-bit integer
     */
    private short byteArrayToShort(byte[] data) {
        return ByteBuffer.allocate(2).put(data).getShort(0);
    }
    
    /**
     * Returns a dictionary initialised with entries for single byte values.
     * 
     * @return  a dictionary initialised with entries for single byte values
     */
    private LZWDictionary getInitializedDictionary() {
        final LZWDictionary dictionary = new LZWDictionary();        
        for (int i = 0; i < 256; i++) {
            dictionary.addData(toHex(i));
        }
        return dictionary;
    }
    
    /**
     * Converts a hexadecimal string to a byte array and returns it.
     * 
     * @param str   the hexadecimal string to convert
     * @return      a byte array representation of the hexadecimal string
     */
    private byte[] fromHexString(String str) {
        return DatatypeConverter.parseHexBinary(str);
    }
    
    /**
     * Returns the first byte of a hexadecimal string.
     * 
     * @param str   the hexadecimal string
     * @return      the first byte (i.e. first two characters) of the hex string
     */
    private String firstByte(String str) {
        return str.substring(0, 2);
    }
    
    @Override
    public byte[] compress(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            
            // Initialise dictionary.
            final LZWDictionary dictionary = getInitializedDictionary();

            // Get ouput stream.
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            // Write checksum byte to output.
            final Checksum checker = new BSDChecksum();
            out.write(checker.compute(data));
            
            String sequence = "";
            String byteString;

            int buffer;
            while ((buffer = in.read()) != -1) {

                byteString = toHex(buffer);
                sequence += byteString;

                if (!dictionary.hasData(sequence)) {
                    dictionary.addData(sequence);
                    sequence = sequence.substring(0, sequence.length() - 2);
                    out.write(shortToByteArray(dictionary.getCode(sequence)));
                    sequence = byteString;
                }

            }
            if (!sequence.isEmpty()) {
                out.write(shortToByteArray(dictionary.getCode(sequence)));
            }
         
            out.close();
            
            return out.toByteArray();
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println("Could not compress data.");
            return null;
            
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            
            // Initialise dictionary.
            final LZWDictionary dictionary = getInitializedDictionary();

            // Get checksum.
            final byte[] checksum = new byte[1];
            in.read(checksum);
            
            // Get I/O streams.
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            String sequence = "";
            
            final byte[] buffer = new byte[2];
            while (in.read(buffer) != -1) {
                final short k = byteArrayToShort(buffer);
                if (k > dictionary.getSize()) {
                    throw new IOException("Cannot reconstruct dictionary.");
                } else if (k == dictionary.getSize()) {
                    dictionary.addData(sequence + firstByte(sequence));
                } else if (!sequence.isEmpty()) {
                    dictionary.addData(sequence + firstByte(dictionary.getData(k)));
                }
                out.write(fromHexString(dictionary.getData(k)));
                sequence = dictionary.getData(k);
            }
            final byte[] payload = out.toByteArray();
         
            out.close();
            
            // Make sure our checksum matches.
            final Checksum checker = new BSDChecksum();
            if (checksum[0] != checker.compute(payload)[0]) {
                throw new IOException("Bad checksum.");
            }
            
            return payload;
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println("Could not decompress data: " + ex.getMessage());
            return null;
            
        }
    }
    
}
