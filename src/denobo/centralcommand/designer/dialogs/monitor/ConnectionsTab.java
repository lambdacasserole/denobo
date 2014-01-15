package denobo.centralcommand.designer.dialogs.monitor;

import denobo.socket.SocketAgent;
import denobo.socket.SocketAgentObserver;
import denobo.socket.connection.DenoboConnection;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeoutException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex Mullen
 */
public class ConnectionsTab extends JPanel implements SocketAgentObserver {

    private final Timer refreshTimer;
    private final JTable connectionsTable;
    private final ConnectionTableModel connectionsTableModel;

    private final SocketAgent agent;
    
    private static final int REFRESH_INTERVAL = 5000;

    public ConnectionsTab(SocketAgent agent) {

        this.agent = agent;

        this.setLayout(new BorderLayout());

        connectionsTableModel = new ConnectionTableModel();
        connectionsTable = new JTable(connectionsTableModel);

        final JScrollPane connectionsTableScrollPane = new JScrollPane(connectionsTable);
        this.add(connectionsTableScrollPane, BorderLayout.CENTER);

        agent.addObserver(this);
        refreshTable();
        
        refreshTimer = new Timer(REFRESH_INTERVAL, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                refreshTable();
            }
            
        });
        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
    
    public void dispose() {
        refreshTimer.stop();
    }

    private void refreshTable() {
        
        connectionsTableModel.setRowCount(0);
        for (DenoboConnection currentConnection : agent.getConnections()) {
            connectionsTableModel.addRow(currentConnection);
        }
        
    }

    @Override
    public void connectionAddSucceeded(SocketAgent agent, final DenoboConnection connection, String hostname, int port) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshTable();
            }
        });

    }

    @Override
    public void incomingConnectionAccepted(SocketAgent agent, final DenoboConnection connection) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshTable();
            }
        });
        
    }

    @Override
    public void connectionClosed(SocketAgent agent, final DenoboConnection connection) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshTable();
            }
        });
        
    }

    @Override
    public void advertisingStarted(SocketAgent agent, int port) {

    }

    @Override
    public void advertisingStopped(SocketAgent agent, int port) {

    }

    @Override
    public void connectionAddFailed(SocketAgent agent, String hostname, int port) {

    }

    /**
     *
     * @author Alex Mullen
     */
    private class ConnectionTableModel extends DefaultTableModel {

        public ConnectionTableModel() {
            super(new Object[]{"ip", "port", "ping"}, 0);
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            // We want to make the entire table un-editable.
            return false;
        }

        public void addRow(DenoboConnection connection) {

            try {
                this.addRow(new Object[]{connection.getRemoteAddress(), connection.getRemotePort(), connection.poke(3000)});
            } catch (TimeoutException ex) {
                System.out.println(ex.getMessage());
            }

        }

    }

}
