package denobo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author Saul
 */
public class TestFrame1 extends JFrame implements ActionListener {
    
    private Agent myAgent;
    JTextField t;
    
    public TestFrame1(Agent agent) {
        myAgent = agent;
        t = new JTextField();
        t.setPreferredSize(new Dimension(200, 20));
        JButton b = new JButton("Send");
        b.addActionListener(this);
        this.setLayout(new BorderLayout());
        this.add(t, BorderLayout.CENTER);
        this.add(b, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        myAgent.sendMessage("agent2", t.getText());
    }
    
}
