package denobo.centralcommand.designer;

import denobo.Agent;
import denobo.centralcommand.designer.dialogs.AgentMonitorDialog;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Represents a Agent that can be displayed and moved in the designer.
 *
 * @author Alex Mullen
 */
public class AgentDisplayable {
    
    private static BufferedImage agentImage;
    protected static BufferedImage socketImage;
    
    public static final int height = 75;
    public static final int width = 75;

    protected final Rectangle bounds;
    
    private final Agent agent;
    private final AgentDebugWindow debugWindow;
    private final AgentMonitorDialog monitorDialog;
    
    public AgentDisplayable(Agent agent, int x, int y) {
        
        this.agent = agent;
        debugWindow = new AgentDebugWindow(agent);
        monitorDialog = new AgentMonitorDialog(agent);
        bounds = new Rectangle(x, y, width, height);
        
        if(agentImage == null) {
            final URL imageUrl = getClass().getResource("resources/agent.png");
            try {
                agentImage = ImageIO.read(imageUrl);
            } catch (IOException ex) {
                System.err.println("Could not load splash screen image.");
            }
        }
        
    }
    
    public AgentDebugWindow getDebugWindow() {
        return debugWindow;
    }
    
    public AgentMonitorDialog getMonitorDialog() {
        return monitorDialog;
    }

    public Rectangle getBounds() {
        return bounds;
    }
     
    public Agent getAgent() {
        return agent;
    }
    
    public void draw(Graphics g) {
//        g.setColor(new Color(38, 127, 0));
//        g.fillOval(bounds.x, bounds.y, height, width);
//        g.setColor(Color.BLACK);
//        g.drawOval(bounds.x, bounds.y, height, width);
        g.drawImage(agentImage, bounds.x, bounds.y, null);
    }

    @Override
    public String toString() {
        return agent.getName();
    }
}
