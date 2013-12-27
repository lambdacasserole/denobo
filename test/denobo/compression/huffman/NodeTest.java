/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo.compression.huffman;

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
public class NodeTest {
    
    public NodeTest() {
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
     * Test of hasChildren method, of class Node.
     */
    @Test
    public void testHasChildren() {
        System.out.println("hasChildren");
        Node instance = null;
        boolean expResult = false;
        boolean result = instance.hasChildren();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasParent method, of class Node.
     */
    @Test
    public void testHasParent() {
        System.out.println("hasParent");
        Node instance = null;
        boolean expResult = false;
        boolean result = instance.hasParent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getData method, of class Node.
     */
    @Test
    public void testGetData() {
        System.out.println("getData");
        Node instance = null;
        int expResult = 0;
        int result = instance.getData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buildBitSequence method, of class Node.
     */
    @Test
    public void testBuildBitSequence() {
        System.out.println("buildBitSequence");
        Node instance = null;
        BitSequence expResult = null;
        BitSequence result = instance.buildBitSequence();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFrequency method, of class Node.
     */
    @Test
    public void testGetFrequency() {
        System.out.println("getFrequency");
        Node instance = null;
        double expResult = 0.0;
        double result = instance.getFrequency();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParent method, of class Node.
     */
    @Test
    public void testSetParent() {
        System.out.println("setParent");
        Node parent = null;
        Node instance = null;
        instance.setParent(parent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toXml method, of class Node.
     */
    @Test
    public void testToXml() {
        System.out.println("toXml");
        Node instance = null;
        String expResult = "";
        String result = instance.toXml();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortByFrequency method, of class Node.
     */
    @Test
    public void testSortByFrequency() {
        System.out.println("sortByFrequency");
        Node[] nodes = null;
        Node.sortByFrequency(nodes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combineLeastFrequent method, of class Node.
     */
    @Test
    public void testCombineLeastFrequent() {
        System.out.println("combineLeastFrequent");
        Node[] nodes = null;
        Node[] expResult = null;
        Node[] result = Node.combineLeastFrequent(nodes);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class Node.
     */
    @Test
    public void testCombine() {
        System.out.println("combine");
        Node zero = null;
        Node one = null;
        Node expResult = null;
        Node result = Node.combine(zero, one);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
