package denobo.compression.huffman;

import static denobo.compression.huffman.Word.SIZE;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sequence of bits.
 * 
 * @author Saul Johnson
 */
public class BitSequence {

    /**
     * The words that make up the bit sequence.
     */
    private final List<Word> words;
    
    /**
     * The length of the bit sequence.
     */
    private int length;
    
    /**
     * Initialises a new instance of a bit sequence.
     */
    public BitSequence() {
        words = new ArrayList<>();
    }
    
    /**
     * Gets the specified bit in the sequence.
     * 
     * @param index the index of the bit to get
     * @return      true if the bit is one, otherwise false
     */
    public boolean getBit(int index) {
     
        // Bounds check.
        if (index < 0 || index >= length) {
            throw new RuntimeException("Cannot get bit " + index + " of bit sequence (length " + length + ").");
        }
        
        // Return bit.
        return getWordAtBitIndex(index).getBit(index % SIZE);
            
    }
    
    /**
     * Sets the specified bit in the sequence.
     * 
     * @param index the index of the bit to set
     * @param value the new value of the bit
     */
    public void setBit(int index, boolean value) {
        
        // Bounds check.
        if (index < 0 || index >= length) {
            throw new RuntimeException("Cannot set bit " + index + " of bit sequence (length " + length + ").");
        }
        
        // Set bit.
        getWordAtBitIndex(index).setBit(index % SIZE, value);
        
    }
    
    /**
     * Appends a word to this bit sequence.
     * @param word      the word to append
     * @param length    the length of the word, legal values are 1-8 (inclusive)
     */
    public void appendWord(Word word, int length) {
                
        // TODO: Make this more efficient.
        for (int i = 0; i < length; i++) {
            append(word.getBit(i));
        }
        
    }
            
    /**
     * Appends a bit onto the end of the sequence.
     * 
     * @param value the value of the bit to append
     */
    public void append(boolean value) {
        
        // Add another word if needed.
        if (length % SIZE == 0) { words.add(new Word()); }
        
        // Set last bit to desired value.
        getLastWord().setBit(length % SIZE, value);
        length++;
        
    }
    
    /**
     * Appends a {@link BitSequence} onto the end of this one.
     * 
     * @param value the {@link BitSequence} to append.
     */
    public void append(BitSequence value) {
        
        final Word[] wordsToAdd = value.getWords();
        
        int newSequenceLength = value.getLength();
        for (Word word : wordsToAdd) {
            appendWord(word, newSequenceLength >= SIZE ? SIZE : newSequenceLength);
            newSequenceLength -= SIZE;
        }
        
    }
    
    /**
     * Gets this word represented as a bit string.
     * 
     * @return  the word represented a as a bit string
     */
    public String toBitString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(getBit(i) ? "1" : "0");
        }
        return sb.toString();
    }
    
    /**
     * Gets the length of the bit sequence.
     * 
     * @return  the length of the bit sequence
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Gets the length of the sequence in words.
     * 
     * @return the length of the sequence in words
     */
    public int getLengthInWords() {
        return words.size();
    }
    
    /**
     * Gets the bit sequence as an array of bytes.
     * 
     * @return  a byte array that represents the bit sequence
     */
    public byte[] toArray() {
        final byte[] data = new byte[words.size()];
        for (int i = 0; i < words.size(); i++) {
            data[i] = words.get(i).getData();
        }
        return data;
    }
    
    /**
     * Gets the list of words that underlies the bit sequence.
     * 
     * @return  the array of words that underlies the bit sequence
     */
    public Word[] getWords() {
        return words.toArray(new Word[] {});
    }
    
    /**
     * Gets the word at the specified index.
     * 
     * @param index the index of the word to get
     * @return      the word at the specified index
     */
    public Word getWord(int index) {
        return words.get(index);
    }
    
    /**
     * Gets the last word in the sequence.
     * 
     * @return  the last word in the sequence
     */
    private Word getLastWord() {
        return words.get(words.size() - 1);
    }
    
    /**
     * Gets the word that contains the bit at the specified index.
     * 
     * @param index the bit index to get the word for
     * @return      the word that contains the bit at the specified index
     */
    private Word getWordAtBitIndex(int index) {
        return words.get(index / SIZE);
    }
    
    /**
     * Gets whether or not the given bit sequence is equal to the value of the bit sequence.
     * 
     * @param sequence  the bit sequence to compare
     * @return          true if the bit sequences are equal in value, otherwise false
     */
    public boolean equals(BitSequence sequence) {
        
        // Cannot be the same if different lengths.
        if (sequence.getLength() != getLength()) { return false; }
        
        // Compare word-by-word.
        for (int i = 0; i < words.size(); i++) {
            if (!words.get(i).equals(sequence.getWord(i))) {
                return false;
            }
        }
        return true;
        
    }
    
    /**
     * Creates a new bit sequence from a bit string.
     * 
     * @param str   the string from which to create the sequence
     * @return      the bit sequence
     */
    public static BitSequence fromBitString(String str) {
        
        final BitSequence sequence = new BitSequence();
        for (int i = 0; i < str.length(); i++) {
            sequence.append(str.charAt(i) == '1');
        }
        
        return sequence;
        
    }
    
    /**
     * Returns a reversed version of the specified bit sequence.
     * 
     * @param sequence  the sequence to reverse
     * @return          a reversed version of the specified bit sequence
     */
    public static BitSequence reverse(BitSequence sequence) {
        final StringBuilder sb = new StringBuilder(sequence.toBitString());
        return fromBitString(sb.reverse().toString());
    }
    
    
}
