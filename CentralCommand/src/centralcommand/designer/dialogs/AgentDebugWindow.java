package centralcommand.designer.dialogs;

import centralcommand.DenoboWindow;
import centralcommand.designer.AgentDisplayable;
import centralcommand.designer.NetworkDesigner;
import denobo.Agent;
import denobo.Message;
import denobo.MessageListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
 * This class represents the debug window for an agent.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentDebugWindow extends DenoboWindow implements ActionListener, MessageListener {

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
     * Used for ceasing to spam tons of messages.
     */
    private final JButton stopSpamButton;
    
    /**
     * Used for spam. TODO: deleted when finished with.
     */
    private Thread spamThread;

    /**
     * Instances of a text area for the agent name.
     */
    private final JComboBox<String> agentSendArea;

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
    @SuppressWarnings("LeakingThisInConstructor")
    public AgentDebugWindow(Agent agentModel) {
        
        super();
        this.agentModel = agentModel;

        agentModel.addMessageListener(this);

        this.setLayout(new BorderLayout());
        this.setTitle("Debug Window [" + agentModel.getName() + "]");
        messageTableModel = new DebugAgentTableModel();
        messageTable = new JTable(messageTableModel);
        messageTable.setFillsViewportHeight(true);

        //JPanels for the south layout.
        final JPanel southPanel = new JPanel();
        final JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel namePos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel messagePos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel boxSouthPanel = new JPanel();
        boxSouthPanel.setLayout(new BoxLayout(boxSouthPanel, BoxLayout.Y_AXIS));

        this.setAlwaysOnTop(true);
        this.add(southPanel, BorderLayout.SOUTH);

        final JScrollPane messageTableScrollPane = new JScrollPane(messageTable);
        this.add(messageTableScrollPane, BorderLayout.CENTER);

        agentSendArea = new JComboBox<>();
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

        stopSpamButton = new JButton("Stop Spam");
        stopSpamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (spamThread != null) {
                    spamThread.interrupt();
                }
            }
        });
        firstRowPanel.add(stopSpamButton);

        namePos.add(new JLabel("Name"));
        boxSouthPanel.add(namePos);
        boxSouthPanel.add(firstRowPanel);

        messageSendArea = new JTextArea(3, 40);
        messageSendArea.setBorder(BorderFactory.createLineBorder(Color.black));
        messageSendArea.setLineWrap(true);
        messageSendArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //Press enter to send the message.
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
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, 300));
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();

    }

    /**
     * Show the Debug window at the current mouse point.
     *
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
            
            final String spamReceipientName = (String) agentSendArea.getSelectedItem();
            
            spamThread = new Thread() {

                @Override
                public void run() {
                    
                    final long startTime = System.currentTimeMillis();
                    System.out.println("Spam started:");
                    
                    for (int i = 0; i < 100000; i++) {
                        agentModel.sendMessage(spamReceipientName, String.valueOf(i));
                        Thread.yield();
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException ex) {
//                            break;
//                        }
                    }
                    
                    final long elapsedTime = (System.currentTimeMillis() - startTime);
                    System.out.println("Spam taken: " + elapsedTime + "ms");
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
     * This class represents the table model for the debug window.
     *
     * @author Lee Oliver, Saul Johnson, Alex Mullen
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
