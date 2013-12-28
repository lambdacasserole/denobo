package denobo.centralcommand.designer;

import javax.swing.ImageIcon;

/**
 * A serialisable class to hold the component array and image backdrop present in a saved data file.
 *
 * @author      Saul Johnson <M2082166@tees.ac.uk>
 * @version     1.0
 * @since       2013-03-21
 */
public class SaveFile implements java.io.Serializable {

    private PlaceableComponent[] components;
    private ImageIcon backdrop;

    /**
     * Gets the component array associated with this data file.
     */
    public PlaceableComponent[] getComponents() {
    
        return components;
    
    }
    
    /**
     * Sets the component array associated with this data file.
     * 
     * @param components The array of components.
     */
    public void setComponents(PlaceableComponent[] components) {
    
        this.components = components;
    
    }
    
    /**
     * Gets the backdrop (background image) associated with this data file.
     */
    public ImageIcon getBackdrop() {
        
        return backdrop;
        
    }
    
    /**
     * Sets the backdrop (background image) associated with this data file.
     * 
     * @param backdrop The backdrop (background image).
     */
    public void setBackdrop(ImageIcon backdrop) {
    
        this.backdrop = backdrop;
    
    }
    
    /**
     * Initialises a new instance of a saved data file with the specified component array and backdrop (background image).
     *
     * @param components The array of components.
     * @param backdrop The backdrop (background image).
     */
    public SaveFile(PlaceableComponent[] components, ImageIcon backdrop) {
    
            this.components = components;
            this.backdrop = backdrop;
    
    }

}
