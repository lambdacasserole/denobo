package denobo.crypto;

/**
 * Provides RC4 stream cipher encryption/decryption that drops the first 4096
 * bytes from the key stream.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RC4Drop4096CryptoAlgorithm extends RC4CryptoAlgorithm {

    public RC4Drop4096CryptoAlgorithm() {
        super();
        drop = 4096;
    }
    
}
