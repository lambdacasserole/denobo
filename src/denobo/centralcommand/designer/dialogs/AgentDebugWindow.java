package denobo.centralcommand.designer.dialogs;

import denobo.Agent;
import denobo.Message;
import denobo.MessageHandler;
import denobo.centralcommand.DenoboWindow;
import denobo.centralcommand.designer.AgentDisplayable;
import denobo.centralcommand.designer.NetworkDesigner;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

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
     * Used for spamming tons of messages. TODO: Delete when not needed anymore.
     */
    private final JButton spamButton;
    /**
     * Used for spam. TODO: deleted when finished with.
     */
    private Thread spamThread;
    /**
     * Instances of a text area for the agent name.
     */
    private final JComboBox agentSendArea;
    /**
     * Instances of a text area for the message.
     */
    private final JTextArea messageSendArea;
    /**
     * Instances of a table that will display the messages that are sent and
     * received.
     */
    private final JTable messageTable;
    /**
     * Instances of the debug table model.
     */
    private final DebugAgentTableModel messageTableModel;

    /**
     * Initialise a new instance AgentDebugWindow.
     *
     * @param agentModel the initial agentModel
     */
    public AgentDebugWindow(Agent agentModel) {
        super();
        this.agentModel = agentModel;

        agentModel.addMessageHandler(this);

        this.setLayout(new BorderLayout());
        this.setTitle("Debug Window [" + agentModel.getName() + "]");
        messageTableModel = new DebugAgentTableModel();
        messageTable = new JTable(messageTableModel);
        messageTable.setFillsViewportHeight(true);

        /**
         * South panel JPanel.
         */
        final JPanel southPanel = new JPanel();
        /**
         * The first row of the BoxLayout that uses the FlowLayout.
         */
        final JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        /**
         * The Second row of the BoxLayout that uses the FlowLayout.
         */
        final JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        /**
         * JLabel that shows the agentSendArea, this uses the FlowLayout.
         */
        final JPanel namePos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        /**
         * JLabel that shows the messageSendArea, this uses the FlowLayout.
         */
        final JPanel messagePos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        /**
         * BoxLayout that holds the firstRowPanel and secondRowPanel,
         * also the position of the JLabels.
         */
        final JPanel boxSouthPanel = new JPanel();
        boxSouthPanel.setLayout(new BoxLayout(boxSouthPanel, BoxLayout.Y_AXIS));

        this.setAlwaysOnTop(true);
        this.add(southPanel, BorderLayout.SOUTH);

        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        this.add(messageTableScrollPane, BorderLayout.CENTER);

        agentSendArea = new JComboBox();
        agentSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        agentSendArea.setEditable(true);
        firstRowPanel.add(agentSendArea);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        firstRowPanel.add(sendButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        firstRowPanel.add(clearButton);

        //TODO: remove when not needed anymore
        spamButton = new JButton("Spam");
        spamButton.addActionListener(this);
        firstRowPanel.add(spamButton);
        
        namePos.add(new JLabel("Name"));
        boxSouthPanel.add(namePos);
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

        messagePos.add(new JLabel("Message"));

        boxSouthPanel.add(messagePos);
        boxSouthPanel.add(secondRowPanel);

        southPanel.add(boxSouthPanel);

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();

    }

    /**
     * Show the Debug window at the current mouse point.
     * @param designer the designer workspace
     * @param position the initial position
     */
    public void showAt(NetworkDesigner designer, Point position) {

        agentSendArea.removeAllItems();
        for (AgentDisplayable current : designer.getAgentDisplayables()) {
            agentSendArea.addItem(current.getAgent().getName());
        }
        this.setLocation(position);
        this.setVisible(true);

    }

    /**
     * Send the message.
     */
    public void sendMessage() {
        messageTableModel.addRow(new Object[]{"To: " + (String) agentSendArea.getSelectedItem(),
            "mgs: " + messageSendArea.getText()});

        agentModel.sendMessage((String) agentSendArea.getSelectedItem(), messageSendArea.getText());
        messageSendArea.setText(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton) {
            sendMessage();
        } else if (e.getSource() == clearButton) {
            messageTableModel.setRowCount(0);
        } else if (e.getSource() == spamButton) {
            //TODO: Delete when testing finished.
            spamThread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000000; i++) {
                        agentModel.sendMessage((String) agentSendArea.getSelectedItem(), messageSendArea.getText() + i);
                    }
                }
            };
            spamThread.start();
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
    
    
    
    /**
     *
     * @author Lee Oliver
     */
    private class DebugAgentTableModel extends DefaultTableModel {

        /**
         * Initialise a new instance of DebugAgentTableModelold.
         */
        public DebugAgentTableModel() {
            super(new Object[]{"Name", "Message"}, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        
    }
    
}


