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

        TestMessageFrame frame = new TestMessageFrame();
        
        frame.setSize(500, 500);
        frame.setLocation(100, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
}
