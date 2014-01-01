package denobo.centralcommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Represents the program splash screen.
 * 
 * @author Saul Johnson
 */
public class SplashScreen extends DenoboWindow {
        
    /**
     * The number of milliseconds the splash screen will wait before 
     * the main window is displayed.
     */
    public static final int SPLASH_DELAY = 0000;
        
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
    public void setVisible(boolean b) {
        
        super.setVisible(b);
        if (b) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() { 
                @Override
                public void run() {
                    
                   // Show main window.
                   final MainWindow mainWnd = new MainWindow();
                   mainWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   mainWnd.setVisible(true);
                   setVisible(false);
                   
                }
            }, SPLASH_DELAY);
        }
        
    }
    
}
