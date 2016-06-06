package com.sauljohnson.denobo.compression;

import com.sauljohnson.lizard.LzwCompressor;

import java.io.IOException;

/**
 * A compression provider that uses LZW compression to compress a set of bytes.
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson
 */
public class LzwCompressionProvider implements CompressionProvider {

    /** The underlying compressor. */
    private LzwCompressor compressor;

    /**
     * Initializes a new instance of a compression provider that uses LZW compression to compress a set of bytes.
     */
    public LzwCompressionProvider() {
        compressor = new LzwCompressor();
    }

    public String getName() {
        return "lzw";
    }

    public byte[] compress(byte[] data) throws IOException {
        return compressor.compress(data);
    }

    public byte[] decompress(byte[] data) throws IOException {
        return compressor.decompress(data);
    }
}
