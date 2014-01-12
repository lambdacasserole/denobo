package denobo;

import denobo.socket.SocketAgent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an entity that will spawn whenever a link between two agents does 
 * not exist anymore.
 * <p>
 * The purpose of this is so that it can crawl the local network and invalidate
 * every entry in every routing table that contained either one of the agents that
 * were involved in the link that no longer exists.
 *
 * @author Alex Mullen
 */
public class Undertaker implements Runnable {

    /**
     * The thread that underlies this Undertaker worker.
     */
    private Thread underlyingThread;
    
    /**
     * The list of Agent's that were disconnected from each other. In most cases
     * this should contain the two Agent's that formed the link.
     */
    private final List<Agent> branches;
    
    /**
     * The name of the first Agent that was apart of the link.
     */
    private final String firstAgentName;
    
    /**
     * The name of the second Agent that was apart of the link. 
     */
    private final String secondAgentName;
    
    /**
     * A Set of agent names that this Undertaker has already visited.
     */
    private final Set<String> visitedAgentNames;
    
    /**
     * A list of SocketAgent's that this Undertaker has visited.
     */
    private final List<SocketAgent> visitedSocketAgents;
    
    
    /**
     * Initializes a new instance of an Undertaker with a list of visited agents
     * that it does not need to visit.
     * 
     * @param branches              A list of Agent's that formed the link
     * @param firstAgentName        The name of the first agent that formed the link
     * @param secondAgentName       the name of the second agent that formed the link
     * @param alreadyVisitedNodes   A set of agent names that don't need to be visited
     */
    public Undertaker(List<Agent> branches, String firstAgentName, String secondAgentName, Set<String> alreadyVisitedNodes) {
        this.branches = branches;
        this.firstAgentName = firstAgentName;
        this.secondAgentName = secondAgentName;
        this.visitedAgentNames = alreadyVisitedNodes;
        this.visitedSocketAgents = new ArrayList<>();
    }
    
    /**
     * Initializes a new instance of an Undertaker that will visit every Agent
     * in the local network.
     * 
     * @param branches              A list of Agent's that formed the link
     * @param firstAgentName        The name of the first agent that formed the link
     * @param secondAgentName       the name of the second agent that formed the link
    */    
    public Undertaker(List<Agent> branches, String firstAgentName, String secondAgentName) {
        this(branches, firstAgentName, secondAgentName, new HashSet<String>());
    }
    
    
    
    /**
     * Recursive method to crawl through every Agent locally.
     * 
     * @param agent the current Agent this Undertaker has crawled to
     */
    private void undertake(Agent agent) {
        
        /*
         * Remember that we have now visited this agent so that we don't visit
         * it again.
         */
        visitedAgentNames.add(agent.getName());     
            
        /*
         * Remember any SocketAgent's we will visit later but make sure not to
         * remember it if it was one of the branches of the link.
         */
        if (agent instanceof SocketAgent) {
            visitedSocketAgents.add((SocketAgent) agent);
        }

        
        // For each agent connected to the current agent
        final List<Agent> connections = agent.getConnectedAgents();
        for (Agent currentAgent : connections) {
            
            // Make sure we don't visit an already visited agent again
            if (visitedAgentNames.contains(currentAgent.getName())) {
                continue;
            }
            
            // Invalidate the agents in the current agent's routing table
            currentAgent.invalidateAgentName(firstAgentName);
            currentAgent.invalidateAgentName(secondAgentName);

            // Continue crawling the network
            undertake(currentAgent);
            
        }
        
    }
    
    @Override
    public void run() {
        
        // Go through each branch and invalidate the link that was removed.
        for (Agent currentBranch : branches) {
            undertake(currentBranch);
        }
        
        /*
         * Go through any found SocketAgent's and tell their connections to
         * crawl their local networks and invalidate the link between the
         * specified two agents.
         */
        for (SocketAgent currentSocketAgent : visitedSocketAgents) {
            currentSocketAgent.invalidateRemote(firstAgentName, secondAgentName, visitedAgentNames);
        }
        
    }
    
    /**
     * Performs this task on another thread.
     */
    public void undertakeAsync() {
        underlyingThread = new Thread(this);
        underlyingThread.start();
    }

}
