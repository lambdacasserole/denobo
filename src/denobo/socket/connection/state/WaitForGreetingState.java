package denobo.socket.connection.state;

import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboPacket;
import denobo.socket.connection.PacketCode;

/**
 *
 * @author Alex Mullen
 */
public class WaitForGreetingState extends DenoboConnectionState {

    @Override
    public void handleReceivedPacket(DenoboConnection connection, DenoboPacket packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {

            case GREETINGS:
                
                // We'll just accept everyone who greets us for now
                connection.send(new DenoboPacket(PacketCode.ACCEPTED));
                connection.setState(new AuthenticatedState());
                break;
            
            default:
                
                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
                break;
        }
    }
}
