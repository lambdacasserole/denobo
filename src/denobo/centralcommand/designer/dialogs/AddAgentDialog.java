package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A dialog for choosing an agent to add into the network designer.
 *
 * @author Alex Mullen
 */
public class AddAgentDialog {
    
    private static final String agentNamePrefix = "Agent";
    private static int agentNameNumber = 1;

    private final JDialog dialog;
    
    private final JTextField agentNameField;
    private final JTextField agentFilePathField;
    private final JButton browseButton;
    private final JButton addButton;
    private final JButton cancelButton;
    
    private Agent agentToReturn;
    
    /**
     * Creates an instance of AddAgentDialog.
     */
    public AddAgentDialog() {

        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Add Agent");
        dialog.setResizable(false);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);    
        
        
        agentNameField = new JTextField(12);
        agentNameField.setText(agentNamePrefix + agentNameNumber);
        agentFilePathField = new JTextField(24);
        agentFilePathField.setEditable(false);
        
        browseButton = new JButton("Browse");
        browseButton.setEnabled(false);
        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
        
        registerActionListeners();
        

        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        dialog.add(contentPanel);

        final JPanel agentNameRow = new JPanel();
        agentNameRow.add(new JLabel("Agent Name:"));
        agentNameRow.add(agentNameField);
        contentPanel.add(agentNameRow);
        
        final JPanel fileFieldRow = new JPanel();
        fileFieldRow.add(new JLabel("File:"));
        fileFieldRow.add(agentFilePathField);
        fileFieldRow.add(browseButton);
        contentPanel.add(fileFieldRow);
        
        
        final JPanel addCancelButtonRow = new JPanel();
        addCancelButtonRow.add(addButton);
        addCancelButtonRow.add(cancelButton);


        dialog.add(addCancelButtonRow, BorderLayout.SOUTH);
        
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
        
        agentNameField.setText(agentNamePrefix + agentNameNumber);
        
        // Execution blocks after this statement until dialog.dispose() is called.
        dialog.setVisible(true);        

        return agentToReturn;
    }
    
    /**
     * Registers all the action listeners required for the controls we are using.
     */
    private void registerActionListeners() {
        
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleBrowseButtonClicked();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleAddButtonClicked();
            }
        });
                
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleCancelButtonClicked();
            }
        });
        
    }
    
    /**
     * Handles the browse button being clicked.
     */
    private void handleBrowseButtonClicked() {
        
        // Browse for agent class file
        final JFileChooser fileBrowseBox = new JFileChooser();
        final int fileBrowseResult = fileBrowseBox.showOpenDialog(dialog);
        if (fileBrowseResult == JFileChooser.APPROVE_OPTION) {
            
            // Set the text box to the full path of the file
            final File file = fileBrowseBox.getSelectedFile();
            agentFilePathField.setText(file.getAbsolutePath());
            
        }

    }
    
    /**
     * Handles the add button being clicked.
     */
    private void handleAddButtonClicked() {
        
        // Attempt to load agent class file
        
        agentToReturn = new Agent(agentNameField.getText());
        agentNameNumber++;
        
        // TODO: Implement this

//        final File file = new File("C:\\Users\\Alex\\Desktop\\");
//        if (file.isFile())
//        try {
//            
//            final URL url = file.toURI().toURL();
//            final URL[] urls = new URL[]{url};
//            
//            
//            
//            
//            final ClassLoader cl = new URLClassLoader(urls);
//            Class loadedClass = cl.loadClass("denobo.Agent");
//
//        } catch (MalformedURLException | ClassNotFoundException ex) {
//            
//            ex.printStackTrace();
//            
//        }

        dialog.dispose();
        
    }
    
    /**
     * Handles the cancel button being clicked.
     */
    private void handleCancelButtonClicked() {
        
        // Cancel was pressed
        
        agentToReturn = null;
        dialog.dispose();
        
    }

}
