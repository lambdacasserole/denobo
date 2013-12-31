package denobo.centralcommand.designer;

import denobo.Agent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents a Agent that can be displayed and moved in the designer.
 *
 * @author Alex Mullen
 */
public class AgentDisplayable {
    
    public static final int height = 75;
    public static final int width = 75;
    
    private final Rectangle bounds;
    private final Agent agent;
    
    
    public AgentDisplayable(Agent agent, int x, int y) {
        this.agent = agent;
        bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
    
    public Agent getAgent() {
        return agent;
    }
    
    public void draw(Graphics g) {
        g.setColor(new Color(38, 127, 0));
        g.fillOval(bounds.x, bounds.y, height, width);
        g.setColor(Color.BLACK);
        g.drawOval(bounds.x, bounds.y, height, width);
    }
    
}
