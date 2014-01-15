package denobo.centralcommand.designer;

import denobo.socket.SocketAgent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Represents a SocketAgent that can be displayed and moved in a NetworkDesigner.
 *
 * @author Alex Mullen
 */
public class SocketAgentDisplayable extends AgentDisplayable {

    /**
     * The image that visually represents a socket agent.
     */
    private static BufferedImage socketImage = null;
    
    static {
        /*
         * To save memory, we only need to load the image once since it won't
         * be changing.
         */
        final URL imageURL = SocketAgentDisplayable.class.getResource(
                "resources/socketagent.png");
        try {
            socketImage = ImageIO.read(imageURL);
        } catch (IOException ex) {
            System.err.println("Could not load resources/socketagent.png: " 
                    + ex.getMessage());
        }
    }
    
    /**
     * Initialised a new instance of a SocketAgentDisplayable.
     * 
     * @param agent the underlying socket agent
     * @param x     the x coordinate position
     * @param y     the y coordinate position
     */
    public SocketAgentDisplayable(SocketAgent agent, int x, int y) {
        super(agent, x, y);
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(socketImage, bounds.x, bounds.y, null);
    }

}
