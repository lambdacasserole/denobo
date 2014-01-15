package denobo.compression;

import denobo.FileIO;
import denobo.compression.lzw.LZWCompressor;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link LZWCompressor}.
 * 
 * @author Saul Johnson
 */
public class LZWCompressorTest {
    
    /**
     * Initialises a new instance of a unit test for {@link LZWCompressor}.
     */
    public LZWCompressorTest() {
        
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
     * Test of {@link LZWCompressor#compress} method, of class {@link LZWCompressor}.
     */
    @Test
    public void testCompress() {
        
        final LZWCompressor instance = new LZWCompressor();
        
        final byte[] fileBytes = FileIO.readBytesFromFile(new File("data/_compression_test.bmp"));
        
        long timer = System.currentTimeMillis();
        final byte[] outputBytes = instance.compress(fileBytes);
        timer = System.currentTimeMillis() - timer;
        
        System.out.println("Compression took: " + timer + "ms");
        
        FileIO.writeBytesToFile(new File("data/lzw_compression_actual.lzw"), outputBytes);
        
        final byte[] expectedBytes = FileIO.readBytesFromFile(new File("data/lzw_compression_expected.lzw"));
        assertArrayEquals(outputBytes, expectedBytes);
        
    }
    
    /**
     * Test of {@link LZWCompressor#decompress} method, of class {@link LZWCompressor}.
     */
    @Test
    public void testDecompress() {
        
        final LZWCompressor instance = new LZWCompressor();
        
        final byte[] fileBytes = FileIO.readBytesFromFile(new File("data/lzw_compression_expected.lzw"));
        
        long timer = System.currentTimeMillis();
        final byte[] outputBytes = instance.decompress(fileBytes);
        timer = System.currentTimeMillis() - timer;
        
        System.out.println("Decompression took: " + timer + "ms");
        
        FileIO.writeBytesToFile(new File("data/lzw_decompression_actual.bmp"), outputBytes);

        final byte[] expectedBytes = FileIO.readBytesFromFile(new File("data/lzw_decompression_expected.bmp"));
        assertArrayEquals(outputBytes, expectedBytes);
        
    }
    
}
