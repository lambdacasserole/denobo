package denobo.centralcommand.designer.dialogs;

import denobo.Actor;
import denobo.centralcommand.designer.AgentDisplayable;
import denobo.centralcommand.designer.AgentLink;
import denobo.centralcommand.designer.NetworkDesigner;
import denobo.socket.SocketAgent;
import denobo.socket.SocketAgentObserver;
import denobo.socket.connection.DenoboConnection;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * A dialog for displaying all the other agents connected to an agent.
 *
 * @author Alex Mullen
 */
public class AgentConnectionsDialog implements SocketAgentObserver {
    
    private final JDialog dialog;
    
    private final JTabbedPane tabHolder;
    private final JPanel remoteTab;
    
    // Controls for local tab
    private final DefaultListModel<AgentDisplayable> localListModel;
    private final DefaultComboBoxModel<AgentDisplayable> localAgentsComboModel;
    private final JList<AgentDisplayable> localConnectionList;
    private final JButton addLocalConnectionButton;
    private final JButton removeLocalConnectionButton;
    private final JComboBox localAgentsCanAddCombo;
    
    // Controls for Remote tab
    private final DefaultListModel<DenoboConnection> remoteConnectionModel;
    private final JList<DenoboConnection> remoteConnectionList;
    private final JButton disconnectRemoteConnectionButton;
    private final JButton connectButton;
    private final JTextField ipField;
    private final JTextField portField;
    
    
    private AgentDisplayable agentModel;
    private final NetworkDesigner networkDesigner;
    
    
    /**
     * Creates an instance of AgentConnectionsDialog.
     * 
     * @param networkDesigner   The network designer associated with this dialog.
     */
    public AgentConnectionsDialog(NetworkDesigner networkDesigner) {
        
        this.networkDesigner = networkDesigner;
        
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Connections");
        dialog.setResizable(true);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        
        

        // Instantiate "Local" tab controls
        localListModel = new DefaultListModel<>();
        localAgentsComboModel = new DefaultComboBoxModel<>();
        
        localConnectionList = new JList<>(localListModel);
        localConnectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        localConnectionList.setLayoutOrientation(JList.VERTICAL);
        localConnectionList.setVisibleRowCount(10);
        //localConnectionList.setCellRenderer(new AgentDisplayableListCellRenderer());
        

        removeLocalConnectionButton = new JButton("Remove");
        addLocalConnectionButton = new JButton("Add");
        
        localAgentsCanAddCombo = new JComboBox(localAgentsComboModel);
        // TODO: This generates a warning because localAgentsCanAddCombo isn't
        // a generic type, but I need this to set the preferred width
        // of this combo box without resorting to creating an agent with a long
        // name.
        localAgentsCanAddCombo.setPrototypeDisplayValue("example-socket-agent123");
        //localAgentsCanAddCombo.setRenderer(new AgentDisplayableListCellRenderer());
        
        // Instantiate "Remote" tab controls
        remoteConnectionModel = new DefaultListModel<>();
        
        remoteConnectionList = new JList<>(remoteConnectionModel);
        remoteConnectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        remoteConnectionList.setLayoutOrientation(JList.VERTICAL);
        remoteConnectionList.setVisibleRowCount(10);
        
        disconnectRemoteConnectionButton = new JButton("Disconnect");
        connectButton = new JButton("Connect");
        
        ipField = new JTextField(10);
        portField = new JTextField(4);
        

        registerActionListeners();
        
        
        ////////////////////////////////////////////////////////////////////////
        
        // Create and add tab holder
        tabHolder = new JTabbedPane();
        dialog.add(tabHolder, BorderLayout.CENTER);
        
        ////////////////////////////////////////////////////////////////////////
        
        // Create and add the "Local" tab
        final JPanel localTab = new JPanel(new GridBagLayout());
        tabHolder.addTab("Local", localTab);

        // Add the list control. Also add the list to a scroll pane so we can
        // make it scrollable.
        final JScrollPane localConnectionListScroller = new JScrollPane(localConnectionList);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        localTab.add(localConnectionListScroller, c);
        
        // Add the "Remove" button
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 5, 5);
        localTab.add(removeLocalConnectionButton, c);
        
        // Add the panel for adding a new connection/link
        final JPanel addLocalConnectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addLocalConnectionPanel.setBorder(new TitledBorder("Add Connection to"));
        addLocalConnectionPanel.add(new JLabel("Agent:"));
        addLocalConnectionPanel.add(localAgentsCanAddCombo);
        addLocalConnectionPanel.add(addLocalConnectionButton);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 5, 5);
        localTab.add(addLocalConnectionPanel, c);
        
        ////////////////////////////////////////////////////////////////////////
        
        // Create and add the "Remote" tab
        remoteTab = new JPanel(new GridBagLayout());
        tabHolder.addTab("Remote", remoteTab);
        
        // Add the list control. Also add the list to a scroll pane so we can
        // make it scrollable.
        final JScrollPane remoteConnectionListScroller = new JScrollPane(remoteConnectionList);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        remoteTab.add(remoteConnectionListScroller, c);
        
        // Add the "Disconnect" button
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 5, 5);
        remoteTab.add(disconnectRemoteConnectionButton, c);
        
        // Add the panel for adding a new remote connection
        final JPanel addRemoteConnectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRemoteConnectionPanel.setBorder(new TitledBorder("Add Connection to"));
        addRemoteConnectionPanel.add(new JLabel("Hostname:"));
        addRemoteConnectionPanel.add(ipField);
        addRemoteConnectionPanel.add(new JLabel("Port:"));
        addRemoteConnectionPanel.add(portField);
        addRemoteConnectionPanel.add(connectButton);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 5, 5);
        remoteTab.add(addRemoteConnectionPanel, c);
        
        ////////////////////////////////////////////////////////////////////////
        
        dialog.pack();
        
    }
    
    /**
     * Registers all the action listeners required for the controls we are using.
     */
    private void registerActionListeners() {
        
        addLocalConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddButtonClicked();
            }
        });
        
        removeLocalConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveButtonClicked();
            }
        });

        disconnectRemoteConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDisconnectButtonClicked();
            }
        });
        
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConnectButtonClicked();
            }
        });        

    }

    /**
     * Makes the dialog visible.
     * 
     * @param position      The position to place the dialog.
     * @param agent         The agent to display all the connections of.
     */
    public void showDialog(Point position, AgentDisplayable agent) {
        
        agentModel = agent;
        final List<AgentDisplayable> allLocalAgents = networkDesigner.getAgentDisplayables();
        
        // Clear the list and combo models
        localListModel.clear();
        localAgentsComboModel.removeAllElements();
        
        remoteConnectionModel.clear();
        
        
        // List of local agents connected
        for (Actor currentActorConnected : agentModel.getAgent().getConnectedActors()) {
            for (AgentDisplayable currentAgentDisplayble : allLocalAgents) {
                if (currentAgentDisplayble.getAgent() == currentActorConnected) {
                    localListModel.addElement(currentAgentDisplayble);
                }
            }
        }
        removeLocalConnectionButton.setEnabled(localListModel.getSize() > 0);
        

        // Combo box of local agents that can be connected
        for (AgentDisplayable currentAgentDisplayable : allLocalAgents) {
            if (!(localListModel.contains(currentAgentDisplayable)) && currentAgentDisplayable != agentModel) {
                localAgentsComboModel.addElement(currentAgentDisplayable);
            }
        }
        addLocalConnectionButton.setEnabled(localAgentsComboModel.getSize() > 0);
       

        
        // Check if the agent has socket capabilities and if it does, show the
        // "Remote" tab with a list of connections.
        if (agentModel.getAgent() instanceof SocketAgent) {
            
            tabHolder.addTab("Remote", remoteTab);
           
            final SocketAgent socketAgent = (SocketAgent) agentModel.getAgent();
            for (DenoboConnection currentConnection : socketAgent.getConnections()) {
                remoteConnectionModel.addElement(currentConnection);
            }
            
            socketAgent.addObserver(this);
            
        } else {

            tabHolder.remove(remoteTab);
            
        }
        
        

        
        dialog.setTitle(agentModel.getAgent().getName() + " Connections");
        dialog.setLocation(position);
        dialog.setVisible(true);
        
        if (agentModel.getAgent() instanceof SocketAgent) {
            ((SocketAgent) agentModel.getAgent()).removeObserver(this);
        }
        
    }

    /**
     * Handles the "Add" button being clicked on the "Local" tab.
     */
    private void handleAddButtonClicked() {
        
        final AgentDisplayable actorSelected = (AgentDisplayable) localAgentsComboModel.getSelectedItem();
        if (agentModel.getAgent().connectActor(actorSelected.getAgent())) {
            
            localAgentsComboModel.removeElement(actorSelected);
            localListModel.addElement(actorSelected);
            
            removeLocalConnectionButton.setEnabled(localListModel.getSize() > 0);
            addLocalConnectionButton.setEnabled(localAgentsComboModel.getSize() > 0);

            networkDesigner.getAgentLinks().add(new AgentLink(actorSelected, agentModel));
            networkDesigner.repaint();
            
        }
        
    }
    
    /**
     * Handles the "Remove" button being clicked on the "Local" tab.
     */
    private void handleRemoveButtonClicked() {
     
        for (Object currentObject : localConnectionList.getSelectedValuesList()) {
            
            final AgentDisplayable currentAgent = (AgentDisplayable) currentObject;

            localListModel.removeElement(currentObject);
            localAgentsComboModel.addElement(currentAgent);

            networkDesigner.removeAnyLinksContaining(agentModel, currentAgent);

        }
        
        removeLocalConnectionButton.setEnabled(localListModel.getSize() > 0);
        addLocalConnectionButton.setEnabled(localAgentsComboModel.getSize() > 0);

    }

    /**
     * Handles the "Disconnect" button being clicked on the "Remote" tab.
     */
    private void handleDisconnectButtonClicked() {
        
        for (Object currentConnectionObject : remoteConnectionList.getSelectedValuesList()) {
            ((DenoboConnection) currentConnectionObject).disconnect();
            remoteConnectionModel.removeElement(currentConnectionObject);
        }

    }
    
    /**
     * Handles the "Connect" button being clicked on the "Remote" tab.
     */
    private void handleConnectButtonClicked() {

        final String hostname = ipField.getText().trim();
        final String portString = portField.getText().trim();
        
        if (hostname.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "A hostname needs to specified.", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (portString.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "A port needs to specified.", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            
            final int port = Integer.parseInt(portString);
            ((SocketAgent) agentModel.getAgent()).addConnection(hostname, port);
            
        } catch (NumberFormatException e) {
            
             JOptionPane.showMessageDialog(dialog, "'" + portString + "' is not a valid port number!", "Error!", JOptionPane.ERROR_MESSAGE);           
        
        }

    }

    @Override
    public void incomingConnectionAccepted(SocketAgent agent, final DenoboConnection connection) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                remoteConnectionModel.addElement(connection);
            }
        });
        
    }

    @Override
    public void connectionClosed(SocketAgent agent, final DenoboConnection connection) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                remoteConnectionModel.removeElement(connection);
            }
        });
        
    }

    @Override
    public void connectionAddSucceeded(SocketAgent agent, final DenoboConnection connection, String hostname, int port) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                remoteConnectionModel.addElement(connection);
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

}
