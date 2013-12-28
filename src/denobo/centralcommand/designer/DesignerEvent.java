package denobo.centralcommand.designer;

/**
 * Represents an event raised by a HouseDesigner control.
 *
 * @author      Saul Johnson <M2082166@tees.ac.uk>
 * @version     1.0
 * @since       2013-03-17
 */
public class DesignerEvent {
    
    private PlaceableComponent source;
    private int sourceIndex;
    
    /**
     * Gets the PlaceableComponent that triggered the event in the designer.
     */
    public PlaceableComponent getSource() {
        
        return source;
        
    }
    
    /**
     * Gets the index of the triggering PlaceableComponent in the designer component array.
     */
    public int getSourceIndex() {
        
        return sourceIndex;
        
    }
    
    /**
     * Initialises a new instance of a designer event.
     * 
     * @param source The PlaceableComponent that triggered the event.
     * @param sourceIndex The index of the triggering PlaceableComponent in the designer component array.
     */
    public DesignerEvent(PlaceableComponent source, int sourceIndex) {
    
        this.source = source;
        this.sourceIndex = sourceIndex;
    
    }
    
}
