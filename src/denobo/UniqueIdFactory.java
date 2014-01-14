package denobo;

import denobo.crypto.Hashing;
import java.util.Random;

/**
 * A factory class to create locally unique identifier strings.
 * 
 * @author Saul Johnson
 */
public class UniqueIdFactory {
    
    /**
     * Holds the incremental ID used to calculate each unique hash.
     */
    private static int incrementalId = 0;
        
    private static String getRandomId() {
        final Random rand = new Random();
        return Hashing.sha256(Integer.toString(rand.nextInt(2048)) 
                + Long.toString(System.currentTimeMillis()) 
                + Integer.toString(incrementalId++));
    }
    
    public static String getId() {
        return getRandomId();
    }
        
}
