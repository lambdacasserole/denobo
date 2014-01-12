package denobo.socket.connection.state;

import denobo.Agent;
import denobo.Message;
import denobo.MessageSerializer;
import denobo.QueryString;
import denobo.Route;
import denobo.RoutingWorker;
import denobo.RoutingWorkerListener;
import denobo.Undertaker;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This represents the state of a connection has completed the hand-shake.
 *
 * @author Alex Mullen, Saul Johnson
 */
public class AuthenticatedState extends DenoboConnectionState {
    
    /**
     * Initialises a new instance of an authenticated connection state.
     * 
     * @param connection    the parent connection to this state
     */
    public AuthenticatedState(DenoboConnection connection) {
        
        super(connection);
        
        // Let observers know we've entered an authenticated state.
        for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
            currentObserver.connectionAuthenticated(connection);
        }
        
    }

    @Override
    public void handleReceivedPacket(Packet packet) {

        QueryString queryString;
                
        // Process packet according to status code.
        switch(packet.getCode()) {
            
            case SEND_MESSAGE:

                // Pass message on to observers.
                final Message deserializedMessage = MessageSerializer.deserialize(packet.getBody());
                for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
                    currentObserver.messageReceived(connection, deserializedMessage); 
                }
                break;
                
            case ROUTE_TO:
                                
                // Parse query string passed to us.
                queryString = new QueryString(packet.getBody());
                
                /* 
                 * Extract local route from query string to continue building 
                 * the route on this side of the connection.
                 */
                final Route localRoute = 
                        Route.deserialize(queryString.get("localroute"));
                
                // Route to destination agent.
                final RoutingWorker worker = new RoutingWorker(this.connection.getParentAgent(), queryString.get("to"), localRoute);
                worker.addRoutingWorkerListener(new RoutingWorkerListener() {
                    @Override
                    public void routeCalculationSucceeded(String destinationAgentName, Route route) {

                        /* 
                         * Pass back a 303 (ROUTE_FOUND) packet containing our
                         * calculated route.
                         */
                        final QueryString queryString = new QueryString();
                        queryString.add("to", destinationAgentName);
                        queryString.add("route", route.serialize());
                        connection.send(new Packet(PacketCode.ROUTE_FOUND, queryString.toString()));
                        
                    }
                });
                worker.mapRouteAsync();
                break;
                
            case ROUTE_FOUND:
                
                // Parse query string passed back.
                queryString = new QueryString(packet.getBody());
                
                // Get routing listeners for the agent we just got a route to.
                final String destinationAgent = queryString.get("to");
                final List<RoutingWorkerListener> listeners = 
                        connection.getParentAgent().remoteRouteToCallbacks.get(destinationAgent);
                
                // Call back on those listeners. Route calculation success.
                final Route queue = Route.deserialize(queryString.get("route"));
                for (RoutingWorkerListener currentListener : listeners) {
                    currentListener.routeCalculationSucceeded(destinationAgent, queue);
                }
                break;
                
            case INVALIDATE_AGENTS:
                
                // Parse query string passed back.
                queryString = new QueryString(packet.getBody());
                
                // Get the names of the agents to invalidate
                final String agent1Name = queryString.get("agent1");
                final String agent2Name = queryString.get("agent2");
                
                // Get the set of agents that have already been visited.
                final String combinedVisitedAgents = queryString.get("visitedagents");
                
                final Set<String> visitedAgentsNames = new HashSet<>();
                
                
                // Check if there was no previously visited agents
                if (combinedVisitedAgents != null) {
                    final String[] splitVisitedAgents = combinedVisitedAgents.split(";");
                    visitedAgentsNames.addAll(Arrays.asList(splitVisitedAgents));                 
                }
                
                /*
                 * Add this SocketAgent instance as a branch for the Undertaker
                 * to crawl along.
                 */
                final ArrayList<Agent> branches = new ArrayList<>(1);
                branches.add(connection.getParentAgent());
                
                // Start an asyncronous Undertaker instance to update our local network
                final Undertaker undertaker = new Undertaker(branches, agent1Name, agent2Name, visitedAgentsNames);
                undertaker.undertakeAsync();
                
                break;
                
            case ROUTE_NOT_FOUND:
                
                // TODO: Can we make efficiency savings with this packet code?
                break;
                
            default:

                // TODO: Bad packet code that we weren't expecting.
                connection.disconnect();
                break;
        }
    }
}    
