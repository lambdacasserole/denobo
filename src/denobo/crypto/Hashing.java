package denobo.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains helper methods for cryptographic hashing.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Hashing {
    
    /**
     * Gets the SHA256 hash of the given string.
     * 
     * @param str   the string to hash
     * @return      a SHA256 hash of the given string
     */
    public static String sha256(String str) {
        
        try {
            
            final MessageDigest digester = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digester.digest(str.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder(64);

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
    
}
