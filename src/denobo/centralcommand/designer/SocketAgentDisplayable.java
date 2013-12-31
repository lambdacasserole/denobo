package denobo.centralcommand.designer;

import denobo.Agent;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Alex Mullen
 */
public class SocketAgentDisplayable extends AgentDisplayable {

    public SocketAgentDisplayable(Agent agent, int x, int y) {
        super(agent, x, y);
    }

    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval(getBounds().x, getBounds().y, height, width);
        g.setColor(Color.BLACK);
        g.drawOval(getBounds().x, getBounds().y, height, width);
    }

}
