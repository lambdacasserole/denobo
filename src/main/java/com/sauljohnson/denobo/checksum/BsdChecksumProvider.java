package com.sauljohnson.denobo.checksum;

import com.sauljohnson.checkers.BsdChecksumCalculator;

/**
 * A checksum provider that uses the BSD checksum algorithm.
 *
 * @version 1.0 05 June 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class BsdChecksumProvider implements ChecksumProvider {

    /** The underlying checksum calculator. **/
    private BsdChecksumCalculator calculator;

    /**
     * Initializes a new instance of a checksum provider that uses the BSD checksum algorithm.
     */
    public BsdChecksumProvider() {
        calculator = new BsdChecksumCalculator();
    }

    public String getName() {
        return "bsd";
    }

    public byte[] compute(byte[] data) {
        return calculator.compute(data);
    }
}
