package com.sauljohnson.denobo.compression;

import java.io.IOException;

/**
 * A compression provider that does not perform any compression or decompression transformation.
 *
 * @version 1.0 06 June 2016
 * @author  Saul Johnson
 */
public class NoopCompressionProvider implements CompressionProvider {

    public String getName() {
        return "noop";
    }

    public byte[] compress(byte[] data) throws IOException {
        return data;
    }

    public byte[] decompress(byte[] data) throws IOException {
        return data;
    }
}
