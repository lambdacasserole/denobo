package denobo.socket.connection.state;

import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.DenoboConnectionObserver;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
 * This represents the state a connection in when we have have initialized a
 * connection to another peer. It is our responsibility to send a GREETINGS
 * packet to them to initiate a session.
 *
 * @author Alex Mullen
 */
public class GreetingState extends DenoboConnectionState {

    @Override
    public void handleConnectionEstablished(DenoboConnection connection) {
        
        System.out.println("sending a GREETINGS packet to " + connection.getRemoteAddress()
                            + ":" + connection.getRemotePort());
        
        connection.send(new Packet(PacketCode.GREETINGS));
        
    }
    
    @Override
    public void handleReceivedPacket(DenoboConnection connection, Packet packet) {

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
                //connection.send(new Packet(PacketCode.CREDENTIALS, "username=foo&password=bar"));
                break;

                // All these mean we need to disconnect anyway so just let them
                // fall through to the default.
                
            case TOO_MANY_PEERS:  
                
                System.out.println(connection.getRemoteAddress()
                    + ":" + connection.getRemotePort() + " has declined our connection"
                        + " request because it has reached its peer limit.");
                break;
                
            case NO:
            case NOT_A_SERVER:
            default:
                
                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
                break;
        }
    }
}
