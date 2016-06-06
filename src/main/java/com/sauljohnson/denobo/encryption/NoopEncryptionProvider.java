package com.sauljohnson.denobo.encryption;

/**
 * A compression provider that does not perform any encryption or decryption transformation.
 *
 * @version 1.0 06 June 2016
 * @author  Saul Johnson
 */
public class NoopEncryptionProvider implements EncryptionProvider {

    public byte[] encrypt(byte[] plaintext) {
        return plaintext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        return ciphertext;
    }
}
