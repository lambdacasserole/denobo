package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnectionState;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
 * This represents the state a connection is in when the other end of the
 * connection initiated the connection. This side of the connection needs to wait
 * for a GREETINGS packet from the other end before a session is allowed.
 *
 * @author Alex Mullen
 */
public class WaitForGreetingState extends DenoboConnectionState {

    @Override
    public void handleSendMessage(DenoboConnection connection, Message message) {
        
        // Don't send messages to this peer until authentication has been performed
        
    }
    
    @Override
    public void handleReceivedPacket(DenoboConnection connection, Packet packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {

            case GREETINGS:
                
                System.out.println("sending a ACCEPTED packet to " + connection.getRemoteAddress()
                        + ":" + connection.getRemotePort());
                
                // We'll just accept everyone who greets us for now
                sendPacket(connection, new Packet(PacketCode.ACCEPTED));
                connection.setState(new AuthenticatedState());
                break;
            
            default:
                
                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
                break;
        }
    }
}
