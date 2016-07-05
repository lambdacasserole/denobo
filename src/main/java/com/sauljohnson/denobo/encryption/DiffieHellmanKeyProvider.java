package com.sauljohnson.denobo.encryption;

/**
 * Specifies that implementing classes support Diffie-Hellman key calculation.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface DiffieHellmanKeyProvider {

    /**
     * Returns a random 1568-bit byte array that represents a private Diffie-Hellman key.
     * @return  a random large integer
     */
    byte[] generatePrivateKey();

    /**
     * Returns a public key generated from a given private key.
     * @param privateKey    the private key from which to generate the public key
     * @return  a public key generated from a given private key
     */
    byte[] generatePublicKey(byte[] privateKey);

    /**
     * Returns a shared key generated from another party's public key and a local private key.
     * @param publicKey     the third-party public key
     * @param privateKey    the local private key
     * @return  a shared key generated from the public and private keys
     */
    byte[] generateSharedKey(byte[] publicKey, byte[] privateKey);
}
