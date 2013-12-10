package denobo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
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
    
    /**
     * Holds a hash set of used ID strings to ensure no duplicates are dispensed.
     */
    private static HashSet<String> usedStrings = new HashSet<>();
    
    /**
     * Gets a SHA256 hash of the given string.
     * 
     * @param str   the string to hash
     * @return      a SHA256 hash of the given string
     */
    private static String sha256(String str) {
        
        try {
            
            final MessageDigest digester = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digester.digest(str.getBytes("UTF-8"));
            final StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
            
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            
           System.out.println(ex.getMessage());
           return null;
           
        }
        
    }
    
    private static String getRandomId() {
        Random rand = new Random();
        return sha256(Integer.toString(rand.nextInt(2048)) 
                + Long.toString(System.currentTimeMillis()) 
                + Integer.toString(incrementalId++));
    }
    
    public static String getId() {
        String buffer = getRandomId();
        while(usedStrings.contains(buffer)) {
            buffer = getRandomId();
        }
        usedStrings.add(buffer);
        return buffer;
    }
        
}
