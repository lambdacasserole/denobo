package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
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
    
    public void show(Agent agent) {
        
        dialog.setTitle("Router Data [" + agent.getName() + "]");
        area.setText(agent.routingTable.toString());
        dialog.setAlwaysOnTop(true);
        dialog.setPreferredSize(new Dimension(250, 250));
        dialog.setVisible(true);
        
    }
    
}
