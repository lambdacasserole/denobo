package denobo.compression.huffman;

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
    byte data;

    /**
     * Initialises a new instance of a word.
     */
    public Word() {
        data = 0;
    }

    /**
     * Initialises a new instance of a word.
     * 
     * @param data  the initial value of the word
     */
    public Word(int data) {
        this.data = (byte) data;
    }

    /**
     * Initialises a new instance of a word.
     * 
     * @param word  the word to clone
     */
    public Word(Word word) {
        this.data = word.getData();
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
        data = (byte) (value ? data | mask : data & ~mask);

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
        return (data & (byte) Math.pow(2, SIZE - index - 1)) != 0;

    }

    /**
     * Makes this word equal to itself AND the specified byte.
     * 
     * @param b the byte to AND with this word
     */
    public void and(byte b) {
        data = (byte) (data & b);
    }
    
    /**
     * Makes this word equal to itself AND the specified word.
     * 
     * @param w the word to AND with this word
     */
    public void and(Word w) {
        and(w.getData());
    }

    /**
     * Makes this word equal to itself OR the specified byte.
     * 
     * @param b the byte to OR with this word
     */
    public void or(byte b) {
        data = (byte) (data | b);
    }

    /**
     * Makes this word equal to itself OR the specified word.
     * 
     * @param w the word to OR with this word
     */
    public void or(Word w) {
        or(w.getData());
    }
    
    /**
     * Makes this word equal to itself XOR the specified byte.
     * 
     * @param b the byte to XOR with this word
     */
    public void xor(byte b) {
        data = (byte) (data ^ b);
    }
    
    /**
     * Makes this word equal to itself XOR the specified word.
     * 
     * @param w the word to XOR with this word
     */
    public void xor(Word w) {
        xor(w.getData());
    }
    
    /**
     * Gets the value of the word as a byte.
     * 
     * @return  the value of the word as a byte
     */
    public byte getData() {
        return data;
    }

    /**
     * Gets this word represented as a bit string.
     * 
     * @return  the word represented a as a bit string
     */
    public String toBitString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
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
        return word.getData() == data;
    }

}
