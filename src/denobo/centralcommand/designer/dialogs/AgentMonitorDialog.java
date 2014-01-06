package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * A dialog for monitoring all activity happening on an agent.
 *
 * @author Alex Mullen
 */
public class AgentMonitorDialog {
    
    private final Agent agent;
    private final JDialog dialog;
    private final JTabbedPane tabHolder;
    
    
    // Controls for "Messages" tab
    private final MessageTableModel messagesReceivedTableModel;
    private final MessageTableModel messagesInterceptedTableModel;
    private final JTable messageTable;
    private final JComboBox<String> messagesFilterComboBox;
    private final JButton clearMessageTableButton;
    
    // Controls for "Connections" tab

    
    // The filter options for the filter combo box
    private static final String receivedFilterOptionName = "Received";
    private static final String interceptedFilterOptionName = "Intercepted";
    
    
    
    /**
     * Creates an instance of a new AgentMonitorDialog for an agent.
     * 
     * @param agent     The agent the dialog will display monitor data for.
     */
    public AgentMonitorDialog(Agent agent) {
        
        this.agent = agent;
        
        // Create and configure the dialog
        dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setTitle(agent.getName() + " Monitor");
        dialog.setResizable(true);
        dialog.setAlwaysOnTop(true);
        dialog.setModalityType(ModalityType.MODELESS);
        
        
        // Instantiate "Messages" tab controls
        messagesFilterComboBox = new JComboBox<>(new String[] {receivedFilterOptionName, interceptedFilterOptionName});
        
        clearMessageTableButton = new JButton("Clear");
        
        messagesReceivedTableModel = new MessageTableModel();
        messagesInterceptedTableModel = new MessageTableModel();
        
        messageTable = new JTable(messagesReceivedTableModel);
        messageTable.setFillsViewportHeight(false);

        agent.addMessageHandler(new MessageHandler() {

            @Override
            public void messageIntercepted(Agent agent, final Message message) {
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        messagesInterceptedTableModel.addRow(message);
                    }
                });
                
            }
            
            @Override
            public void messageRecieved(Agent agent, final Message message) {
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        messagesReceivedTableModel.addRow(message);
                    }
                });
                
            }
            
        });
        
        
        registerActionListeners();
        
        
        ////////////////////////////////////////////////////////////////////////

        // Create and add tab holder
        tabHolder = new JTabbedPane();
        dialog.add(tabHolder, BorderLayout.CENTER);
        
        ////////////////////////////////////////////////////////////////////////
        
        // Create and add the "Messages" tab
        final JPanel messagesTab = new JPanel(new BorderLayout());
        tabHolder.addTab("Messages", messagesTab);
        
        final JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(new JLabel("Filter:"));
        northPanel.add(messagesFilterComboBox);
        northPanel.add(clearMessageTableButton);
        messagesTab.add(northPanel, BorderLayout.NORTH);
        
        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        messagesTab.add(messageTableScrollPane, BorderLayout.CENTER);
        
        ////////////////////////////////////////////////////////////////////////
        
        // Create and add the "Connections" tab
        
        
        
        ////////////////////////////////////////////////////////////////////////
        
        dialog.pack();
        
    }
    
    /**
     * Registers all the action listeners required for the controls we are using.
     */
    private void registerActionListeners() {
        
        messagesFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMessageFilterComboChanged();
            }
        });
        
        clearMessageTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMessageTableClearButtonClicked();
            }
        });
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
        
        dialog.setVisible(false);
        
    }
    
    /**
     * Returns which MessageTableModel represents what is selected from the "Filter"
     * combo box.
     * 
     * @return  The MessageTableModel that is selected from the "Filter" combo box.
     */
    private MessageTableModel getSelectedMessageTableFromCombo() {
        
        switch (messagesFilterComboBox.getSelectedItem().toString()) {
            
            case receivedFilterOptionName:
                return messagesReceivedTableModel;
                
            case interceptedFilterOptionName:
                return messagesInterceptedTableModel;
                
            default:
                // This shouldn't happen but if it ever does, we'll default to
                // returning the received messages model.
                return messagesReceivedTableModel;
        }
        
    }
    
    /**
     * Handles the Message filtering combo box changing.
     */
    private void handleMessageFilterComboChanged() {
        
        messageTable.setModel(getSelectedMessageTableFromCombo());

    }
    
    /**
     * Handles the clear button been clicked on the "Messages" tab.
     */
    private void handleMessageTableClearButtonClicked() {
        
        getSelectedMessageTableFromCombo().setRowCount(0);
        
    }
    
}
