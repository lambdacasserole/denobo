package denobo.crypto;

/**
 * A non-functioning crypto algorithm that neither encrypts nor decrypts data.
 * 
 * @author Saul Johnson
 */
public class DummyCryptoAlgorithm implements CryptoAlgorithm {

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return plaintext;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        return ciphertext;
    }

}
