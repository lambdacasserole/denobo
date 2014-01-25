package centralcommand.designer.dialogs;

import denobo.Agent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 * A dialog for displaying the current routing table for an agent.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentRoutingDialog {
    
    /**
     * The underlying JDialog this uses to display itself.
     */
    private final JDialog dialog;
    
    /**
     * The text area to dump the routing table output in.
     */
    private final JTextArea area;
    
    /**
     * Creates a new AgentRoutingDialog.
     */
    public AgentRoutingDialog() {
        
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        
        area = new JTextArea();
        
        dialog.add(area, BorderLayout.CENTER);
        
    }
    
    /**
     * Makes this AgentRoutingDialog visible and displays the routing table
     * data for the specified Agent instance.
     * 
     * @param agent     the Agent
     * @param position  the position to place this dialog
     */
    public void show(Agent agent, Point position) {
        
        area.setText(agent.getRoutingTable().toString());
        dialog.setTitle("Router Data [" + agent.getName() + "]");
        dialog.setLocation(position);
        dialog.setAlwaysOnTop(true);
        dialog.setSize(new Dimension(250, 250));
        dialog.setVisible(true);
        
    }
    
}
