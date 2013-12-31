package denobo.crypto;

/**
 * Specifies that implementing classes support data encryption and decryption.
 * 
 * @author Saul Johnson
 */
public interface CryptoAlgorithm {
    
    /**
     * Returns an encrypted version of the specified byte array using the algorithm's
     * current parameters.
     * 
     * @param plaintext the plaintext bytes to encrypt
     * @return          an encrypted version of specified byte array
     */
    public byte[] encrypt(byte[] plaintext);
    
    /**
     * Returns a decrypted version of the specified byte array using the algorithm's
     * current parameters.
     * 
     * @param ciphertext    the ciphertext bytes to decrypt
     * @return              an encrypted version of specified byte array
     */
    public byte[] decrypt(byte[] ciphertext);
    
}
