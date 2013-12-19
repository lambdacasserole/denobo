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
//        
//        final Agent a1 = new Agent("agent1");
//        final Agent a2 = new Agent("agent2");
//        final Agent a3 = new Agent("agent3");
//        
//
//        a1.connectActor(a1);
//        a1.connectActor(a2);
//        a1.connectActor(a3);
//
//        a2.connectActor(a1);
//        a2.connectActor(a2);
//        a2.connectActor(a3);
//        
//        a3.connectActor(a1);
//        a3.connectActor(a2);
//        a3.connectActor(a3);
//        
//
//        a1.shutdown();
//        a2.shutdown(); 
//        a3.shutdown();


        final TestMessageFrame frame = new TestMessageFrame();
        
        frame.setSize(500, 500);
        frame.setLocation(100, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
}
