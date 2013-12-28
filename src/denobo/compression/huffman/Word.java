package denobo.compression.huffman;

import java.util.Objects;

/**
 * Represents a word.
 */
public class Word {

    /**
     * The size in bits of a {@link Word}.
     */
    public static final int SIZE = Byte.SIZE;

    /**
     * The word data.
     */
    private byte value;

    /**
     * Initialises a new instance of a word.
     */
    public Word() {
        value = 0;
    }

    /**
     * Initialises a new instance of a word.
     * 
     * @param data  the initial value of the word
     */
    public Word(int data) {
        this.value = (byte) data;
    }

    /**
     * Initialises a new instance of a word.
     * 
     * @param word  the word to clone
     */
    public Word(Word word) {
        
        // Word cannot be null.
        Objects.requireNonNull(word, "Word cannot be null.");
        
        // Assign value.
        this.value = word.getValue();
        
    }
    
    /**
     * Sets the specified bit in the word to one or zero.
     * 
     * @param index the index of the bit to set
     * @param value the new value of the bit
     */
    public void setBit(int index, boolean value) {

        // Bounds check.
        if (index < 0 || index >= SIZE) {
            throw new RuntimeException("Cannot set bit [" + index + "] of " + SIZE + "-bit word.");
        }

        // Set specified bit.
        final byte mask = (byte) Math.pow(2, SIZE - index - 1);
        this.value = (byte) (value ? this.value | mask : this.value & ~mask);

    }

    /**
     * Gets the specified bit in the word.
     * 
     * @param index the index of the bit to get
     * @return      true if the bit is one, otherwise false
     */
    public boolean getBit(int index) {

        // Bounds check.
        if (index < 0 || index >= SIZE) {
            throw new RuntimeException("Cannot get bit [" + index + "] of " + SIZE + "-bit word.");
        }

        // Get specified bit.
        return (value & (byte) Math.pow(2, SIZE - index - 1)) != 0;

    }

    /**
     * Makes this word equal to itself AND the specified byte.
     * 
     * @param value the byte to AND with this word
     */
    public void and(byte value) {
        this.value = (byte) (this.value & value);
    }
    
    /**
     * Makes this word equal to itself AND the specified word.
     * 
     * @param word  the word to AND with this word
     */
    public void and(Word word) {
        and(word.getValue());
    }

    /**
     * Makes this word equal to itself OR the specified byte.
     * 
     * @param value the byte to OR with this word
     */
    public void or(byte value) {
        this.value = (byte) (this.value | value);
    }

    /**
     * Makes this word equal to itself OR the specified word.
     * 
     * @param word the word to OR with this word
     */
    public void or(Word word) {
        or(word.getValue());
    }
    
    /**
     * Makes this word equal to itself XOR the specified byte.
     * 
     * @param value the byte to XOR with this word
     */
    public void xor(byte value) {
        this.value = (byte) (this.value ^ value);
    }
    
    /**
     * Makes this word equal to itself XOR the specified word.
     * 
     * @param word  the word to XOR with this word
     */
    public void xor(Word word) {
        xor(word.getValue());
    }
    
    /**
     * Gets the value of the word as a byte.
     * 
     * @return  the value of the word as a byte
     */
    public byte getValue() {
        return value;
    }

    /**
     * Gets this word represented as a bit string.
     * 
     * @return  the word represented a as a bit string
     */
    public String toBitString() {
        
        // Build string.
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            sb.append(getBit(i) ? "1" : "0");
        }
        return sb.toString();
        
    }

    /**
     * Gets whether or not the given word is equal to the value of the word.
     * 
     * @param word  the word to compare
     * @return      true if the words are equal in value, otherwise false
     */
    public boolean equals(Word word) {
        
        // This word is not null.
        if (word == null) {
            return false;
        }
        
        // Compare by value.
        return word.getValue() == value;
        
    }

}
