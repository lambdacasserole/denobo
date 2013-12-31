package denobo.crypto;

import denobo.compression.FileIO;
import denobo.compression.huffman.FileUtils;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Represents a unit test for {@link RC4CryptoAlgorithm}.
 * 
 * @author Saul Johnson
 */
public class RC4CryptoAlgorithmTest {
    
    /**
     * Initialises a new instance of a unit test for {@link RC4CryptoAlgorithm}.
     */
    public RC4CryptoAlgorithmTest() {
        
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
     * Test of {@link RC4CryptoAlgorithm#setKey} method, of class {@link RC4CryptoAlgorithm}.
     */
    @Test
    public void testSetKey() {
        
        final int[] key = new int[] {255, 255, 255};
        final RC4CryptoAlgorithm instance = new RC4CryptoAlgorithm();
        
        instance.setKey(key);
        
        assertArrayEquals(key, instance.getKey());
        
    }

    /**
     * Test of {@link RC4CryptoAlgorithm#getKey} method, of class {@link RC4CryptoAlgorithm}.
     */
    @Test
    public void testGetKey() {
        
        final int[] key = new int[] {255, 255, 255};
        final RC4CryptoAlgorithm instance = new RC4CryptoAlgorithm();
        
        assertNull(instance.getKey());
        
        instance.setKey(key);
        
        assertArrayEquals(key, instance.getKey());
        
    }
    
    /**
     * Test of {@link RC4CryptoAlgorithm#encrypt} method, of class {@link RC4CryptoAlgorithm}.
     */
    @Test
    public void testEncrypt() {
        try {
            
            final byte[] keyBytes = "Key".getBytes("US-ASCII");
            final int[] key = new int[keyBytes.length];
            for (int i = 0; i < keyBytes.length; i++) {
                key[i] = keyBytes[i] & 0xFF;
            }
            
            final RC4CryptoAlgorithm instance = new RC4CryptoAlgorithm();
            instance.setKey(key);
            
            final byte[] ciphertext = instance.encrypt(FileUtils.readFile(new File("data/encryption_test.txt")).getBytes("US-ASCII"));
            FileIO.setFileBytes(new File("data/encrypted_actual.rc4"), ciphertext);
            
            final byte[] expected = FileIO.getFileBytes(new File("data/encrypted_expected.rc4"));
            
            assertArrayEquals(expected, ciphertext);
            
        } catch (UnsupportedEncodingException ex) {
            
            System.out.println("Encoding not supported.");
            
        }
    }

    /**
     * Test of {@link RC4CryptoAlgorithm#decrypt} method, of class {@link RC4CryptoAlgorithm}.
     */
    @Test
    public void testDecrypt() {
       try {
            
            final byte[] keyBytes = "Key".getBytes("US-ASCII");
            final int[] key = new int[keyBytes.length];
            for (int i = 0; i < keyBytes.length; i++) {
                key[i] = keyBytes[i] & 0xFF;
            }
            
            final RC4CryptoAlgorithm instance = new RC4CryptoAlgorithm();
            instance.setKey(key);
            
            final byte[] plaintext = instance.decrypt(FileIO.getFileBytes(new File("data/encrypted_expected.rc4")));
            FileIO.setFileBytes(new File("data/decrypted_actual.txt"), plaintext);
            
            final byte[] expected = FileIO.getFileBytes(new File("data/decrypted_expected.txt"));
            
            assertArrayEquals(expected, plaintext);
            
        } catch (UnsupportedEncodingException ex) {
            
            System.out.println("Encoding not supported.");
            
        }
    }
    
}
