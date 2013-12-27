/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo.compression;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Saul Johnson
 */
public class BasicCompressorTest {
    
    public BasicCompressorTest() {
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
     * Test of compress method, of class BasicCompressor.
     */
    @Test
    public void testCompress() {
        
        final Compressor instance = new BasicCompressor();
        
        final byte[] fileBytes = FileUtils.getFileBytes(new File("compression_test.bmp"));
        final byte[] outputBytes = instance.compress(fileBytes);
        
        FileUtils.setFileBytes(new File("compression_test.arc"), outputBytes);
        
    }

    /**
     * Test of decompress method, of class BasicCompressor.
     */
    @Test
    public void testDecompress() {
        
        final Compressor instance = new BasicCompressor();
        
        byte[] fileBytes = FileUtils.getFileBytes(new File("compression_test.bmp"));
        byte[] outputBytes = instance.compress(fileBytes);
        
        FileUtils.setFileBytes(new File("decompression_test.arc"), outputBytes);
                
        fileBytes = FileUtils.getFileBytes(new File("decompression_test.arc"));
        outputBytes = instance.decompress(fileBytes);
        
        FileUtils.setFileBytes(new File("decompression_test.bmp"), outputBytes);
        
    }
    
}
