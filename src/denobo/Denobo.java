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
    
        Agent a1 = new Agent("agent1", false);
        Agent a2 = new Agent("agent2", false);
        NetworkPortal np = new NetworkPortal("networkportal1", 9385);
        NetworkPortal np2 = new NetworkPortal("networkportal2", 4757);
        np.addAgent(a1);
        np2.addAgent(a2);
        try {
            np.connect(InetAddress.getLocalHost().getHostAddress(), 4757);
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