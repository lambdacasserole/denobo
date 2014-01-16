package denobo.centralcommand.designer.dialogs.monitor;

import denobo.Agent;
import denobo.centralcommand.DenoboDialog;
import denobo.socket.SocketAgent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Point;
import javax.swing.JTabbedPane;

/**
 * A dialog for monitoring all activity happening on an agent.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentMonitorDialog {
    
    private final Agent agent;
    private final DenoboDialog dialog;
    private final JTabbedPane tabHolder;

    /**
     * Creates an instance of a new AgentMonitorDialog for an agent.
     * 
     * @param agent the agent the dialog will display monitor data for.
     */
    public AgentMonitorDialog(Agent agent) {
        
        this.agent = agent;
        
        // Create and configure the dialog
        dialog = new DenoboDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Monitor [" + agent.getName() + "]");
        dialog.setResizable(true);
        dialog.setAlwaysOnTop(true);
        dialog.setModalityType(ModalityType.MODELESS);
        
        // Create the tab holder and add it to the dialog
        tabHolder = new JTabbedPane();
        dialog.add(tabHolder, BorderLayout.CENTER);
        
        
        // Add the "Messages" tab
        tabHolder.addTab("Messages", new MessagesTab(agent));
        
        // Only create and add a "Connections" tab if the Agent is a SocketAgent.
        if (agent instanceof SocketAgent) {
            tabHolder.addTab("Connections", new ConnectionsTab((SocketAgent) agent));
        }

        
        dialog.pack();
        
    }
    

    
    /**
     * Makes this dialog window visible.
     */
    public void show() {

        dialog.setVisible(true);
        
    }
    
    /**
     * Makes this dialog window visible at the specified position.
     * 
     * @param position      The position to place the dialog window.
     */
    public void show(Point position) {
        
        dialog.setLocation(position);
        this.show();
        
    }
    
    /**
     * Hides this dialog window.
     */
    public void hide() {
        
        dialog.dispose();
        
    }
    
}
