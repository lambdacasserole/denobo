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
        
    /**
     * Returns a randomly-generated ID string.
     * 
     * @return  a random ID string
     */
    private static String getRandomId() {
        final Random rand = new Random();
        byte[] buffer = new byte[128];
        rand.nextBytes(buffer);
        return String.format("%02x", buffer);
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
