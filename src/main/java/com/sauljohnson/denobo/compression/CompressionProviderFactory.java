package com.sauljohnson.denobo.compression;

/**
 * A static factory for implementors of the {@link CompressionProvider} interface.
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson
 */
public class CompressionProviderFactory {

    /**
     * Returns a new instance of a compression provider that uses the format with the specified name.
     * @param name  the name of the format to use
     * @return  a new compression provider
     */
    public static CompressionProvider instantiate(String name) {

        // Manually map names to formats.
        if (name.equals("elpremi")) {
            return new ElpremiCompressionProvider();
        }
        else if (name.equals("lzw")) {
            return new LzwCompressionProvider();
        }
        return new NoopCompressionProvider(); // Fall back to no compression.
    }
}
