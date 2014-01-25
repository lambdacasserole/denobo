package denobo.centralcommand;

import javax.swing.SwingUtilities;

/**
 * The main class for the Denobo Central Command software.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Denobo {
    
    /**
     * The main method for the Denobo Central Command software.
     * 
     * @param args  command-line arguments
     */
    public static void main(String[] args) {
       
        // Start up the GUI from within the EDT thread.
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                // Splash screen handles it from here.
                final SplashScreen splash = new SplashScreen();
                splash.setVisible(true);                
            }
            
        });

    }
    
}
