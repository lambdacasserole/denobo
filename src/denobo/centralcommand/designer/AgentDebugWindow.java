package denobo.centralcommand.designer;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.centralcommand.DenoboWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author Lee Oliver
 */
public class AgentDebugWindow extends DenoboWindow implements ActionListener, MessageHandler {
    
    /**
     * New instances of this agent.
     */
    private final Agent agentModel;
        
    /**
     * Instances of a send button.
     */
    private final JButton sendButton;
    
    /**
     * Instances of a clear button.
     */
    private final JButton clearButton;
    
    /**
     * Instances of a text area for the agent name.
     */
    private final JTextArea agentSendArea;
    
    /**
     * Instances of a text area for the message.
     */
    private final JTextArea messageSendArea;
    
    /**
     * Instances of a table that will display the messages that are sent and received.
     */
    private final JTable messageTable;
    
    /**
     * Instances of the debug table model.
     */
    private final DebugAgentTableModel messageTableModel;

    public AgentDebugWindow(Agent agentModel) {
        super();
        this.agentModel = agentModel;

        agentModel.addMessageHandler(this);
        
        
        
        this.setLayout(new BorderLayout());
        this.setTitle(agentModel.getName() + " Debug Window");
        messageTableModel = new DebugAgentTableModel();
        messageTable = new JTable(messageTableModel);
        messageTable.setFillsViewportHeight(false);

        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();
        
        this.setAlwaysOnTop(true);
        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        

        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        centerPanel.add(messageTableScrollPane);
        
        southPanel.setLayout(new GridLayout(1, 3));
        agentSendArea = new JTextArea(1, 10);
        agentSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        southPanel.add(agentSendArea);
        
        messageSendArea = new JTextArea(1, 10);
        messageSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        southPanel.add(messageSendArea);
        
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        southPanel.add(sendButton);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        southPanel.add(clearButton);
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        
    }
    
    public void showAt(Point position) {
        
        this.setLocation(position);
        this.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == sendButton) {
            
            messageTableModel.addRow(new Object[] {"To: " + agentSendArea.getText(), 
                "mgs: " + messageSendArea.getText()}
            );
//            agentRecieveArea.append("To: " + agentSendArea.getText() + "\n");
//            messageRecieveArea.append("mgs: " + messageSendArea.getText() + "\n");
            agentModel.sendMessage(agentSendArea.getText(), messageSendArea.getText());
            
        } else if (e.getSource() == clearButton) {
            
            messageTableModel.setRowCount(0);
            
        }
        
    }

    @Override
    public void messageRecieved(Agent agent, Message message) {
        
        messageTableModel.addRow(new Object[] {"From: " + agentSendArea.getText(), 
            "mgs: " + messageSendArea.getText()});
        
    }

    @Override
    public void messageIntercepted(Agent agent, Message message) {
        
    }

}