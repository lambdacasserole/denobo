package denobo.socket.connection.state;

import denobo.Message;
import denobo.MessageSerializer;
import denobo.QueryString;
import denobo.Route;
import denobo.RoutingWorker;
import denobo.RoutingWorkerListener;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import java.util.List;

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
            case PROPAGATE:

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

                    @Override
                    public void routeCalculationFailed(String destinationAgentName) {
                        
                        /*
                         * Pass back a 304 (ROUTE_NOT_FOUND) packet. This 
                         * currently has no effect on the system.
                         */
                        connection.send(new Packet(PacketCode.ROUTE_NOT_FOUND, destinationAgentName));
                        
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
                        connection.getParentAgent().remoteDestinationFoundCallbacks.get(destinationAgent);
                
                // Call back on those listeners. Route calculation success.
                final Route queue = Route.deserialize(queryString.get("route"));
                for (RoutingWorkerListener currentListener : listeners) {
                    currentListener.routeCalculationSucceeded(destinationAgent, queue);
                }
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
