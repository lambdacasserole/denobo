package denobo.centralcommand.designer.dialogs.monitor;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alex Mullen
 */
public class MessagesTab extends JPanel {
    
    // The filter options for the filter combo box
    private static final String receivedFilterOptionName = "Received";
    private static final String interceptedFilterOptionName = "Intercepted";
    
    private final MessageTableModel messagesReceivedTableModel;
    private final MessageTableModel messagesInterceptedTableModel;
    
    private final JTable messageTable;
    private final JButton clearMessageTableButton;
    private final JComboBox<String> messagesFilterComboBox;
    
    private final Agent agentModel;

    
    public MessagesTab(Agent agent) {
        
        this.agentModel = agent;

        // Instantiate "Messages" tab controls
        messagesFilterComboBox = new JComboBox<>(new String[] {receivedFilterOptionName, interceptedFilterOptionName});
        clearMessageTableButton = new JButton("Clear");
        
        messagesReceivedTableModel = new MessageTableModel();
        messagesInterceptedTableModel = new MessageTableModel();
        
        messageTable = new JTable(messagesReceivedTableModel);
        messageTable.setFillsViewportHeight(false);
        
        
        this.setLayout(new BorderLayout());
        
        final JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(new JLabel("Filter:"));
        northPanel.add(messagesFilterComboBox);
        northPanel.add(clearMessageTableButton);
        this.add(northPanel, BorderLayout.NORTH);
        
        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        this.add(messageTableScrollPane, BorderLayout.CENTER);
        
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
     * Returns which MessageTableModel represents what is selected from the "Filter"
     * combo box.
     * 
     * @return the MessageTableModel that is selected from the "Filter" combo box.
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
     * Handles the clear button been clicked.
     */
    private void handleMessageTableClearButtonClicked() {
        
        getSelectedMessageTableFromCombo().setRowCount(0);
        
    }

}
