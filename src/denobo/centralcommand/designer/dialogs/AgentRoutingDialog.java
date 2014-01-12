package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import java.awt.BorderLayout;
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
        area = new JTextArea();
        
        dialog.setLayout(new BorderLayout());
        dialog.add(area, BorderLayout.CENTER);
        
    }
    
    public void show(Agent agent) {
        
        area.setText(agent.routingTable.toString());
        dialog.setVisible(true);
        
    }
    
}
