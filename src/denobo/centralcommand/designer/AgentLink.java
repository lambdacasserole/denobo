package denobo.centralcommand.designer;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Alex Mullen
 */
public class AgentLink {
    
    public final AgentDisplayable agent1;
    public final AgentDisplayable agent2;
    private final Color lineColour = Color.BLACK;

    public AgentLink(AgentDisplayable agent1, AgentDisplayable agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        agent1.getAgent().connectActor(agent2.getAgent());
    }
    
    public void draw(Graphics g) {
        
        final int sourceX = (agent1.getBounds().x + (agent1.getBounds().width / 2));
        final int sourceY = (agent1.getBounds().y + (agent1.getBounds().height / 2));
        
        final int destX = (agent2.getBounds().x + (agent2.getBounds().width / 2));
        final int destY = (agent2.getBounds().y + (agent2.getBounds().height / 2));
        
        g.setColor(lineColour);
        g.drawLine(sourceX, sourceY, destX, destY);
        
    }
    
}
