package denobo.centralcommand.designer;

import denobo.centralcommand.designer.dialogs.AgentDebugWindow;
import denobo.Agent;
import denobo.centralcommand.designer.dialogs.monitor.AgentMonitorDialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Represents an Agent that can be displayed and moved in a NetworkDesigner.
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentDisplayable {
    
    /**
     * The image that visually represents an agent.
     */
    private static BufferedImage agentImage;
    
    /**
     * The HEIGHT of the image.
     */
    public static final int HEIGHT = 75;
    
    /**
     * The WIDTH of the image.
     */
    public static final int WIDTH = 75;

    /**
     * A rectangle that represents the image so we can use it for containing
     * it's size and position in the designer.
     */
    protected final Rectangle bounds;
    
    /**
     * The underlying Agent instance that is been visually represented.
     */
    private final Agent agent;
    
    /**
     * The attached debug window for this agent.
     */
    private final AgentDebugWindow debugWindow;
    
    /**
     * The attached monitor window for this agent.
     */
    private final AgentMonitorDialog monitorDialog;
    
    
    
    static {
        /*
         * To save memory, we only need to load the image once since it won't
         * be changing.
         */
        final URL imageUrl = AgentDisplayable.class.getResource("resources/agent.png");
        try {
            agentImage = ImageIO.read(imageUrl);
        } catch (IOException ex) {
            System.err.println("Could not load resources/agent.png: " + ex.getMessage());
        }
    }
    
    /**
     * Initialises a new instance of an AgentDisplayable.
     * 
     * @param agent the underlying agent
     * @param x     the x coordinate position
     * @param y     the y coordinate position
     */
    public AgentDisplayable(Agent agent, int x, int y) {
        this.agent = agent;
        debugWindow = new AgentDebugWindow(agent);
        monitorDialog = new AgentMonitorDialog(agent);
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
    }
    
    /**
     * Returns the AgentDebugWindow instance for this agent.
     * 
     * @return the AgentDebugWindow instance
     */
    public AgentDebugWindow getDebugWindow() {
        return debugWindow;
    }
    
    /**
     * Returns the AgentMonitorDialog instance for this agent.
     * 
     * @return the AgentMonitorDialog instance
     */
    public AgentMonitorDialog getMonitorDialog() {
        return monitorDialog;
    }

    /**
     * Returns the Rectangle instance that represents the size and position of
     * this AgentDisplayable.
     * 
     * @return the Rectangle instance
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Returns the underlying Agent instance this AgentDisplayable is representing.
     * 
     * @return the Agent instance
     */
    public Agent getAgent() {
        return agent;
    }
    
    /**
     * Renders this agent onto the specified Graphics context.
     * 
     * @param g the Graphics context
     */
    public void draw(Graphics g) {
        g.drawImage(agentImage, bounds.x, bounds.y, null);
    }

    @Override
    public String toString() {
        return agent.getName();
    }
}
