package denobo.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

/**
 * A helper class for generating keys based on a Diffie-Hellman key exchange.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DiffieHellmanKeyGenerator {
    
    /**
     * The large prime used for modular arithmetic.
     */
    private final static BigInteger SHARED_LARGE_PRIME = 
            new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" 
            + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" 
            + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
            + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" 
            + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" 
            + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" 
            + "83655D23DCA3AD961C62F356208552BB9ED529077096966D" 
            + "670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF", 16);
    
    /**
     * The generator used for modular arithmetic.
     */
    private final static BigInteger GENERATOR = new BigInteger("2");
    
    /**
     * Returns a random 1568-bit integer that represents a private 
     * Diffie-Wellman key.
     * 
     * @return  a random large integer
     */
    public static BigInteger generatePrivateKey() {
        final byte[] largeNumber = new byte[196];
        final Random random = new Random();
        random.nextBytes(largeNumber);
        return new BigInteger(largeNumber);
    }
    
    /**
     * Returns a public key generated from a given private key.
     * 
     * @param privateKey    the private key from which to generate the public
     *                      key
     * @return              a public key generated from a given private key
     */
    public static BigInteger generatePublicKey(BigInteger privateKey) {
        return GENERATOR.modPow(privateKey, SHARED_LARGE_PRIME);
    }
    
    /**
     * Returns a shared key generated from another party's public key and a 
     * local private key.
     * 
     * @param publicKey     the third-party public key
     * @param privateKey    the local private key
     * @return              a shared key generated from the public and private 
     *                      keys
     */
    public static BigInteger generateSharedKey(BigInteger publicKey, BigInteger privateKey) {
        return publicKey.modPow(privateKey, SHARED_LARGE_PRIME);
    }

    /**
     * Generates an array of integers for use as a key with certain 
     * {@link CryptoAlgorithm} implementations (for example 
     * {@link RC4CryptoAlgorithm}) using a shared secret key.
     * 
     * @param sharedKey the shared secret key from which to generate the array
     * @return          an array of integers for use as a key with certain 
     *                  encryption algorithms
     */
    public static int[] generateEncryptionKey(BigInteger sharedKey) {
        try {
            final String cryptoKey = Hashing.sha256(sharedKey.toString());
            final byte[] cryptoKeyBytes = cryptoKey.getBytes("US-ASCII");
            final int[] cryptoKeyInts = new int[cryptoKeyBytes.length];
            for (int i = 0; i < cryptoKeyBytes.length; i++) {
                cryptoKeyInts[i] = cryptoKeyBytes[i];
            }
            return cryptoKeyInts;
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Key generation did not support encoding: " + ex.getMessage());
            return null;
        }
    }
    
}
