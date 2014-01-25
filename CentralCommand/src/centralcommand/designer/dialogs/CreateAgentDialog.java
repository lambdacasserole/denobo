package centralcommand.designer.dialogs;

import denobo.Agent;
import denobo.compression.Compressor;
import denobo.socket.SocketAgent;
import denobo.socket.SocketAgentConfiguration;
import denobo.socket.connection.Credentials;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 * A dialog for choosing an agent to add into the network designer.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class CreateAgentDialog {
    
    /**
     * The default name prefix to give each agent.
     */
    private static final String AGENT_NAME_PREFIX = "Agent";
    
    /**
     * The counter that increments on each agent creation that we append to the
     * end of the default agent name prefix to form a unique agent name.
     */
    private static int agentNameCounter = 1;

    /**
     * The underlying JDialog this uses to display itself.
     */
    private final JDialog dialog;
    
    
    // Controls for every agent type
    
    /**
     * The text field for entering the name of the agent.
     */
    private final JTextField agentNameField;
    
    /**
     * The check box for choosing whether the agent will be cloneable or not.
     */
    private final JCheckBox cloneableCheckBox;
  
    
    // Labels for socket configuration controls. Saving a reference to them so
    // we can enable and disable them.
    
    /**
     * The label for labelling the port text field.
     */
    private final JLabel portLabel = new JLabel("Port:");
    
    /**
     * The label for labelling the maximum connections text field.
     */
    private final JLabel maxConnectionsLabel = new JLabel("Maximum Connections:");
    
    /**
     * The label for labelling the password text field.
     */
    private final JLabel passwordLabel = new JLabel("Password:");
    
    /**
     * The label for labelling for compression drop down list.
     */
    private final JLabel compressionLabel = new JLabel("Compression:");
    
    
    // Controls for agents with socket capababilities
    
    /**
     * The check box for choosing whether the current agent will have socket
     * functionality.
     */
    private final JCheckBox socketFunctionalityCheckBox;
    
    /**
     * The text field for entering what port this agent will advertise on.
     */
    private final JTextField portField;
    
    /**
     * The text field for entering the maximum number of connections the agent
     * will allow.
     */
    private final JTextField maxConnectionsField;
    
    /**
     * The check box for choosing whether the agent will initially start
     * advertising once constructed.
     */
    private final JCheckBox startAdvertisingCheckBox;
    
    /**
     * The password text field for entering the password used to protect the
     * agent from connections who don't know the password.
     */
    private final JPasswordField passwordField;
    
    /**
     * The check box for choosing whether the agent will encrypt data transmitted
     * over sockets.
     */
    private final JCheckBox encryptionCheckBox;
    
    /**
     * The combo box for choosing what compression to use for compressing data
     * transmitted over sockets.
     */
    private final JComboBox<String> compressionComboBox;
    
    /**
     * The button used for creating the agent and closing this dialog.
     */
    private final JButton createButton;
    
    /**
     * The button used for cancelling the current agent creation then closing
     * this dialog.
     */
    private final JButton cancelButton;
    
    /**
     * The created agent to return once the create button was clicked.
     */
    private Agent agentToReturn;
    
    /**
     * Creates a new CreateAgentDialog instance.
     */
    public CreateAgentDialog() {

        // Create and configure the dialog
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Create Agent");
        dialog.setResizable(false);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);    
        
        // Instantiate controls
        agentNameField = new JTextField(12);
        agentNameField.setText(AGENT_NAME_PREFIX + agentNameCounter);
        cloneableCheckBox = new JCheckBox("Cloneable", false);
        socketFunctionalityCheckBox = new JCheckBox("Enable Socket Functionality", false);
        portField = new JTextField("4757", 4);
        maxConnectionsField = new JTextField("10", 3);
        startAdvertisingCheckBox = new JCheckBox("Start Advertising", false);
        passwordField = new JPasswordField(10);

        encryptionCheckBox = new JCheckBox("Use Encryption", false);
        compressionComboBox = new JComboBox<>(new String[] {"none", "basic", "lzw"});
        
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        registerActionListeners();
        
        

        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        dialog.add(contentPanel, BorderLayout.NORTH);

        final JPanel agentNameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agentNameRow.add(new JLabel("Agent Name:"));
        agentNameRow.add(agentNameField);
        contentPanel.add(agentNameRow);

        final JPanel cloneableRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cloneableRow.add(cloneableCheckBox);
        contentPanel.add(cloneableRow);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        final JPanel enableSocketsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enableSocketsRow.add(socketFunctionalityCheckBox);
        contentPanel.add(enableSocketsRow);
        
        final JPanel maxConnectionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maxConnectionsRow.add(maxConnectionsLabel);
        maxConnectionsRow.add(maxConnectionsField);
        contentPanel.add(maxConnectionsRow);
        
        final JPanel startAdvertisingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startAdvertisingRow.add(portLabel);
        startAdvertisingRow.add(portField);
        startAdvertisingRow.add(Box.createRigidArea(new Dimension(10, 0)));
        startAdvertisingRow.add(startAdvertisingCheckBox);
        contentPanel.add(startAdvertisingRow);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        final JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);
        contentPanel.add(passwordRow);
        
        final JPanel encryptionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encryptionRow.add(encryptionCheckBox);
        contentPanel.add(encryptionRow);
        
        final JPanel compressionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        compressionRow.add(compressionLabel);
        compressionRow.add(compressionComboBox);
        contentPanel.add(compressionRow);
        
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        final JPanel createCancelButtonRow = new JPanel();
        createCancelButtonRow.add(createButton);
        createCancelButtonRow.add(cancelButton);
        dialog.add(createCancelButtonRow, BorderLayout.SOUTH);
        
        toggleSocketControls(socketFunctionalityCheckBox.isSelected());
        toggleAdvertisingControls(startAdvertisingCheckBox.isSelected() 
                                && socketFunctionalityCheckBox.isSelected());
        
        dialog.pack();
        
    }
    
    /**
     * Makes the dialog visible for the user to fill in.
     * 
     * @param position      The position to place the dialog.
     * @return              The agent that the user has added otherwise null is
     *                      returned if they press the cancel button or the close
     *                      button or abort in any other way.
     */
    public Agent showDialog(Point position) {
        
        dialog.setLocation(position);
        
        agentNameField.setText(AGENT_NAME_PREFIX + agentNameCounter);
        
        // Prevents us returning the previous agent in case the Close or cancel
        // button was clicked.
        agentToReturn = null;
        
        // Execution blocks after this statement until dialog.dispose() is called.
        dialog.setVisible(true);        

        return agentToReturn;
        
    }
    
    /**
     * Registers all the action listeners required for the controls we are using.
     */
    private void registerActionListeners() {

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateButtonClicked();
            }
        });
                
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelButtonClicked();
            }
        });
        
        socketFunctionalityCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSocketControls(socketFunctionalityCheckBox.isSelected());
                toggleAdvertisingControls(socketFunctionalityCheckBox.isSelected() 
                                            && startAdvertisingCheckBox.isSelected());
            }
        });
        
        startAdvertisingCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAdvertisingControls(startAdvertisingCheckBox.isSelected() 
                                            && socketFunctionalityCheckBox.isSelected());
            }
        });
        
    }
    
    
    /**
     * Handles the "Create" button being clicked.
     */
    private void handleCreateButtonClicked() {
        
        constructAgent();
        
        // Only close the dialog if we successfully constructed an agent as some
        // validation error might have prevented us constructing one.
        if (agentToReturn != null) {
            dialog.dispose();
        }

    }
    
    /**
     * Handles the cancel button being clicked.
     */
    private void handleCancelButtonClicked() {
        
        // Cancel was pressed
        dialog.dispose();
        
    }
    
    /**
     * Enables or disables the controls for configuring the creation of an agent
     * with socket capabilities.
     * 
     * @param enable true to enable the controls, false disables them.
     */
    private void toggleSocketControls(boolean enable) {

        portLabel.setEnabled(enable);
        maxConnectionsLabel.setEnabled(enable);
        passwordLabel.setEnabled(enable);
        compressionLabel.setEnabled(enable);
        
        startAdvertisingCheckBox.setEnabled(enable);
        portField.setEditable(enable);
        maxConnectionsField.setEditable(enable);
        passwordField.setEditable(enable);
        encryptionCheckBox.setEnabled(enable);
        compressionComboBox.setEnabled(enable);

    }
    
    /**
     * Enables or disables the controls for configuring whether the agent should
     * start advertising as soon as it is created.
     * 
     * @param enable true to enable the controls, false disables them.
     */
    private void toggleAdvertisingControls(boolean enable) {
        
        portLabel.setEnabled(enable);
        portField.setEditable(enable);
        
    }
    
    /**
     * Checks all inputted data on the form and displays error dialogs if some
     * data fails validation.
     * 
     * @return true if all the data passed validation otherwise false is returned
     * if there was something wrong with some of the data.
     */
    private boolean performValidation() {

        // Validate general settings
        agentNameField.setText(agentNameField.getText().trim());

        // Validate socket settings if they are enabled
        if (socketFunctionalityCheckBox.isSelected()) {
            
            maxConnectionsField.setText(maxConnectionsField.getText().trim());
            if (maxConnectionsField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "A maximum connection limit needs to be specified.", "Error!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
                            
            if (!isStringValidInteger(maxConnectionsField.getText())) {
                JOptionPane.showMessageDialog(dialog, "'" + maxConnectionsField.getText() + "' is not a valid number!", "Error!", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Validate advertising settings if they are enabled
            if (startAdvertisingCheckBox.isSelected()) {
               
                portField.setText(portField.getText().trim());
                if (portField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "A port number needs to specified.", "Error!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                if (!isStringValidInteger(portField.getText())) {
                    JOptionPane.showMessageDialog(dialog, "'" + portField.getText() + "' is not a valid port number!", "Error!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            }

        }
        
        return true;
    }
    
    /**
     * Helper method that determines whether some String represents a valid integer.
     * This keeps any validation code cleaner and uncluttered from exception blocks.
     * 
     * @param text  The string to check.
     * @return      true if the string represents a valid integer otherwise false
     *              is returned.
     */
    private boolean isStringValidInteger(String text) {
        
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Builds the agent from the configuration on the form.
     */
    private void constructAgent() {
        
        // Run validation checks and if they fail then don't proceed to constructing
        // the agent.
        if (!performValidation()) {
            return;
        }
        
        if (socketFunctionalityCheckBox.isSelected()) {

            final SocketAgentConfiguration agentConfig = new SocketAgentConfiguration();
            
            agentConfig.setMaximumConnections(Integer.parseInt(maxConnectionsField.getText()));
            agentConfig.setCredentialsHandler(new CredentialsPromptDialog());
            final String passwordSet = String.valueOf(passwordField.getPassword());
            if (!passwordSet.isEmpty()) {
                agentConfig.setMasterCredentials(new Credentials(null, passwordSet));
            }
            agentConfig.setIsSecure(encryptionCheckBox.isSelected());
            agentConfig.setCompression(
                    Compressor.instantiate((String) compressionComboBox.getSelectedItem()));
            
            final SocketAgent newSocketAgent = new SocketAgent(agentNameField.getText(), 
                    cloneableCheckBox.isSelected(), agentConfig);
            
            if (startAdvertisingCheckBox.isSelected()) {
                
                try {
                    newSocketAgent.startAdvertising(Integer.parseInt(portField.getText()));
                } catch (IOException e) {
                     JOptionPane.showMessageDialog(dialog, 
                             "The socket agent was unable to start advertising.\n\n" 
                                     + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
                }

            }
                      
            agentToReturn = newSocketAgent;

        } else {
            
            agentToReturn = new Agent(agentNameField.getText(), cloneableCheckBox.isSelected());
            
        }
        
        agentNameCounter++;

    }

}
