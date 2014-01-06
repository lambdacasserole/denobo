package denobo.centralcommand.designer;

import java.awt.Point;

/**
 * A specialised event listener designed to listen for events raised by a NetworkDesigner control.
 * 
 * @author      Saul Johnson <M2082166@tees.ac.uk>, Edited for use for Denobo by Alex Mullen
 * @version     1.0
 * @since       2013-03-17
 */
public interface DesignerEventListener {

    /**
     * Raised when an agent is added into the designer.
     * 
     * @param agent The agent that was added.
     */
    public void agentAdded(AgentDisplayable agent);
    
    /**
     * Raised when an agent is deleted from the designer.
     * 
     * @param agent The agent that was deleted.
     */
    public void agentDeleted(AgentDisplayable agent);
    
    /**
     * Raised when an agent is selected in the designer.
     * 
     * @param agent The agent that was selected.
     */
    public void agentSelected(AgentDisplayable agent);
    
    /**
     * Raised when an agent has been repositioned in the designer.
     * 
     * @param agent The agent that has been repositioned/moved.
     */
    public void agentMoved(AgentDisplayable agent);

    /**
     * Raised when a previously selected agent is now not selected and nothing
     * else is currently selected.
     * 
     */
    public void selectionCleared();
    
    /**
     * Raised when an agent is linked with another agent in the designer.
     * 
     * @param link The link that was created.
     */
    public void linkCreated(AgentLink link);
    
    /**
     * Raised when an agent link is broken then deleted in the designer.
     * 
     * @param link The link that is and has been deleted.
     */
    public void linkDeleted(AgentLink link);
    
}