/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo;

import javax.swing.JFrame;

/**
 *
 * @author Saul
 */
public class Denobo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        Portal p = new Portal("portal1");
        Agent a1 = new Agent("agent1", false);
        Agent a2 = new Agent("agent2", false);
        p.addAgent(a1);
        p.addAgent(a2);
        NetworkPortal np = new NetworkPortal("networkportal1", 9999);
        p.registerParentPortal(np);
        
        TestFrame1 t1 = new TestFrame1(a1);
        TestFrame2 t2 = new TestFrame2(a2);
        t1.setSize(250, 250);
        t2.setSize(250, 250);
        t1.setLocation(100, 450);
        t2.setLocation(400, 450);
        t1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        t2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        t1.setVisible(true);
        t2.setVisible(true);
        
    }
    
}