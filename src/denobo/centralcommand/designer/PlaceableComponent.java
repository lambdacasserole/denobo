package denobo.centralcommand.designer;

import java.awt.Point;

/**
 * Represents an appliance placed at specific coordinates in the designer.
 * 
 * @author      Saul Johnson <M2082166@tees.ac.uk>
 * @version     1.0
 * @since       2013-03-17
 */
public class PlaceableComponent implements java.io.Serializable {

    private Appliance appliance; // Each PlaceableComponent is associated with an Appliance.
    private Point location; // We have coordinates for the location of the component in the designer.
        
    /**
     * Gets the width of the component icon in pixels.
     */
    public int getWidth() {
        
        return appliance.getPicture().getIconWidth();
        
    }
    
    /**
     * Gets the height of the component icon in pixels.
     */
    public int getHeight() {
        
        return appliance.getPicture().getIconHeight();
        
    }
    
    /**
     * Gets the pixel x-coordinate of the component in the designer.
     */
    public int getX() {
        
        return (int)location.getX();
        
    }
    
    /**
     * Gets the pixel y-coordinate of the component in the designer.
     */
    public int getY() {
        
        return (int)location.getY();
        
    }
    
    /**
     * Sets the pixel location of the component in the designer.
     * 
     * @param x The new x-coordinate for the component's location in the designer.
     * @param y The new y-coordinate for the component's location in the designer.
     */
    public void setLocation(int x, int y) {
        
		// Negative coordinates are not allowed.
		x = (x < 0 ? 0 : x);
		y = (y < 0 ? 0 : y);
		
        location = new Point(x, y);
        
    }
    
    /**
     * Gets the appliance associated with this component.
     */
    public Appliance getAppliance() {
        
        return appliance;
        
    }
    
    /**
     * Sets the appliance associated with this component.
     */
    public void setAppliance(Appliance appliance) {
        
        this.appliance = appliance;
        
    }
    
    /**
     * Gets the xy-coordinate of this component.
     */
    public Point getLocation() {
        
        return location;
        
    }
        
    /**
     * Sets the xy-coordinate of this component.
     */
    public void setLocation(Point location) {
        
        this.location = location;
        
    }
    
    /**
     * Initialises a new instance of a placeable component with no associated appliance.
     */
    private PlaceableComponent() {
        
        appliance = null;
        location = new Point(0, 0);
        
    }
    
    /**
     * Initialises a new instance of a placeable component with an associated appliance.
     *
     * @param appliance The appliance associated with the component.
     */
    public PlaceableComponent(Appliance appliance) {
    
        this();
        this.appliance = appliance;
        
    }
    
    /**
     * Initialises a new instance of a placeable component with an assocated appliance and given Point location.
     *
     * @param appliance The appliance associated with the component.
     * @param location The xy-coordinates of the component in the designer.
     */
    public PlaceableComponent(Appliance appliance, Point location) {
        
        this(appliance);
        this.location = location;
        
    }
    
    /**
     * Initialises a new instance of a placeable component with an assocated appliance and given x-y location.
     *
     * @param appliance The appliance associated with the component.
     * @param x The x-coordinate of the component in the designer.
     * @param y The y-coordinate of the component in the designer.
     */
    public PlaceableComponent(Appliance appliance, int x, int y) {
        
        this(appliance, new Point(x, y));
        
    }
    
}
