package denobo.compression.huffman;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link BitInputStream}.
 * 
 * @author Saul Johnson
 */
public class BitInputStreamTest {
    
    /**
     * Initialises a new instance of a unit test for {@link BitInputStream}.
     */
    public BitInputStreamTest() {
        
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
     * Test of {@link BitInputStream#skipBytes} method, of class {@link BitInputStream}.
     */
    @Test
    public void testSkipBytes() {
        
        final byte[] data = new byte[] {0, 0, 0, -128};
        
        final BitInputStream instance = new BitInputStream(data);
        instance.skipBytes(3);
        
        assertTrue(instance.read());
        
    }

    /**
     * Test of {@link BitInputStream#getPosition} method, of class {@link BitInputStream}.
     */
    @Test
    public void testGetPosition() {
      
        final byte[] data = new byte[] {0, 0, 0, 0};
        
        final BitInputStream instance = new BitInputStream(data);
        
        instance.read();
        instance.skipBytes(1);
        
        assertEquals(instance.getPosition(), 9);
        
    }

    /**
     * Test of {@link BitInputStream#read} method, of class {@link BitInputStream}.
     */
    @Test
    public void testRead() {
        
        final byte[] data = new byte[] {-1, -128};
        
        final BitInputStream instance = new BitInputStream(data);
        
        final boolean[] expectedData = {true, true, true, true, true, true, true, true,
            true, false, false, false, false, false, false, false};
        
        for (int i = 0; i < expectedData.length; i++) {
            if (expectedData[i] != instance.read()) {
               fail("Unexpected output from method.");
            }
        }
                
    }
    
}
