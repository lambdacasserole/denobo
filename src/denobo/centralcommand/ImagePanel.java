package denobo.centralcommand;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * A component capable of displaying an image.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class ImagePanel extends JComponent {
   
    /**
     * The image that will be displayed on the component.
     */
    private BufferedImage image;
    
    /**
     * Initialises a new instance of an image display component.
     */
    public ImagePanel() {
        this(null);
    }
    
    /**
     * Initialises a new instance of an image display component.
     * 
     * @param image the {@link BufferedImage} to display in the component
     */
    public ImagePanel(BufferedImage image) {
        super();
        this.image = image;
    }
    
    /**
     * Gets the image being displayed in the component.
     * 
     * @return  the {@link BufferedImage} being displayed in the component or null
     */
    public BufferedImage getImage() {
        return image;
    }
    
    /**
     * Sets the image to display in the component.
     * 
     * @param image the {@link BufferedImage} to display in the component
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        this.repaint();
    }
        
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
    
}
