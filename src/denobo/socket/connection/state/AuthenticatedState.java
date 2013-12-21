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

    @Override
    public void handleReceivedPacket(DenoboConnection connection, Packet packet) {

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
                break;
        }
    }
}
