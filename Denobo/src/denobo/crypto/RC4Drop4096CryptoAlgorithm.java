package denobo.crypto;

/**
 * Provides RC4 stream cipher encryption/decryption that drops the first 4096
 * bytes from the key stream.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RC4Drop4096CryptoAlgorithm extends RC4CryptoAlgorithm {

    /**
     * Initialises a new instance of an RC4 encryption algorithm that discards
     * the first 4096 bits.
     */
    public RC4Drop4096CryptoAlgorithm() {
        super();
        drop = 4096;
    }
    
}
