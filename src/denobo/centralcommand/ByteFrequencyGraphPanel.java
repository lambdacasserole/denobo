package denobo.centralcommand;

import denobo.compression.huffman.ByteFrequencySet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Displays the relative byte frequencies for a file.
 * 
 * @author Saul Johnson
 */
public class ByteFrequencyGraphPanel extends ImagePanel {

    /**
     * The frequency set from which to draw the graph.
     */
    private ByteFrequencySet freqs;
    
    /**
     * The graph foreground colour.
     */
    private Color foregroundColor;
    
    /**
     * The graph background colour.
     */
    private Color backgroundColor;
    
    /**
     * Whether or not the graph is being drawn vertically.
     */
    private boolean verticalLayout;
    
    /**
     * Initialises a new instance of a component to display the relative byte
     * frequencies for a file.
     */
    public ByteFrequencyGraphPanel() {
        super();
        foregroundColor = Color.BLACK;
        backgroundColor = Color.WHITE;
        verticalLayout = false;
    }
    
    /**
     * Renders the graph as a {@link BufferedImage} and returns it.
     * 
     * @return  the graph as a {@link BufferedImage}
     */
    private BufferedImage render() {
        
        final BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();

        g.setColor(backgroundColor);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        final double imageHeight = image.getHeight();
        final double imageWidth = image.getWidth();
        
        if (freqs != null) {
            g.setColor(foregroundColor);
            for (int i = 0; i < freqs.getSize(); i++) {
                if (verticalLayout) {
                    g.drawLine(0, i, (int) (freqs.getUnsignedByteFrequency(i) * imageWidth), i);
                } else {
                    g.drawLine(i, image.getHeight(), i, (int) (imageHeight - (freqs.getUnsignedByteFrequency(i) * imageHeight)));
                }
            }
        }
        
        return image;
       
    }
    
    /**
     * Sets the frequency set to display in the component.
     * 
     * @param freqs the frequency set to display
     */
    public void setFrequencySet(ByteFrequencySet freqs) {
        this.freqs = freqs;
        setImage(render());
    }
    
    /**
     * Displays the byte frequencies for the specified file.
     * 
     * @param file  the file for which to display byte frequencies.
     */
    public void setFile(File file) {
        setFrequencySet(ByteFrequencySet.fromFile(file));
    }
    
    /**
     * Gets the foreground colour of the graph.
     * 
     * @return  the foreground colour of the graph 
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }

    /**
     * Sets the foreground colour of the graph.
     * 
     * @param foregroundColor   the new foreground colour of the graph 
     */
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        setImage(render());
    }

    /**
     * Gets the background colour of the graph.
     * 
     * @return  the background colour of the graph 
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    /**
     * Sets the background colour of the graph.
     * 
     * @param backgroundColor   the new background colour of the graph 
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setImage(render());
    }
    
    /**
     * Gets whether or not the graph is currently displaying data in a vertical layout.
     * 
     * @return  whether or not the graph is currently displaying data in a vertical layout
     */
    public boolean isVerticalLayout() {
        return verticalLayout;
    }

    /**
     * Sets whether or not the graph should display in a vertical layout.
     * 
     * @param verticalLayout    whether or not to display in vertical layout
     */
    public void setVerticalLayout(boolean verticalLayout) {
        this.verticalLayout = verticalLayout;
        setImage(render());
    }
    
}
