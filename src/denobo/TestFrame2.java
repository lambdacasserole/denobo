package denobo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author Saul
 */
public class TestFrame2 extends JFrame implements MessageHandler {
    
    private Agent myAgent;
    private JTextField t;
    
    public TestFrame2(Agent agent) {
        myAgent = agent;
        myAgent.addMessageHandler(this);
        t = new JTextField();
        t.setPreferredSize(new Dimension(200, 20));
        this.setLayout(new BorderLayout());
        this.add(t, BorderLayout.CENTER);
    }

    @Override
    public void messageRecieved(String to, String from, String message) {
        t.setText(from + "," + to + "," + message); 
    }
    
}
