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
    
        Agent a2 = new Agent("agent2", false);
        NetworkPortal np2 = new NetworkPortal("networkportal2", 4757);
        np2.addAgent(a2);

        TestFrame1 t1 = new TestFrame1(a2);
        
        t1.setSize(250, 250);
        t1.setLocation(100, 450);
        t1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        t1.setVisible(true);
    }
}