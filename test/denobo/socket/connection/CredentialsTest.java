/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo.socket.connection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Saul
 */
public class CredentialsTest {
    
    public CredentialsTest() {
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
     * Test of parse method, of class Credentials.
     */
    @Test
    public void testParse() {
        
    }

    /**
     * Test of getPassword method, of class Credentials.
     */
    @Test
    public void testGetPassword() {
        
    }

    /**
     * Test of getUsername method, of class Credentials.
     */
    @Test
    public void testGetUsername() {
        
    }

    /**
     * Test of validate method, of class Credentials.
     */
    @Test
    public void testValidate() {
        final Credentials instance = new Credentials("username", "password");
        final Credentials comparison = new Credentials("username", "password");
        assertTrue(Credentials.validate(instance, comparison));
    }

    /**
     * Test of toString method, of class Credentials.
     */
    @Test
    public void testToString() {
        
    }
    
}
