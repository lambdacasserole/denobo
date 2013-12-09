/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package denobo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Portal p2 = new Portal("portal2");
        Agent a1 = new Agent("agent1", false);
        Agent a2 = new Agent("agent2", false);
        p.addAgent(a1);
        p2.addAgent(a2);
        NetworkPortal np = new NetworkPortal("networkportal1", 9998);
        NetworkPortal np2 = new NetworkPortal("networkportal2", 9999);
        np.addAgent(p);
        np2.addAgent(p2);
        try {
            np.connect(InetAddress.getLocalHost().getHostAddress(), 9999);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Denobo.class.getName()).log(Level.SEVERE, null, ex);
        }
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