package denobo.centralcommand;

import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Represents a window used in the the program.
 * 
 * @author Saul Johnson
 */
public class DenoboWindow extends JFrame {
    
    /**
     * Initialises a new instance of a program window.
     */
    public DenoboWindow() {
        
        super();
        
        // Load window icons.
        final ArrayList<Image> wndIcons = new ArrayList<>();
        final int[] iconSizes = new int[] {16, 24, 32, 48, 64, 96, 128, 256, 512 };
        for (int i : iconSizes) {
            wndIcons.add(new ImageIcon(getClass().getResource("resources/icon/icon_" + i + ".png")).getImage());
        }
        this.setIconImages(wndIcons);
        
    }
    
}