package denobo.centralcommand.designer;

import javax.swing.ImageIcon;

/**
 * Represents an electrical appliance. This class must be inherited.
 * 
 * @author      Saul Johnson <M2082166@tees.ac.uk>
 * @version     1.0
 * @since       2013-03-15
 */
public abstract class Appliance implements java.io.Serializable {

    /*
     * Explanation of extra attribute 'name' and 'getName()' accessor - 
     * I feel that using reflection e.g. 'myAppliance.getClass().getName()' is
     * a messy and unreliable method of retrieving the name of the appliance.
     * I have therefore added another attribute and accessor for storing and retrieving
     * the name of the appliance.
     */

    /**
     * The name of the appliance.
     */
    protected String name;
    
    /**
     * The power state of the appliance. True is on, false is off.
     */
    protected boolean power; 
    
    /**
     * The picture that represents the appliance.
     */
    protected ImageIcon picture; 
    
    /**
     * Gets the name of the appliance.
     *
     * @return A String containing the name of this type of appliance.
     */
    public String getName() {
    
        return name;
        
    }
    
    /**
     * Gets the current power status of the appliance.
     *
     * @return True if the power is on, otherwise false.
     */
    public boolean getPower() {
    
        return power;
        
    }

    /**
     * Sets the power status of the appliance.
     *
     * @param power The new power state of the appliance.
     */
    public void setPower(boolean power) {
    
        this.power = power;
        
    }

    /**
     * Gets a String containing the power status of the Appliance.
     *
     * @return A String containing details about this Appliance's properties.
     */
    public String toString() {
    
        return "Power: " + (power ? "ON" : "OFF");
        
    }
    
    /**
     * Gets the ImageIcon associated with an Appliance of this type. Must be overridden.
     *
     * @return An ImageIcon associated with an Appliance of this type. May differ depending on power status.
     */
    public abstract ImageIcon getPicture();

    /**
     * Initialises a new instance of an Appliance with its power off and no associated picture.
     */
    public Appliance() {
    
        power = false;
        picture = null;
        
    }

}
