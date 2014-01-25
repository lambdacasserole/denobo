package denobo.compression.huffman;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link BitSequence}.
 * 
 * @author Saul Johnson
 */
public class BitSequenceTest {
    
    /**
     * Initialises a new instance of a unit test for {@link BitSequence}.
     */
    public BitSequenceTest() {
        
        // Nothing to do.
        
    }
    
    /**
     * Run before this unit test.
     */
    @BeforeClass
    public static void setUpClass() {
        
        // Nothing to do.
        
    }
    
    /**
     * Run after this unit test.
     */
    @AfterClass
    public static void tearDownClass() {
        
        // Nothing to do.
        
    }

    /**
     * Run before each test case.
     */
    @Before
    public void setUp() {
        
        // Nothing to do.
        
    }
    
    /**
     * Run after each test case.
     */
    @After
    public void tearDown() {
        
        // Nothing to do.
        
    }

    /**
     * Test of getBit method, of class {@link BitSequence}.
     */
    @Test
    public void testGetBit() {
      
        final BitSequence instance = new BitSequence();
        instance.append(true);
        instance.setBit(0, false);
        
        assertFalse(instance.getBit(0));
        
    }

    /**
     * Test of setBit method, of class {@link BitSequence}.
     */
    @Test
    public void testSetBit() {
        
        final BitSequence instance = new BitSequence();
        instance.append(true);
        instance.append(false);
        instance.append(true);
        
        final boolean result = instance.getBit(0) 
                && !instance.getBit(1) 
                && instance.getBit(2);
        
        assertTrue(result);
        
    }

    /**
     * Test of appendWord method, of class {@link BitSequence}.
     */
    @Test
    public void testAppendWord() {
        
        final BitSequence instance = new BitSequence();
       
        final Word firstWord = new Word(77);
        final Word secondWord = new Word(-64);
        final Word thirdWord = new Word(-1);
        
        instance.appendWord(firstWord, 6);
        instance.appendWord(secondWord, 8);
        instance.appendWord(thirdWord, 4);
        
        final String expectedResult = "010011110000001111";
        assertEquals(instance.toBitString(), expectedResult);
        
    }

    /**
     * Test of append method, of class {@link BitSequence}.
     */
    @Test
    public void testAppendboolean() {
        
        final BitSequence instance = new BitSequence();
        instance.append(true);
        instance.append(true);
        instance.append(true);
        instance.append(false);
        
        final String expectedResult = "1110";
        assertEquals(instance.toBitString(), expectedResult);
        
    }

    /**
     * Test of append method, of class {@link BitSequence}.
     */
    @Test
    public void testAppendBitSequence() {
        
        final BitSequence firstBitSequence = new BitSequence();
        firstBitSequence.appendWord(new Word(0), 8);
        firstBitSequence.appendWord(new Word(-1), 7);
        
        final BitSequence secondBitSequence = new BitSequence();
        firstBitSequence.appendWord(new Word(-128), 6);
        firstBitSequence.appendWord(new Word(127), 5);
        
        final String expectedResult = "00000000111111110000001111";
        
        firstBitSequence.append(secondBitSequence);
        assertEquals(expectedResult, firstBitSequence.toBitString());
        
    }

    /**
     * Test of toBitString method, of class {@link BitSequence}.
     */
    @Test
    public void testToBitString() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(64), 8);
        instance.appendWord(new Word(-1), 6);
        
        final String expectedResult = "01000000111111";
        
        assertEquals(instance.toBitString(), expectedResult);
        
    }

    /**
     * Test of getLength method, of class {@link BitSequence}.
     */
    @Test
    public void testGetLength() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(64), 8);
        instance.appendWord(new Word(-1), 6);
        instance.append(true);
        
        final int expectedResult = 15;
        assertEquals(instance.getLength(), expectedResult);
        
    }

    /**
     * Test of getLengthInWords method, of class {@link BitSequence}.
     */
    @Test
    public void testGetLengthInWords() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(64), 8);
        instance.appendWord(new Word(-1), 6);
        instance.append(true);
        
        final int expectedResult = 2;
        assertEquals(instance.getLengthInWords(), expectedResult);
        
    }

    /**
     * Test of getData method, of class {@link BitSequence}.
     */
    @Test
    public void testToArray() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(0), 8);
        instance.appendWord(new Word(-1), 4);
        instance.append(true);
     
        final byte[] output = instance.toArray();
        assertTrue(output[0] == 0 && output[1] == -8);
        
    }

    /**
     * Test of getWords method, of class {@link BitSequence}.
     */
    @Test
    public void testGetWords() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(0), 8);
        instance.appendWord(new Word(-1), 7);
        instance.append(true);
        instance.append(true);
     
        final Word[] words = instance.getWords();
        assertEquals(words.length, 3);
        
    }

    /**
     * Test of getWord method, of class {@link BitSequence}.
     */
    @Test
    public void testGetWord() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(0), 8);
        instance.appendWord(new Word(-1), 7);
        instance.append(true);
        instance.append(true);
        
        assertEquals(instance.getWord(2).getValue(), -128);
        
    }

    /**
     * Test of equals method, of class {@link BitSequence}.
     */
    @Test
    public void testEquals() {
        
        final BitSequence firstInstance = new BitSequence();
        firstInstance.appendWord(new Word(-128), 8);
        firstInstance.appendWord(new Word(-1), 5);
        
        final BitSequence secondInstance = new BitSequence();
        secondInstance.appendWord(new Word(-128), 8);
        secondInstance.appendWord(new Word(-1), 5);
        
        final BitSequence thirdInstance = new BitSequence();
        thirdInstance.appendWord(new Word(64), 8);
        thirdInstance.appendWord(new Word(32), 5);
        
        final BitSequence fourthInstance = new BitSequence();
        fourthInstance.appendWord(new Word(-128), 8);
        fourthInstance.appendWord(new Word(-1), 4);
        
        assertFalse(firstInstance.equals(thirdInstance));
        assertFalse(firstInstance.equals(fourthInstance));
        assertTrue(firstInstance.equals(secondInstance));
        
    }
    
    /**
     * Test of reverse method, of class {@link BitSequence}.
     */
    @Test
    public void testReverse() {
        
        final BitSequence instance = new BitSequence();
        instance.appendWord(new Word(-128), 8);
        instance.appendWord(new Word(3), 8);
        
        final BitSequence reversedInstance = BitSequence.reverse(instance);
        
        final String expectedResult = "1100000000000001";
        assertEquals(expectedResult, reversedInstance.toBitString());
        
    }
    
}
