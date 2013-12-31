package denobo.centralcommand.designer;

/**
 * A specialised event listener designed to listen for events raised by a NetworkDesigner control.
 * 
 * @author      Saul Johnson <M2082166@tees.ac.uk>, Edited for use for Denobo by Alex Mullen
 * @version     1.0
 * @since       2013-03-17
 */
public interface DesignerEventListener {

    /**
     * Raised when a component is added to the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void componentAdded(DesignerEvent e);

    /**
     * Raised when a component is selected in the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void componentSelected(DesignerEvent e);
    
    /**
     * Raised when a component is deleted from the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void componentDeleted(DesignerEvent e);
    
    /**
     * Raised when a component is repositioned in the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void componentMoved(DesignerEvent e);
    
    /**
     * Raised when a component is right-clicked in the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void componentRightClicked(DesignerEvent e);
    
    /**
     * Raised when all components have been deselected in the designer.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public void selectionCleared(DesignerEvent e);
    
    /**
     * Raised when a component is about to be deleted in the designer. Returning false will cancel the deletion.
     *
     * @param e Event arguments containing information about the triggering component.
     */
    public boolean componentDeleting(DesignerEvent e);
    
}