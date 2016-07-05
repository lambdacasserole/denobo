package com.sauljohnson.denobo.encryption;

/**
 * Specifies that implementing classes support data encryption and decryption.
 *
 * @version 1.0 06 June 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface EncryptionProvider {

    /**
     * Returns an encrypted version of the specified byte array.
     * @param plaintext the plaintext bytes to encrypt
     * @return  an encrypted version of specified byte array
     */
    byte[] encrypt(byte[] plaintext);

    /**
     * Returns a decrypted version of the specified byte array.
     * @param ciphertext    the ciphertext bytes to decrypt
     * @return  an encrypted version of specified byte array
     */
    byte[] decrypt(byte[] ciphertext);
}