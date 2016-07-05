package com.sauljohnson.denobo.encryption;

import java.math.BigInteger;

import com.sauljohnson.mayo.DiffieHellmanKeyGenerator;

/**
 * A Diffie-Hellman key provider that uses the Mayo library.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson
 */
public class MayoDiffieHellmanKeyProvider implements DiffieHellmanKeyProvider {

    public byte[] generatePrivateKey() {
        // Convert to byte array
        return DiffieHellmanKeyGenerator.generatePrivateKey().toByteArray();
    }

    public byte[] generatePublicKey(byte[] privateKey) {
        // Convert to big integer for calculation, the back to byte array.
        BigInteger numericKey = new BigInteger(privateKey);
        return DiffieHellmanKeyGenerator.generatePublicKey(numericKey).toByteArray();
    }

    public byte[] generateSharedKey(byte[] publicKey, byte[] privateKey) {
        // Convert to big integers for calculation, the back to byte array.
        return DiffieHellmanKeyGenerator.generateSharedKey(new BigInteger(publicKey),
                new BigInteger(privateKey)).toByteArray();
    }
}
