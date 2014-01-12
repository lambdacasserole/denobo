package denobo.centralcommand.designer;

import denobo.Agent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Alex Mullen
 */
public class SocketAgentDisplayable extends AgentDisplayable {

    private static BufferedImage socketImage = null;
    
    static {
        final URL imageURL = SocketAgentDisplayable.class.getResource("resources/socketagent.png");
        try {
            socketImage = ImageIO.read(imageURL);
        } catch (IOException ex) {
            System.err.println("Could not load resources/socketagent.png: " + ex.getMessage());
        }
    }
    
    public SocketAgentDisplayable(Agent agent, int x, int y) {
        super(agent, x, y);
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(socketImage, bounds.x, bounds.y, null);
    }

}
