package denobo.socket.connection.state;

import denobo.Message;
import denobo.MessageSerializer;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.Packet;

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

            default:

                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();

        }
    }
}    
