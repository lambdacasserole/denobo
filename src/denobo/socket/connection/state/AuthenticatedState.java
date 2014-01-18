package denobo.socket.connection.state;

import denobo.Agent;
import denobo.Message;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * This represents the state of a connection has completed the hand-shake.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AuthenticatedState extends DenoboConnectionState implements RoutingWorkerListener {
    
    /**
     * The lock object that we use for waiting for a poke reply and notifying
     * when we get the reply.
     */
    private final Object pokeLock;
    
    /**
     * Indicates whether this connection has sent a poke packet and we are 
     * expecting a poke packet back.
     */
    private boolean pokeSent;
    
    /**
     * An indicator to the poke method that this connection received a poke 
     * packet back.
     */
    private boolean pokeReturned;

    
    /**
     * Initialises a new instance of an authenticated connection state.
     * 
     * @param connection    the parent connection to this state
     */
    public AuthenticatedState(DenoboConnection connection) {
        
        super(connection);
        
        pokeLock = new Object();
        
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
                final Message deserializedMessage = Message.deserialize(packet.getBody());
                for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
                    currentObserver.messageReceived(connection, deserializedMessage); 
                }
                break;
                
            case ROUTE_TO:
                                
                // Parse query string passed to us.
                queryString = new QueryString(packet.getBody());
                
                /* 
                 * Extract local route and destination name from query string to 
                 * continue building the route on this side of the connection.
                 */
                final Route localRoute = 
                        Route.deserialize(queryString.get("localroute"));                
                final String destinationName = queryString.get("to");
                final boolean usesBacktracking = queryString.getAsBoolean("backtracks");
                
                /*
                 * If we are the agent in question, we don't need to spawn a
                 * routing worker at all.
                 */
                if (connection.getParentAgent().getName().equals(destinationName)) {
                    localRoute.append(connection.getParentAgent().getName());
                    this.routeCalculationSucceeded(destinationName, localRoute);
                    return;
                }
                
                // Route to destination agent.
                final RoutingWorker worker = new RoutingWorker(this.connection.getParentAgent(), 
                        destinationName, localRoute, usesBacktracking);
                worker.addRoutingWorkerListener(this);
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
                
                // Parse query string passed through.
                queryString = new QueryString(packet.getBody());
                
                // Get the set of agents that have already been visited.
                final Set<String> visitedAgents = queryString.getAsSet("visitedagents");
                
                // Get the list of invalidated agent names
                final List<String> invalidatedAgents = queryString.getAsList("invalidatedagents");
                
                /*
                 * Add this SocketAgent instance as a branch for the Undertaker
                 * to crawl along.
                 */
                final ArrayList<Agent> branches = new ArrayList<>(1);
                branches.add(connection.getParentAgent());
                
                /* 
                 * Start an asyncronous Undertaker instance to update our local 
                 * network.
                 */
                final Undertaker undertaker = 
                        new Undertaker(branches, invalidatedAgents, visitedAgents);
                undertaker.undertakeAsync();
                
                break;
                
            case POKE:

                // Send poke code synchronously.
                synchronized (pokeLock) {
                    if (pokeSent) {
                        pokeReturned = true;
                        pokeLock.notify();
                    } else {
                        connection.send(new Packet(PacketCode.POKE));
                    }
                }
                break;
                
            default:

                // Bad packet code that we weren't expecting.
                connection.disconnect();
                break;
                
        }
        
    }

    @Override
    public long handleSendPoke(long timeout) throws TimeoutException {
        
        synchronized (pokeLock) {
            
            // Save the current time
            final long startTime = System.currentTimeMillis();
            
            // Send a poke packet
            pokeSent = true;
            connection.send(new Packet(PacketCode.POKE));

            // Check if we have recieved a reply poke
            while (!pokeReturned) {
                /*
                 * Check if we have waited the elapsed time or longer with still
                 * no reply.
                 */
                if ((System.currentTimeMillis() - startTime) >= timeout) {
                    throw new TimeoutException();
                }

                // Go to sleep until we are notified of a reply
                try {
                    pokeLock.wait(timeout);
                } catch (InterruptedException ex) { 
                    System.out.println(ex.getMessage());
                }
            }
            
            // Reset variables
            pokeSent = false;
            pokeReturned = false;
            
            // Return how long the process took
            return (System.currentTimeMillis() - startTime);
            
        }
        
    }
    
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
                    
}    
