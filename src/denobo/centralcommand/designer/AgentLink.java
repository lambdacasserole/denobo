package denobo.centralcommand.designer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a visual link between two AgentDisplayable instances.
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
    
    /**
     * Determines whether this AgentLink contains an agent.
     * 
     * @param agent     The agent to check whether it is contained in the link.
     * @return          true if the agent is contained within the link, otherwise
     *                  false is returned.
     */
    public boolean contains(AgentDisplayable agent) {
        
        return (this.agent1 == agent || this.agent2 == agent);
        
    }
    
    /**
     * Determines whether 2 agents are contained in this AgentLink.
     * 
     * @param agent1        The first agent.
     * @param agent2        The second agent.
     * @return              true if both agents are contained within the link,
     *                      otherwise false is returned.
     */
    public boolean contains(AgentDisplayable agent1, AgentDisplayable agent2) {
        
        return (this.agent1 == agent1 || this.agent2 == agent1) 
                &&
               (this.agent1 == agent2 || this.agent2 == agent2);
        
    }
    
    public void breakLink() {
        agent1.getAgent().disconnectActor(agent2.getAgent());
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
