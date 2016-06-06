package com.sauljohnson.denobo.encryption;

import com.sauljohnson.arcy.Rc4Cipher;

/**
 * An encryption provider that uses the RC4 algorithm.
 *
 * @version 1.0 06 June 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Rc4EncryptionProvider implements EncryptionProvider {

    /** The underlying cipher. **/
    private Rc4Cipher cipher;

    /**
     * Initializes a new instance of an encryption provider that uses the RC4 algorithm.
     * @param key   the key to schedule the RC4 cipher with
     */
    public Rc4EncryptionProvider(byte[] key) {
        cipher = new Rc4Cipher(byteArrayToIntArray(key));
    }

    /**
     * Converts a byte array to an int array.
     * @param arr   the array to convert
     * @return  the converted array
     */
    private static int[] byteArrayToIntArray(byte[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            out[i] = arr[i];
        }
        return out;
    }

    /**
     * Converts an int array to a byte array.
     * @param arr   the array to convert
     * @return  the converted array
     */
    private static byte[] intArrayToByteArray(int[] arr) {
        byte[] out = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            out[i] = (byte) (arr[i] & 0xFF);
        }
        return out;
    }

    public byte[] encrypt(byte[] plaintext) {
        return intArrayToByteArray(cipher.encrypt(byteArrayToIntArray(plaintext)));
    }

    public byte[] decrypt(byte[] ciphertext) {
        return intArrayToByteArray(cipher.decrypt(byteArrayToIntArray(ciphertext)));
    }
}
