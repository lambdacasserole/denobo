package denobo.centralcommand;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.socket.SocketAgent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Lee Oliver
 */
public class DebugAgent extends SocketAgent implements ActionListener, MessageHandler {
    
    private JFrame frame = new JFrame();
    
    private JButton sendButton, clearButton;
    private JTextField agentName, message;
    private JTextArea agentRecieveArea, messageRecieveArea, agentSendArea, messageSendArea;
    private GridLayout northGrid, centerGrid, southGrid;
    private JPanel northPanel, centerPanel, southPanel;
    

    public DebugAgent(String name, int maxConnections) {
        super(name, maxConnections);
        try {
            this.startAdvertising(maxConnections);
        } catch (IOException ex) {
            Logger.getLogger(DebugAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.addMessageHandler(this);
        
        northGrid = new GridLayout(1, 2);
        centerGrid = new GridLayout(1, 2);
        southGrid = new GridLayout(1, 3);
        
        northPanel = new JPanel();
        centerPanel = new JPanel();
        southPanel = new JPanel();
        
        frame.setLayout(new BorderLayout());
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        
        northPanel.setLayout(northGrid);
        agentName = new JTextField(10);
        agentName.setText("Agent");
        northPanel.add(agentName);
        
        message = new JTextField(10);
        message.setText("Message");
        northPanel.add(message);
        
        centerPanel.setLayout(centerGrid);
        agentRecieveArea = new JTextArea(5, 20);
        agentRecieveArea.setEditable(false);
        agentRecieveArea.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.add(agentRecieveArea);
        
        messageRecieveArea = new JTextArea(5, 20);
        messageRecieveArea.setEditable(false);
        messageRecieveArea.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.add(messageRecieveArea);
        
        southPanel.setLayout(southGrid);
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
        
        frame.pack();
    }
    
    public void show() {
        frame.setLocation(100, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public void hide() {
        frame.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sendButton) {
            agentRecieveArea.append("To: " + agentSendArea.getText() + "\n");
            messageRecieveArea.append("mgs: " + messageSendArea.getText() + "\n");
        } else if(e.getSource() == clearButton) {
            agentRecieveArea.setText(null);
            messageRecieveArea.setText(null);
        }
    }

    @Override
    public void messageRecieved(Agent agent, Message message) {
        System.out.println("message got");
        agentRecieveArea.append("From: " +  message.getFrom() + ":\n");
        messageRecieveArea.append("mgs: " + message.getData() + "\n");
    }
}
