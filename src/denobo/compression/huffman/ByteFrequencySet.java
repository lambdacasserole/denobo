package denobo.compression.huffman;

import denobo.FileIO;
import java.io.File;

/**
 * Represents a set of byte occurrence frequencies.
 * 
 * @author Saul Johnson
 */
public class ByteFrequencySet {
   
    /**
     * The array of occurrence frequencies that underlies the set.
     */
    private final double[] frequencies;
        
    /**
     * Initialises a new instance of a byte frequency set.
     * 
     * @param data  the data from which to initialise the frequency set
     */
    public ByteFrequencySet(byte[] data) {
        
        frequencies = new double[256];
        final double unit = 1.00d / (double) data.length;
        for (byte b : data) {
            frequencies[b & 0xFF] += unit;
        }
        
    }
    
    /**
     * Gets the size of the frequency set.
     * 
     * @return  the size of the frequency set
     */
    public int getSize() {
        return frequencies.length;
    }
    
    /**
     * Gets the occurrence frequency of the specified byte.
     * 
     * @param index the byte for which to get the occurrence frequency
     * @return      the occurrence frequency of the specified byte
     */
    public double getUnsignedByteFrequency(int index) {
        return frequencies[index];
    }
    
    /**
     * Calculates the byte occurrence frequencies in a file and returns the
     * results as a {@link ByteFrequencySet}.
     * 
     * @param file  the file for which to calculate byte frequencies
     * @return      a {@link ByteFrequencySet} for the specified file
     */
    public static ByteFrequencySet fromFile(File file) {
        return new ByteFrequencySet(FileIO.readBytesFromFile(file));
    }
    
}
