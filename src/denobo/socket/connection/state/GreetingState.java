package denobo.socket.connection.state;

import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.DenoboPacket;
import denobo.socket.connection.PacketCode;

/**
 *
 * @author Alex Mullen
 */
public class GreetingState extends DenoboConnectionState {

    @Override
    public void handleConnectionEstablished(DenoboConnection connection) {
        
        System.out.println("sending a GREETINGS packet to " + connection.getRemoteAddress()
                            + ":" + connection.getRemotePort());
        
        connection.send(new DenoboPacket(PacketCode.GREETINGS));
        
    }
    
    @Override
    public void handleReceivedPacket(DenoboConnection connection, DenoboPacket packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {

            case ACCEPTED:
                
                System.out.println(connection.getRemoteAddress()
                    + ":" + connection.getRemotePort() + " has accepted our "
                        + "GREETINGS request");
                
                for (DenoboConnectionObserver currentObserver : connection.getObservers()) {
                    currentObserver.connectionAuthenticated(connection);
                }
                connection.setState(new AuthenticatedState());
                break;
                
            case CREDENTIALS_PLZ:
                
                // TODO: Implement credentials
                //connection.send(new DenoboPacket(PacketCode.CREDENTIALS, "username=foo&password=bar"));
                break;

                // All these mean we need to disconnect anyway so just let them
                // fall through to the default.
                
            case NO:
            case TOO_MANY_PEERS:
            case NOT_A_SERVER:
            default:
                
                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
                break;
        }
    }
}
