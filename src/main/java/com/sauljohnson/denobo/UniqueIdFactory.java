package com.sauljohnson.denobo;

import java.util.Random;

/**
 * A factory class to create locally unique identifier strings.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class UniqueIdFactory {
    
    /**
     * Holds the incremental ID used to calculate each unique hash.
     */
    private static int incrementalId = 0;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Returns a randomly-generated ID string.
     * 
     * @return  a random ID string
     */
    private static String getRandomId() {
        final Random rand = new Random();
        byte[] buffer = new byte[128];
        rand.nextBytes(buffer);
        return bytesToHex(buffer);
    }
    
    /**
     * Returns a random ID string that is likely to be globally unique.
     * 
     * @return  a random ID string that is likely to be globally unique.
     */
    public static String getId() {
        return getRandomId();
    }
        
}
