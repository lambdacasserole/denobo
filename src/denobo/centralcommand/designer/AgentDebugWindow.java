package denobo.centralcommand.designer;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.centralcommand.DenoboWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

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

    /**
     * Initialise a new instance AgentDebugWindow.
     * @param agentModel the Initial agentModel
     */
    public AgentDebugWindow(Agent agentModel) {
        super();
        this.agentModel = agentModel;

        agentModel.addMessageHandler(this);

        this.setLayout(new BorderLayout());
        this.setTitle("Debug Window [" + agentModel.getName() + "]") ;
        messageTableModel = new DebugAgentTableModel();
        messageTable = new JTable(messageTableModel);
        messageTable.setFillsViewportHeight(true);
        
        JPanel southPanel = new JPanel();
        JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel boxSouthPanel = new JPanel();
        boxSouthPanel.setLayout(new BoxLayout(boxSouthPanel, BoxLayout.Y_AXIS));

        this.setAlwaysOnTop(true);
        this.add(southPanel, BorderLayout.SOUTH);
        
        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        this.add(messageTableScrollPane, BorderLayout.CENTER);
        
        agentSendArea = new JTextArea(1, 10);
        agentSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        firstRowPanel.add(agentSendArea);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        firstRowPanel.add(sendButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        firstRowPanel.add(clearButton);

        boxSouthPanel.add(firstRowPanel);

        messageSendArea = new JTextArea(3, 40);
        messageSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        messageSendArea.setLineWrap(true);
        messageSendArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //
            }
 
            @Override
            public void keyPressed(KeyEvent e) {
                //
            }
 
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        final JScrollPane messageBoxScrollPane = new JScrollPane(messageSendArea);
        messageBoxScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        secondRowPanel.add(messageBoxScrollPane);

        boxSouthPanel.add(secondRowPanel);

        southPanel.add(boxSouthPanel);

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();

    }

    public void showAt(Point position) {

        this.setLocation(position);
        this.setVisible(true);

    }
    
    public void sendMessage() {
        messageTableModel.addRow(new Object[]{"To: " + agentSendArea.getText(),
            "mgs: " + messageSendArea.getText()});
 
        agentModel.sendMessage(agentSendArea.getText(), messageSendArea.getText());
        messageSendArea.setText(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton) {

            messageTableModel.addRow(new Object[]{"To: " + agentSendArea.getText(),
                "mgs: " + messageSendArea.getText()});

            agentModel.sendMessage(agentSendArea.getText(), messageSendArea.getText());

        } else if (e.getSource() == clearButton) {

            messageTableModel.setRowCount(0);

        }

    }

    @Override
    public void messageRecieved(Agent agent, final Message message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageTableModel.addRow(new Object[]{"From: " + message.getOriginator(),
                    "mgs: " + message.getData()});
            }
        });
    }

    @Override
    public void messageIntercepted(Agent agent, Message message) {
    }
}