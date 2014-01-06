package denobo.centralcommand.designer;

import denobo.Agent;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Alex Mullen
 */
public class SocketAgentDisplayable extends AgentDisplayable {

    public SocketAgentDisplayable(Agent agent, int x, int y) {
        super(agent, x, y);
        if(socketImage == null) {
            final URL imageUrl = getClass().getResource("resources/socketagent.png");
            try {
                socketImage = ImageIO.read(imageUrl);
            } catch (IOException ex) {
                System.err.println("Could not load splash screen image.");
            }
        }
    }

    public void draw(Graphics g) {
        //        g.setColor(new Color(38, 127, 0));
        //        g.fillOval(bounds.x, bounds.y, height, width);
        //        g.setColor(Color.BLACK);
        //        g.drawOval(bounds.x, bounds.y, height, width);
        g.drawImage(socketImage, bounds.x, bounds.y, null);
    }

}
