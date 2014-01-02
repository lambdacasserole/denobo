package denobo.centralcommand.designer;

/**
 * Represents an event raised by a NetworkDesigner control.
 *
 * @author      Saul Johnson <M2082166@tees.ac.uk>, Edited for use for Denobo by Alex Mullen
 * @version     1.0
 * @since       2013-03-17
 */
public class DesignerEvent {

    /**
     * Initialises a new instance of a designer event.
     * 
     * @param source The AgentDisplayable that triggered the event.
     */
    public DesignerEvent(AgentDisplayable source) {
    
        this.source = source;
    
    }
    
    private AgentDisplayable source;
    
    /**
     * Gets the AgentDisplayable that triggered the event in the designer.
     */
    public AgentDisplayable getSource() {
        
        return source;
        
    }

    
}