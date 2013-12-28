package denobo.compression.huffman;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link Word}.
 * 
 * @author Saul Johnson
 */
public class WordTest {
    
    /**
     * Initialises a new instance of a unit test for {@link Word}.
     */
    public WordTest() {
        
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
     * Test of {@link Word#setBit} method, of class {@link Word}.
     */
    @Test
    public void testSetBit() {
        
        final Word instance = new Word(0);
        instance.setBit(0, true);
        instance.setBit(1, true);
        instance.setBit(7, true);
        instance.setBit(1, false);
        
        final String expResult = "10000001";
        final String result = instance.toBitString();
        
        assertEquals(result, expResult);
        
    }

    /**
     * Test of {@link Word#getBit} method, of class {@link Word}.
     */
    @Test
    public void testGetBit() {
        
        final Word instance = new Word(0);
        instance.setBit(0, true);
        instance.setBit(1, true);
        instance.setBit(7, true);
        instance.setBit(1, false);
        
        assertTrue(instance.getBit(0) && instance.getBit(7) && !instance.getBit(1));
       
    }

    /**
     * Test of {@link Word#and} method, of class {@link Word}.
     */
    @Test
    public void testAnd() {
        
        final Word instance = new Word(Integer.parseInt("00010000", 2));
        final Word mask = new Word(Integer.parseInt("11111111", 2));
        
        instance.and(mask);
        
        final String expResult = "00010000";
        assertEquals(expResult, instance.toBitString());
        
    }

    /**
     * Test of {@link Word#or} method, of class {@link Word}.
     */
    @Test
    public void testOr() {
        
        final Word instance = new Word(Integer.parseInt("10101010", 2));
        final Word mask = new Word(Integer.parseInt("01010101", 2));
        
        instance.or(mask);
        
        final String expResult = "11111111";
        assertEquals(expResult, instance.toBitString());
        
    }

    /**
     * Test of {@link Word#xor} method, of class {@link Word}.
     */
    @Test
    public void testXor() {
        
        final Word instance = new Word(Integer.parseInt("10101010", 2));
        final Word mask = new Word(Integer.parseInt("11111111", 2));
        
        instance.xor(mask);
        
        final String expResult = "01010101";
        assertEquals(expResult, instance.toBitString());
        
    }
    
    /**
     * Test of {@link Word#getValue} method, of class {@link Word}.
     */
    @Test
    public void testGetData() {
        
        final int expResult = 10;
        final Word instance = new Word(expResult);
        
        assertEquals(expResult, instance.getValue());
        
    }

    /**
     * Test of {@link Word#toBitString} method, of class {@link Word}.
     */
    @Test
    public void testToBitString() {
        
        final Word instance = new Word(10);
        final String expResult = "00001010";
        
        assertEquals(expResult, instance.toBitString());
        
    }

    /**
     * Test of {@link Word#equals} method, of class {@link Word}.
     */
    @Test
    public void testEquals() {
        
        final Word instance = new Word(100);
        final Word compare = new Word(100);
        
        assertTrue(instance.equals(compare));
        
    }
    
}
