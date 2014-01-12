package denobo;

import denobo.socket.SocketAgent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex Mullen
 */
public class TheUndertaker implements Runnable {

    private Thread underlyingThread;
    
    private final List<Agent> branches;
    private final String firstAgentName;
    private final String secondAgentName;
    private final List<String> visitedAgentNames;
    private final List<SocketAgent> visitedSocketAgents;
    
    
    public TheUndertaker(List<Agent> branches, String firstAgentName, String secondAgentName, List<String> alreadyVisitedNodes) {
        this.branches = branches;
        this.firstAgentName = firstAgentName;
        this.secondAgentName = secondAgentName;
        visitedAgentNames = alreadyVisitedNodes;
        visitedSocketAgents = new ArrayList<>();
    }
    
    public TheUndertaker(List<Agent> branches, String firstAgentName, String secondAgentName) {
        this(branches, firstAgentName, secondAgentName, new ArrayList<String>());
    }
    
    
    
    
    public void undertake(Agent agent) {
        
        if (agent instanceof SocketAgent) {
            if (!branches.contains(agent)) {
                visitedSocketAgents.add((SocketAgent) agent);
            }
        }
        
        System.out.println("Undertaking: " + agent.getName());
        
        final List<Agent> connections = agent.getConnectedAgents();
        for (Agent currentAgent : connections) {
            
            if (visitedAgentNames.contains(currentAgent.getName())) {
                continue;
            }
                         
            currentAgent.invalidateAgentName(firstAgentName);
            currentAgent.invalidateAgentName(secondAgentName);
            
            visitedAgentNames.add(currentAgent.getName());
            
            undertake(currentAgent);
            
        }
        
    }
    
    @Override
    public void run() {
        
        for (Agent currentBranch : branches) {
            undertake(currentBranch);
        }
        
        for (SocketAgent currentSocketAgent : visitedSocketAgents) {
            currentSocketAgent.invalidateRemote(firstAgentName, secondAgentName, visitedAgentNames);
        }
        
    }
    
    public void undertakeAsync() {
        underlyingThread = new Thread(this);
        underlyingThread.start();
    }

}
