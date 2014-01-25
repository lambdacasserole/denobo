package denobo.compression.huffman;

import denobo.FileIO;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link PrefixCodeTable}.
 * 
 * @author Saul Johnson
 */
public class PrefixCodeTableTest {
    
    /**
     * Initialises a new instance of a unit test for {@link PrefixCodeTable}.
     */
    public PrefixCodeTableTest() {
        
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
     * Test of {@link PrefixCodeTable#getCodes} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testGetCodes() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        assertArrayEquals(instance.getCodes(), codes);
        
    }

    /**
     * Test of {@link PrefixCodeTable#getSymbols} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testGetSymbols() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        assertArrayEquals(instance.getSymbols(), symbols);
        
    }

    /**
     * Test of {@link PrefixCodeTable#translateSymbol} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testTranslateSymbol() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        
        final String expectedResult = "00000010";
        assertEquals(expectedResult, instance.translateSymbol(3).toBitString());
        
    }

    /**
     * Test of {@link PrefixCodeTable#translateCode} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testTranslateCode() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        
        final BitSequence codeToTranslate = new BitSequence();
        codeToTranslate.appendWord(new Word(2), 8);
        
        final int expectedResult = 3;
        assertEquals(expectedResult, instance.translateCode(codeToTranslate));
        
    }

    /**
     * Test of {@link PrefixCodeTable#hasCode} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testHasCode() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        
        final BitSequence hasThisCode = new BitSequence();
        hasThisCode.appendWord(new Word(1), 8);
        assertTrue(instance.hasCode(hasThisCode));
        
        final BitSequence notThisCode = new BitSequence();
        notThisCode.appendWord(new Word(10), 8);
        assertFalse(instance.hasCode(notThisCode));
        
        final BitSequence emptyCode = new BitSequence();
        assertFalse(instance.hasCode(emptyCode));
        
    }

    /**
     * Test of {@link PrefixCodeTable#toString} method, of class {@link PrefixCodeTable}.
     */
    @Test
    public void testToString() {
        
        final int[] symbols = new int[] {1, 2, 3};
        final BitSequence[] codes = new BitSequence[3];
        
        for (int i = 0; i < codes.length; i++) {
            final BitSequence seq = new BitSequence();
            seq.appendWord(new Word(i), 8);
            codes[i] = seq;
        }
        
        final PrefixCodeTable instance = new PrefixCodeTable(symbols, codes);
        final String expectedResult = FileIO.readTextFromFile(new File("data/prefix_code_table_expected.txt"));
        
        assertEquals(expectedResult, instance.toString());
        
    }
    
}
