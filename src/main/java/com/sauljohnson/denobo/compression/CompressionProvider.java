package com.sauljohnson.denobo.compression;

import java.io.IOException;

/**
 * Specifies that implementing classes support data compression and decompression.
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface CompressionProvider {

    /**
     * Gets the name of the compression provider.
     * @return  the name of the compression provider
     */
    String getName();

    /**
     * Compresses an array of bytes.
     * @param data  the byte array to compress
     * @return  a compressed array of bytes
     */
    byte[] compress(byte[] data) throws IOException;

    /**
     * Decompresses an array of bytes.
     * @param data  the byte array to decompress
     * @return  a decompressed array of bytes
     */
    byte[] decompress(byte[] data) throws IOException;
}