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
    private final JComboBox messagesFilterComboBox;
    
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
        messagesFilterComboBox = new JComboBox(new Object[] {receivedFilterOptionName, interceptedFilterOptionName});
        
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
        messagesTab.add(northPanel, BorderLayout.NORTH);
        
        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        messagesTab.add(messageTableScrollPane, BorderLayout.CENTER);
        
        ////////////////////////////////////////////////////////////////////////
        
        
        
        
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
     * Handles the Message filtering combo box changing.
     */
    private void handleMessageFilterComboChanged() {
        
        // Determine what option was selected then change the message table model
        // to that.
        switch (messagesFilterComboBox.getSelectedItem().toString()) {
            
            case receivedFilterOptionName:
                messageTable.setModel(messagesReceivedTableModel);
                break;
                
            case interceptedFilterOptionName:
                messageTable.setModel(messagesInterceptedTableModel);
                break;
                
            default:
                // This shouldn't happen
                messageTable.setModel(messagesReceivedTableModel);
        }

    }
    
}
