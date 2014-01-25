package centralcommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Represents the program splash screen.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class SplashScreen extends DenoboWindow {
        
    /**
     * The number of milliseconds the splash screen will wait before 
     * the main window is displayed.
     */
    public static final int SPLASH_DELAY = 3000;
        
    /**
     * Initialises the program splash screen.
     */
    public SplashScreen() {
        
        super();
                
        final URL imageUrl = getClass().getResource("resources/splash.png");
        
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageUrl);
        } catch (IOException ex) {
            System.err.println("Could not load splash screen image.");
        }
        
        this.setLayout(new BorderLayout());
        
        final ImagePanel panel = new ImagePanel(image);
        panel.setPreferredSize(new Dimension(480, 300));
        this.add(panel, BorderLayout.CENTER);
        
        this.setTitle("Denobo Central Command");
        this.setUndecorated(true);
        this.pack();
        this.setLocationRelativeTo(null);
        
    }
    
    @Override
    public void setVisible(boolean visible) {
        
        super.setVisible(visible);
        if (visible) {
            
            final Timer timer = new Timer(SPLASH_DELAY, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    // Show main window.
                    final MainWindow mainWnd = new MainWindow();
                    mainWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    mainWnd.setVisible(true);
                    setVisible(false);
                    
                }
            });
            
            // We only want the timer to run once.
            timer.setRepeats(false);
            
            // Start the timer.
            timer.start();

        }
        
    }
    
}
