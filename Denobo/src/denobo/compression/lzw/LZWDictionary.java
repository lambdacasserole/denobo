package denobo.compression.lzw;

import java.util.HashMap;

/**
 * Represents a bidirectional mapping dictionary used for LZW compression.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class LZWDictionary {
    
    /**
     * Maps hexadecimal strings to 16-bit data codes.
     */
    private final HashMap<String, Short> dataToCodes;
    
    /**
     * Maps 16-bit data codes to hexadecimal strings.
     */
    private final HashMap<Short, String> codesToData;
            
    /**
     * Initialises a new instance of an LZW compression dictionary.
     */
    public LZWDictionary() {
        dataToCodes = new HashMap<>();
        codesToData = new HashMap<>();
    }
    
    /**
     * Adds a hexadecimal string to the end of this dictionary.
     * 
     * @param data  the string to add
     */
    public void addData(String data) {
        if (isFull()) {
            throw new RuntimeException("Compression dictoinary is full.");
        }
        dataToCodes.put(data, (short) dataToCodes.size());
        codesToData.put((short) codesToData.size(), data);
    }
    
    /**
     * Gets whether or not the dictionary has an entry with data matching the 
     * specified hexadecimal string.
     * 
     * @param data  the string to check for
     * @return      true if dictionary has matching data, otherwise false
     */
    public boolean hasData(String data) {
        return dataToCodes.containsKey(data);
    }
    
    /**
     * Gets the 16-bit encoded integer associated with the specified hexadecimal
     * string.
     * 
     * @param data  the string to check for
     * @return      the associated 16-bit encoded integer
     */
    public Short getCode(String data) {
        return dataToCodes.get(data);
    }
    
    /**
     * Gets the hexadecimal string associated with the specified 16-bit encoded 
     * integer.
     * 
     * @param value the 16-bit encoded integer to check for
     * @return      the associated hexadecimal string
     */
    public String getData(short value) {
        return codesToData.get(value);
    }
    
    /**
     * Gets the size of the dictionary.
     * 
     * @return  the size of the dictionary
     */
    public int getSize() {
        return dataToCodes.size();
    }
    
    /**
     * Gets whether or not the dictionary is full.
     * 
     * @return  true if the dictionary is full, otherwise false
     */
    public boolean isFull() {
        return dataToCodes.size() == Short.MAX_VALUE;
    }
    
}
