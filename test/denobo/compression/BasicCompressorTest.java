package denobo.compression;

import denobo.FileIO;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link BasicCompressor}.
 * 
 * @author Saul Johnson
 */
public class BasicCompressorTest {
    
    /**
     * Initialises a new instance of a unit test for {@link BasicCompressor}.
     */
    public BasicCompressorTest() {
        
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
     * Test of {@link BasicCompressor#compress} method, of class {@link BasicCompressor}.
     */
    @Test
    public void testCompress() {
        
        final Compressor instance = new BasicCompressor();
        
        final byte[] fileBytes = FileIO.readBytesFromFile(new File("data/_compression_test.bmp"));
        
        long timer = System.currentTimeMillis();
        final byte[] outputBytes = instance.compress(fileBytes);
        timer = System.currentTimeMillis() - timer;
        
        System.out.println("Compression took: " + timer + "ms");
        
        FileIO.writeBytesToFile(new File("data/basic_compression_actual.arc"), outputBytes);
        
        final byte[] expectedBytes = FileIO.readBytesFromFile(new File("data/basic_compression_expected.arc"));
        final byte[] actualBytes = FileIO.readBytesFromFile(new File("data/basic_compression_actual.arc"));
        
        assertArrayEquals(expectedBytes, actualBytes);
        
    }

    /**
     * Test of {@link BasicCompressor#decompress} method, of class {@link BasicCompressor}.
     */
    @Test
    public void testDecompress() {
        
        final Compressor instance = new BasicCompressor();
        
        final byte[] fileBytes = FileIO.readBytesFromFile(new File("data/basic_decompression_test.arc"));
        
        long timer = System.currentTimeMillis();
        final byte[] outputBytes = instance.decompress(fileBytes);
        timer = System.currentTimeMillis() - timer;
        
        System.out.println("Decompression took: " + timer + "ms");
        
        FileIO.writeBytesToFile(new File("data/basic_decompression_actual.bmp"), outputBytes);
        
        final byte[] expectedBytes = FileIO.readBytesFromFile(new File("data/basic_decompression_expected.bmp"));
        final byte[] actualBytes = FileIO.readBytesFromFile(new File("data/basic_decompression_actual.bmp"));
        
        assertArrayEquals(expectedBytes, actualBytes);
        
    }
    
}
