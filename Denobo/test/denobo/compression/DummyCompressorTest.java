package denobo.compression;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link DummyCompressor}.
 * 
 * @author Saul Johnson
 */
public class DummyCompressorTest {
    
    /**
     * Initialises a new instance of a unit test for {@link BasicCompressor}.
     */
    public DummyCompressorTest() {
        
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
     * Test of {@link DummyCompressor#compress} method, of class {@link DummyCompressor}.
     */
    @Test
    public void testCompress() {
        
        final Compressor instance = new DummyCompressor();
        
        final byte[] testBytes = new byte[] {10, 10, 12, 16};
        assertArrayEquals(testBytes, instance.compress(testBytes));
        
    }

    /**
     * Test of {@link DummyCompressor#decompress} method, of class {@link DummyCompressor}.
     */
    @Test
    public void testDecompress() {
        
        final Compressor instance = new DummyCompressor();
        
        final byte[] testBytes = new byte[] {10, 10, 12, 16};
        assertArrayEquals(testBytes, instance.decompress(testBytes));
        
    }
    
}
