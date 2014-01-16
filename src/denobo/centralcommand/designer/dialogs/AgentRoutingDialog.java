package denobo.centralcommand.designer.dialogs;

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
    
    private final JDialog dialog;
    private final JTextArea area;
    
    public AgentRoutingDialog() {
        
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        
        area = new JTextArea();
        
        dialog.add(area, BorderLayout.CENTER);
        
    }
    
    public void show(Agent agent, Point position) {
        
        dialog.setTitle("Router Data [" + agent.getName() + "]");
        area.setText(agent.routingTable.toString());
        dialog.setLocation(position);
        dialog.setAlwaysOnTop(true);
        dialog.setSize(new Dimension(250, 250));
        dialog.setVisible(true);
        
    }
    
}
