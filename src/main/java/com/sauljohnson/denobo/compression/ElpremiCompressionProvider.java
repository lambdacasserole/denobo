package com.sauljohnson.denobo.compression;

import com.sauljohnson.elpremi.ElpremiCompressor;

import java.io.IOException;

/**
 * A compression provider that uses the Elpremi format to compress a set of bytes.
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson
 */
public class ElpremiCompressionProvider implements CompressionProvider {

    /** The underlying compressor. */
    private ElpremiCompressor compressor;

    /**
     * Initializes a new instance of a compression provider that uses the Elpremi format to compress a set of bytes.
     */
    public ElpremiCompressionProvider() {
        compressor = new ElpremiCompressor();
    }

    public String getName() {
        return "elpremi";
    }

    public byte[] compress(byte[] data) throws IOException {
        return compressor.compress(data);
    }

    public byte[] decompress(byte[] data) throws IOException {
        return compressor.decompress(data);
    }
}
