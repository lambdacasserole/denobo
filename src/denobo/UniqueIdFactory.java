package denobo;

import denobo.crypto.Hashing;
import java.util.Random;

/**
 * A factory class to create locally unique identifier strings.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
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
        return Hashing.sha256(Integer.toString(rand.nextInt(2048)) 
                + Long.toString(System.currentTimeMillis()) 
                + Integer.toString(incrementalId++));
    }
    
    /**
     *Returns a random ID string that is likely to be globally unique.
     * 
     * @return  a random ID string that is likely to be globally unique.
     */
    public static String getId() {
        return getRandomId();
    }
        
}
