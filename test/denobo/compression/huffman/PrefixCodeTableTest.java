/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo.compression.huffman;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Saul Johnson
 */
public class PrefixCodeTableTest {
    
    public PrefixCodeTableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCodes method, of class PrefixCodeTable.
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
     * Test of getSymbols method, of class PrefixCodeTable.
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
     * Test of translateSymbol method, of class PrefixCodeTable.
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
     * Test of translateCode method, of class PrefixCodeTable.
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
     * Test of hasCode method, of class PrefixCodeTable.
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
     * Test of toString method, of class PrefixCodeTable.
     */
    @Test
    public void testToString() {
        
        
        
    }
    
}
