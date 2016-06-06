package com.sauljohnson.denobo.checksum;

/**
 * Specifies that implementing classes support checksum computation
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface ChecksumProvider {

    /**
     * Gets the name of the checksum provider.
     * @return  the name of the checksum provider
     */
    String getName();

    /**
     * Computes the checksum for an array of bytes.
     * @param data  the byte array to compute the checksum for
     * @return  the checksum for the array of bytes
     */
    byte[] compute(byte[] data);
}
