package denobo.centralcommand.designer.dialogs.monitor;

import denobo.socket.SocketAgent;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Alex Mullen
 */
public class ConnectionsTab extends JPanel {
    
    private final JTable connectionsTable;
    private final ConnectionTableModel connectionsTableModel;
    
    private final SocketAgent agent;
    
        
    public ConnectionsTab(SocketAgent agent) {
        
        this.agent = agent;
        
        this.setLayout(new BorderLayout());

        connectionsTableModel = new ConnectionTableModel();
        connectionsTable = new JTable(connectionsTableModel);
        
        final JScrollPane connectionsTableScrollPane = new JScrollPane(connectionsTable);
        this.add(connectionsTableScrollPane, BorderLayout.CENTER);

    }

}
