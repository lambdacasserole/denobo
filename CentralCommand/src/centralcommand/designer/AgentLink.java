package centralcommand.designer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a visual link between two AgentDisplayable instances.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AgentLink {
    
    /**
     * The first agent that forms this link.
     */
    public final AgentDisplayable agent1;
    
    /**
     * The second agent that forms this link.
     */
    public final AgentDisplayable agent2;
    
    /**
     * The colour of the line that visually represents the link.
     */
    private static final Color LINE_COLOUR = Color.BLACK;

    /**
     * Instantiates a new AgentLink between two agents.
     * 
     * @param agent1    the first agent in the link
     * @param agent2    the second agent in the link
     */
    public AgentLink(AgentDisplayable agent1, AgentDisplayable agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        agent1.getAgent().connectAgent(agent2.getAgent());
    }
    
    /**
     * Determines whether this AgentLink contains an agent.
     * 
     * @param agent the agent to check whether it is contained in the link
     * @return      true if the agent is contained within the link, 
     *              otherwise false
     */
    public boolean contains(AgentDisplayable agent) {
        return (this.agent1 == agent || this.agent2 == agent);
    }
    
    /**
     * Determines whether 2 agents are contained in this AgentLink.
     * 
     * @param agent1    the first agent
     * @param agent2    the second agent
     * @return          true if both agents are contained within the link,
     *                  otherwise false
     */
    public boolean contains(AgentDisplayable agent1, AgentDisplayable agent2) {
        return (contains(agent1) && contains(agent2));
    }
    
    /**
     * Breaks the link between the two agents that form this link.
     */
    public void breakLink() {
        agent1.getAgent().disconnectAgent(agent2.getAgent());
    }
    
    /**
     * Renders this link to the specified graphics context.
     * 
     * @param g the graphics context
     */
    public void draw(Graphics g) {
        
        final int sourceX = (agent1.getBounds().x + (agent1.getBounds().width / 2));
        final int sourceY = (agent1.getBounds().y + (agent1.getBounds().height / 2));
        
        final int destX = (agent2.getBounds().x + (agent2.getBounds().width / 2));
        final int destY = (agent2.getBounds().y + (agent2.getBounds().height / 2));
        
        g.setColor(LINE_COLOUR);
        g.drawLine(sourceX, sourceY, destX, destY);
        
    }
    
}
