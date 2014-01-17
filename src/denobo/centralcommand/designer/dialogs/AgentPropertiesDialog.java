package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import denobo.socket.SocketAgent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A dialog for displaying the properties of an Agent.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentPropertiesDialog {
    
    /**
     * The underlying JDialog this uses to display itself.
     */
    private final JDialog dialog;
    
    
    // Tab holder and tabs
    
    /**
     * The pane to hold our tabs for each type of properties.
     */
    private final JTabbedPane tabHolder;
    
    /**
     * The "Socket" tab container.
     */
    private final JPanel socketTab;
    
    
    // Controls for general tab
    
    /**
     * The text field for displaying the name of the selected agent.
     */
    private final JTextField agentNameField;
    
    /**
     * The radio button that is selected for a selected cloneable agent.
     */
    private final JRadioButton agentCloneableRadioButton;
    
    /**
     * The radio button that is selected for a selected non-cloneable agent.
     */
    private final JRadioButton agentNonCloneableRadioButton;
    
    
    // Controls for Socket tab
    
    /**
     * The label whose text is set to "Yes" or "No" depending on whether the
     * selected agent is currently advertising.
     */
    private final JLabel advertisingStatusLabel;
    
    /**
     * The label whose text is set to the port the selected agent is advertising
     * on or "N/A" if it is not currently advertising.
     */
    private final JLabel advertisingPortLabel;
    
    /**
     * The button that can be clicked to stop the currently selected agent from
     * advertising.
     */
    private final JButton stopAdvertisingButton;
    
    /**
     * The button that can be clicked to start advertising on the currently
     * selected agent.
     */
    private final JButton startAdvertisingButton;
    
    /**
     * The text field where the port to advertise on can be entered.
     */
    private final JTextField advertisePortField;
    
    /**
     * The check box that currently only displays whether the selected agent
     * encrypts data sent over sockets.
     */
    private final JCheckBox encryptionCheckBox;
    
    /**
     * The label that has its text set to the current compression being used on
     * data transferred over sockets.
     */
    private final JLabel compressionTypeLabel;
    
    
    // Controls for the dialog
    
    /**
     * The "OK" button that will simply close the dialog.
     */
    private final JButton okButton;
    
    /**
     * The "Apply" button that will save any potential property changes then
     * close the dialog.
     */
    private final JButton applyButton;

    /**
     * The agent to display the properties for.
     */
    private Agent agentModel;
    
    
    /**
     * Creates an instance of AgentPropertiesDialog.
     */
    public AgentPropertiesDialog() {
        
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Properties");
        dialog.setResizable(false);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);       
        
        
        agentNameField = new JTextField(12);
        agentNameField.setEditable(false);
        agentCloneableRadioButton = new JRadioButton("Cloneable");
        agentCloneableRadioButton.setEnabled(false);
        agentNonCloneableRadioButton = new JRadioButton("Non-Cloneable");
        agentNonCloneableRadioButton.setEnabled(false);
        final ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(agentCloneableRadioButton);
        radioButtonGroup.add(agentNonCloneableRadioButton);
        
        advertisingStatusLabel = new JLabel("No");
        advertisingPortLabel = new JLabel("N/A");
        stopAdvertisingButton = new JButton("Stop Advertising");
        startAdvertisingButton = new JButton("Start Advertising");
        advertisePortField = new JTextField("4757", 5);
        
        encryptionCheckBox = new JCheckBox("", false);
        encryptionCheckBox.setEnabled(false);
        
        compressionTypeLabel = new JLabel("none");
        
        okButton = new JButton("OK");
        applyButton = new JButton("Apply");

        registerActionListeners();
        

        
        // Create and add tab holder
        tabHolder = new JTabbedPane();
        dialog.add(tabHolder, BorderLayout.CENTER);
        
        
        // Create the "General" tab
        final JPanel generalTab = new JPanel(new BorderLayout());
        tabHolder.addTab("General", generalTab);
        
        final JPanel generalContentPanel = new JPanel();
        generalContentPanel.setLayout(new BoxLayout(generalContentPanel, BoxLayout.Y_AXIS));
        generalTab.add(generalContentPanel, BorderLayout.NORTH);
        
        final JPanel agentNameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agentNameRow.add(new JLabel("Agent Name:"));
        agentNameRow.add(agentNameField);
        generalContentPanel.add(agentNameRow);
        
        final JPanel cloneableRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cloneableRow.add(agentCloneableRadioButton);
        cloneableRow.add(agentNonCloneableRadioButton);
        generalContentPanel.add(cloneableRow);

        
        // Create the "Socket" tab
        socketTab = new JPanel(new BorderLayout());
        tabHolder.addTab("Socket", socketTab);
        
        final JPanel socketContentPanel = new JPanel();
        socketContentPanel.setLayout(new BoxLayout(socketContentPanel, BoxLayout.Y_AXIS));
        socketTab.add(socketContentPanel, BorderLayout.NORTH);
        
        final JPanel advertisingStatusRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        advertisingStatusRowPanel.add(new JLabel("Advertising:"));
        advertisingStatusRowPanel.add(advertisingStatusLabel);
        socketContentPanel.add(advertisingStatusRowPanel);
        
        final JPanel advertisingPortRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        advertisingPortRowPanel.add(new JLabel("Advertising Port:"));
        advertisingPortRowPanel.add(advertisingPortLabel);
        socketContentPanel.add(advertisingPortRowPanel);
        
        final JPanel advertisingPortFieldRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        advertisingPortFieldRowPanel.add(new JLabel("Port:"));
        advertisingPortFieldRowPanel.add(advertisePortField);
        advertisingPortFieldRowPanel.add(startAdvertisingButton);
        socketContentPanel.add(advertisingPortFieldRowPanel);
        
        final JPanel advertisingButtonsRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        advertisingButtonsRowPanel.add(stopAdvertisingButton);
        socketContentPanel.add(advertisingButtonsRowPanel);
        
        socketContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        socketContentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        socketContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        final JPanel encryptionOptionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encryptionOptionRow.add(new JLabel("Encryption:"));
        encryptionOptionRow.add(encryptionCheckBox);
        socketContentPanel.add(encryptionOptionRow);
        
        final JPanel compressionOptionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        compressionOptionRow.add(new JLabel("Compression:"));
        compressionOptionRow.add(compressionTypeLabel);
        socketContentPanel.add(compressionOptionRow);
        
        socketContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        
        // Create and place any buttons at the bottom of the panel outside the tabs
        final JPanel bottomButtonRowPanel = new JPanel();
        bottomButtonRowPanel.add(okButton);
        bottomButtonRowPanel.add(applyButton);
        dialog.add(bottomButtonRowPanel, BorderLayout.SOUTH);
        

        dialog.pack();
    }

    /**
     * Registers all the action listeners required for the controls we are using.
     */
    private void registerActionListeners() {
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkButtonClicked();
            }
        });
        
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleApplyButtonClicked();
            }
        });
        
        stopAdvertisingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStopAdvertisingButtonClicked();
            }            
        });
        
        startAdvertisingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStartAdvertisingButtonClicked();
            }   
        });
        
    }
    
    /**
     * Makes the dialog visible.
     * 
     * @param position      The position to place the dialog.
     * @param agent         The agent to display the properties of.
     */
    public void showDialog(Point position, Agent agent) {
        
        // Save a reference to the agent for any action listeners
        agentModel = agent;
        
        updateGeneralTabProperties(agent);
        
        // Check if the agent has socket capabilities
        if (agent instanceof SocketAgent) {
            
            // Show the socket tab then update the socket properties of the agent
            tabHolder.addTab("Socket", socketTab);
            updateSocketTabProperties((SocketAgent) agent);

        } else {
            
            // Remove the socket tab if the agent doesn't have socket capabilities
            tabHolder.remove(socketTab);
            
        }
        
        dialog.setTitle("Properties [" + agent.getName() + "]");
        dialog.setLocation(position);
        dialog.setVisible(true);
        
    }
    
    /**
     * Updates the properties for the specified agent on the "General" tab.
     * 
     * @param agent     The agent to display the general properties for.
     */
    private void updateGeneralTabProperties(Agent agent) {
        
        // Display agent name
        agentNameField.setText(agent.getName());

        // Select the correct radio button on whether this agent is cloneable
        // or not
        if (agent.isCloneable()) {
            agentCloneableRadioButton.setSelected(true);
        } else {
            agentNonCloneableRadioButton.setSelected(true);
        }
        
    }
    
    /**
     * Updates the properties for the specified socket agent on the "Socket" tab.
     * 
     * @param socketAgent       The socket agent to display socket properties for.
     */
    private void updateSocketTabProperties(SocketAgent socketAgent) {

        // Check if the socket agent is currently advertising
        if (socketAgent.isAdvertising()) {
            
            advertisingStatusLabel.setForeground(Color.GREEN);
            advertisingStatusLabel.setText("Yes");
            advertisingPortLabel.setText(String.valueOf(socketAgent.getAdvertisingPort()));
            stopAdvertisingButton.setEnabled(true);
            startAdvertisingButton.setEnabled(false);
            advertisePortField.setEditable(false);

        } else {
            
            advertisingStatusLabel.setForeground(Color.BLACK);
            advertisingStatusLabel.setText("No");
            advertisingPortLabel.setText("N/A");
            stopAdvertisingButton.setEnabled(false);
            startAdvertisingButton.setEnabled(true);
            advertisePortField.setEditable(true);
            
        }
        
        encryptionCheckBox.setSelected(socketAgent.getConfiguration().getIsSecure());
        compressionTypeLabel.setText(socketAgent.getConfiguration().getCompression().getName());

    }
    
    /**
     * Handles the OK button being clicked.
     */
    private void handleOkButtonClicked() {
        
        // Just close the dialog
        dialog.dispose();
        
    }
    
    /**
     * Handles the Apply button being clicked.
     */
    private void handleApplyButtonClicked() {
        
        // Apply any settings changed
        
        // Close the dialog
        dialog.dispose();
        
    }
    
    /**
     * Handles the "Stop Advertising" button being clicked.
     */
    private void handleStopAdvertisingButtonClicked() {
        
        final SocketAgent socketAgent = (SocketAgent) agentModel;
        
        socketAgent.stopAdvertising();
        
        updateSocketTabProperties(socketAgent);

    }
    
    /**
     * Handles the "Start Advertising" button being clicked.
     */
    private void handleStartAdvertisingButtonClicked() {
        
        final SocketAgent socketAgent = (SocketAgent) agentModel;
        final String portString = advertisePortField.getText().trim();
        
        if (portString.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "A port number needs to specified.", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            
            final int portNumber = Integer.parseInt(portString);
            socketAgent.startAdvertising(portNumber);
            updateSocketTabProperties(socketAgent);
            
        } catch (NumberFormatException e) {
            
            JOptionPane.showMessageDialog(dialog, "'" + portString + "' is not a valid port number!", "Error!", JOptionPane.ERROR_MESSAGE);
            
        } catch (IOException e) {
            
            JOptionPane.showMessageDialog(dialog, "The socket agent was unable to start advertising.\n\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            
        }

    }
    
}
