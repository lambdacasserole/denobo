package denobo.socket.connection.state;

import denobo.Message;
import denobo.MessageSerializer;
import denobo.QueryString;
import denobo.RoutingQueue;
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
 * @author Alex Mullen
 */
public class AuthenticatedState extends DenoboConnectionState {
    
    /**
     * Initialises a new instance of an authenticated connection state.
     * 
     * @param connection    the parent connection to this state
     */
    public AuthenticatedState(DenoboConnection connection) {
        
        super(connection);
        for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
            currentObserver.connectionAuthenticated(connection);
        }
        
    }

    @Override
    public void handleReceivedPacket(Packet packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {

            case PROPAGATE:

                final Message deserializedMessage = MessageSerializer.deserialize(packet.getBody());
                for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
                    currentObserver.messageReceived(connection, deserializedMessage); 
                }
                break;
                
            case ROUTE_TO:
                
                final RoutingWorker worker = new RoutingWorker(this.connection.getParentAgent(), packet.getBody());
                worker.addRoutingWorkerListener(new RoutingWorkerListener() {
                    @Override
                    public void routeCalculationSucceeded(String destinationAgentName, RoutingQueue route) {

                        connection.send(new Packet(PacketCode.ROUTE_FOUND, destinationAgentName));
                        
                    }

                    @Override
                    public void routeCalculationFailed(String destinationAgentName) {
                        
                        connection.send(new Packet(PacketCode.ROUTE_NOT_FOUND, destinationAgentName));
                        
                    }
                });
                worker.mapRouteAsync();
                
            case ROUTE_FOUND:
                
                final List<RoutingWorkerListener> listeners = 
                        this.connection.getParentAgent().remoteDestinationFoundCallbacks.get(packet.getBody());
                
                for (RoutingWorkerListener currentListener : listeners) {
                    currentListener.routeCalculationSucceeded(packet.getBody(), null);
                }
                
            case ROUTE_NOT_FOUND:
                
                
                
            default:

                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();

        }
    }
}    
