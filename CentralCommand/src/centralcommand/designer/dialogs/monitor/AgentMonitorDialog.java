package centralcommand.designer.dialogs.monitor;

import centralcommand.DenoboDialog;
import denobo.Agent;
import denobo.socket.SocketAgent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Point;
import javax.swing.JTabbedPane;

/**
 * A dialog for monitoring all activity happening on an agent.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentMonitorDialog extends DenoboDialog {
    
    /**
     * The agent this dialog is monitoring.
     */
    private final Agent agent;
    
    /**
     * The tabbed pane that holds the tabs in this dialog.
     */
    private final JTabbedPane tabHolder;

    /**
     * Creates an instance of a new AgentMonitorDialog for an agent.
     * 
     * @param agent the agent the dialog will display monitor data for.
     */
    public AgentMonitorDialog(Agent agent) {
        
        super();
        this.agent = agent;
        
        // Create and configure the dialog.
        this.setLayout(new BorderLayout());
        this.setTitle("Monitor [" + agent.getName() + "]");
        this.setResizable(true);
        this.setAlwaysOnTop(true);
        this.setModalityType(ModalityType.MODELESS);
        
        // Create the tab holder and add it to the dialog.
        tabHolder = new JTabbedPane();
        this.add(tabHolder, BorderLayout.CENTER);
        
        
        // Add the "Messages" tab.
        tabHolder.addTab("Messages", new MessagesTab(agent));
        
        /* 
         * Only create and add a "Connections" tab if the Agent is a 
         * SocketAgent.
         */
        if (agent instanceof SocketAgent) {
            tabHolder.addTab("Connections", new ConnectionsTab((SocketAgent) agent));
        }

        this.pack();
        
    }
    
    /**
     * Makes this dialog window visible at the specified position.
     * 
     * @param position  the position to place the dialog window
     */
    public void showAt(Point position) {
        this.setLocation(position);
        this.setVisible(true);
    }
    
}
