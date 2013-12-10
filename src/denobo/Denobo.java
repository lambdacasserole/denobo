package denobo;

import javax.swing.JFrame;

/**
 *
 * @author Saul
 */
public class Denobo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        Portal p1 = new Portal("portal1");
//        Portal p2 = new Portal("portal2");
//        Portal p3 = new Portal("portal3");
//
//        p1.addAgent(p2);
//        p1.addAgent(p3);
//
//        p2.addAgent(p1);
//        p2.addAgent(p3);
//
//        p3.addAgent(p1);
//        p3.addAgent(p2);
//
//        
//        Agent agent1 = new Agent("agent1", false);
//        agent1.addMessageHandler(new MessageHandler() {
//            @Override
//            public void messageRecieved(Message message) {
//                System.out.println("received message from " + message.getFrom() + ": " + message.getMessage());
//            }
//        });
//        
//        Agent agent2 = new Agent("agent2", false);
//        agent2.addMessageHandler(new MessageHandler() {
//            @Override
//            public void messageRecieved(Message message) {
//                System.out.println("received message from " + message.getFrom() + ": " + message.getMessage());
//            }
//        });
//                
//        Agent agent3 = new Agent("agent3", false);
//        agent3.addMessageHandler(new MessageHandler() {
//            @Override
//            public void messageRecieved(Message message) {
//                System.out.println("received message from " + message.getFrom() + ": " + message.getMessage());
//            }
//        });
//        
//        p1.addAgent(agent1);
//        p2.addAgent(agent2);
//        p3.addAgent(agent3);
//        
//        agent3.sendMessage("agent2", "Testing");
//
//        Agent a2 = new Agent("agent2", false);
//        NetworkPortal np2 = new NetworkPortal("networkportal2", 4757);
//        np2.addAgent(a2);
//        
        

        //TestFrame1 t1 = new TestFrame1(a2);

        TestMessageFrame frame = new TestMessageFrame();
        
        frame.setSize(500, 500);
        frame.setLocation(100, 300);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
