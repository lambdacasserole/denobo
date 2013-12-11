package denobo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alex
 */
public class TestMessageFrame extends JFrame implements ActionListener, MessageHandler, NetworkPortalObserver {

    private Agent messageAgent;
    private NetworkPortal networkPortal;

    private JTextField ipTextField, portTextField, localAgentNameField, remoteAgentNameField;
    private JTextArea receiveTextField, sendTextField;
    private JButton connectButton, sendButton, disconnectButton;

    public TestMessageFrame() {
        
        ipTextField = new JTextField(10);
        ipTextField.setText("localhost");

        portTextField = new JTextField(5);
        portTextField.setText("4757");

        localAgentNameField = new JTextField(10);
        localAgentNameField.setText("Alex");

        remoteAgentNameField = new JTextField(10);
        remoteAgentNameField.setText("Lee");

        receiveTextField = new JTextArea(30, 40);
        receiveTextField.setEditable(false);
        receiveTextField.setBorder(BorderFactory.createLineBorder(Color.black));

        sendTextField = new JTextArea(3, 30);
        sendTextField.setBorder(BorderFactory.createLineBorder(Color.black));

        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(this);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(ipTextField, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(portTextField, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(connectButton, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(disconnectButton, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(localAgentNameField, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(remoteAgentNameField, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(receiveTextField, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(sendTextField, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(sendButton, c);
        
        
        networkPortal = new NetworkPortal("local-network-portal", 4757);
        networkPortal.addObserver(this);
        
        messageAgent = new Agent(localAgentNameField.getText(), false);
        messageAgent.addMessageHandler(this);
        networkPortal.addAgent(messageAgent);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == connectButton) {
            networkPortal.addConnection(ipTextField.getText(), Integer.valueOf(portTextField.getText()));
            receiveTextField.setText(null);
        } else if (ae.getSource() == sendButton) {
            messageAgent.sendMessage(remoteAgentNameField.getText(), sendTextField.getText());
            receiveTextField.append("Me: " + sendTextField.getText() + "\n");
            sendTextField.setText(null);
        } else if (ae.getSource() == disconnectButton) {
            networkPortal.shutdown();
        }
    }

    @Override
    public void messageRecieved(Message message) {
        receiveTextField.append(message.getFrom() + ": " + message.getMessage() + "\n");
    }

    @Override
    public void incomingConnectionAccepted(String ip, int port) {
        receiveTextField.append("[" + ip + ":" + port + "] has connected\n");
    }

    @Override
    public void connectionClosed(String ip, int port) {
        receiveTextField.append("[" + ip + ":" + port + "] has diconnected\n");
    }
}
