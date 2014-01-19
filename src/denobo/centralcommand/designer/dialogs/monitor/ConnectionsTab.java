package denobo.centralcommand.designer.dialogs.monitor;

import denobo.socket.SocketAgent;
import denobo.socket.SocketAgentObserver;
import denobo.socket.connection.DenoboConnection;
import java.awt.BorderLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Represents the "Connections" tab within an agent monitor dialog.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 * @see     AgentMonitorDialog
 */
public class ConnectionsTab extends JPanel implements SocketAgentObserver {
    
    /**
     * The interval the ping column of each connection is refreshed.
     */
    private static final int PING_REFRESH_INTERVAL = 2000;
    
    /**
     * The maximum number of milliseconds to wait for a ping reply for each
     * connection.
     */
    private static final int PING_TIMEOUT = 20000;
    
    /**
     * The timer that this uses to refresh the ping of each connection at a 
     * scheduled interval.
     */
//    private final Timer refreshTimer;
    
    /**
     * The table to display the list of connections on.
     */
    private final JTable connectionsTable;
    
    /**
     * The table model that holds the list of connections to display in.
     */
    private final ConnectionTableModel connectionsTableModel;

    /**
     * The scheduled scheduledExecutor service to use for scheduling a thread to 
     * refresh the ping of each connection at a scheduled interval.
     */
    private final ScheduledExecutorService scheduledExecutor;
    
    /**
     * The SocketAgent instance this ConnectionsTab instance displays the data
     * for.
     */
    private final SocketAgent agent;
    
    
    /**
     * Creates a new ConnectionsTab for displaying a list of connections and
     * associated data for the specified SocketAgent instance.
     * 
     * @param agent     the SocketAgent instance
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ConnectionsTab(SocketAgent agent) {

        this.agent = agent;

        this.setLayout(new BorderLayout());

        connectionsTableModel = new ConnectionTableModel();
        connectionsTable = new JTable(connectionsTableModel);

        final JScrollPane connectionsTableScrollPane = new JScrollPane(connectionsTable);
        this.add(connectionsTableScrollPane, BorderLayout.CENTER);

        agent.addObserver(this);
        for (DenoboConnection currentConnection : agent.getConnections()) {
            connectionsTableModel.addRow(currentConnection);
        }
        
        // Schedule a task to automatically refresh pings at a scheduled interval.
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refreshPings();
            }
        }, PING_REFRESH_INTERVAL, PING_REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Stops the refresh time and cleans up any resources used by this
     * ConnectionsTab instance.
     */
    public void dispose() {
        scheduledExecutor.shutdown();
    }

    /**
     * Refreshes the connections table.
     */
    private void refreshPings() {

        /*
         * Go through each connection and get the current ping on a seperate 
         * thread to the swing so as not to block.
         * 
         * Updating the ping column MUST be done on the swing thread.
         */
        for (final DenoboConnection currentConnection : agent.getConnections()) {
            
            scheduledExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final long ping = currentConnection.poke(PING_TIMEOUT);
                        updateConnectionPingColumn(currentConnection, ping);
                    } catch (TimeoutException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });

        }

    }
    
    /**
     * Updates the ping column for a connection in the connections model.
     * 
     * @param connection    the connection
     * @param ping          the value to set the ping column to
     */
    private void updateConnectionPingColumn(final DenoboConnection connection, final long ping) {
    
        // Make sure we are updating within the swing thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                /* 
                 * Iterate through the table and find the connection where the
                 * IP and port match then change column index 2 (the ping).
                 */ 
                for (int row = 0; row < connectionsTableModel.getRowCount(); row++) {
                    if (connectionsTableModel.getValueAt(row, 0).equals(connection.getRemoteAddress())) {
                        if (connectionsTableModel.getValueAt(row, 1).equals(connection.getRemotePort())) {
                            connectionsTableModel.setValueAt(ping, row, 2);
                        }
                    }
                }

            }
        });
        
    }

    @Override
    public void connectionAddSucceeded(SocketAgent agent, final DenoboConnection connection, String hostname, int port) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                connectionsTableModel.addRow(connection);

            }
        });

    }

    @Override
    public void incomingConnectionAccepted(SocketAgent agent, final DenoboConnection connection) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                connectionsTableModel.addRow(connection);
                
            }
        });
        
    }

    @Override
    public void connectionClosed(SocketAgent agent, final DenoboConnection connection) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                /* 
                 * Iterate through the table and find the connection where the
                 * IP and port match then remove it.
                 */ 
                for (int row = 0; row < connectionsTableModel.getRowCount(); row++) {
                    if (connectionsTableModel.getValueAt(row, 0).equals(connection.getRemoteAddress())) {
                        if (connectionsTableModel.getValueAt(row, 1).equals(connection.getRemotePort())) {
                            connectionsTableModel.removeRow(row);
                        }
                    }
                }
                
            }
        });
        
    }

    @Override
    public void advertisingStarted(SocketAgent agent, int port) {
        // Not needed
    }

    @Override
    public void advertisingStopped(SocketAgent agent, int port) {
        // Not needed
    }

    @Override
    public void connectionAddFailed(SocketAgent agent, String hostname, int port) {
        // Not needed
    }

    
    
    /**
     * A TableModel for storing instances of DenoboConnection objects to be
     * displayed.
     *
     * @author Alex Mullen
     */
    private class ConnectionTableModel extends DefaultTableModel {

        /**
         * Creates a new instance of a ConnectionTableModel for displaying
         * DenoboConnection objects.
         */
        public ConnectionTableModel() {
            super(new Object[]{"ip", "port", "ping"}, 0);
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            // We want to make the entire table un-editable.
            return false;
        }

        /**
         * Adds a DenoboConnection into this table model as a new row.
         * 
         * @param connection 
         */
        public void addRow(DenoboConnection connection) {

            this.addRow(new Object[]{connection.getRemoteAddress(), connection.getRemotePort(), -1});

        }

    }

}
