package denobo.centralcommand.designer;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Lee Oliver
 */
public class AgentDebugWindow implements ActionListener, MessageHandler {
    
    private final Agent agentModel;
    
    private JFrame frame = new JFrame();
    
    private JButton sendButton, clearButton;
    private JTextArea agentRecieveArea, messageRecieveArea, agentSendArea, messageSendArea;
    

    public AgentDebugWindow(Agent agentModel) {
        
        this.agentModel = agentModel;

        agentModel.addMessageHandler(this);
        
        
        
        frame.setLayout(new BorderLayout());
        frame.setTitle(agentModel.getName() + " Debug Window");

        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();
        
        frame.setAlwaysOnTop(true);
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        
        northPanel.setLayout(new GridLayout(1, 2));
        northPanel.add(new JLabel("Agent"));
        northPanel.add(new JLabel("Message"));
        
        centerPanel.setLayout(new GridLayout(1, 2));
        agentRecieveArea = new JTextArea(5, 20);
        agentRecieveArea.setEditable(false);
        agentRecieveArea.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.add(agentRecieveArea);
        
        messageRecieveArea = new JTextArea(5, 20);
        messageRecieveArea.setEditable(false);
        messageRecieveArea.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.add(messageRecieveArea);
        
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
        
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        
    }
    
    public void show(Point position) {
        
        frame.setLocation(position);
        frame.setVisible(true);
        
    }
    
    public void hide() {
        
        frame.setVisible(false);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == sendButton) {
            
            agentRecieveArea.append("To: " + agentSendArea.getText() + "\n");
            messageRecieveArea.append("mgs: " + messageSendArea.getText() + "\n");
            agentModel.sendMessage(agentSendArea.getText(), messageSendArea.getText());
            
        } else if (e.getSource() == clearButton) {
            
            agentRecieveArea.setText(null);
            messageRecieveArea.setText(null);
            
        }
        
    }

    @Override
    public void messageRecieved(Agent agent, Message message) {
        
        agentRecieveArea.append("From: " +  message.getFrom() + ":\n");
        messageRecieveArea.append("mgs: " + message.getData() + "\n");
        
    }

    @Override
    public void messageIntercepted(Agent agent, Message message) {
        
    }

}