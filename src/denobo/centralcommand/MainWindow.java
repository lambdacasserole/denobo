package denobo.centralcommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Saul Johnson
 */
public class MainWindow extends JFrame {
    
    /**
     * Initialises the program main window.
     */
    public MainWindow() {
        
        super();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        final JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(640, 480));
        this.add(contentPanel, BorderLayout.CENTER);
        
        this.pack();
        
    }
    
}
