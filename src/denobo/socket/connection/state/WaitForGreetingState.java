package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Credentials;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
* This represents the state a connection is in when the other end of the
* connection initiated the connection. 
* <p>
* This side of the connection needs to wait for a GREETINGS packet from the 
* other end before a session is allowed.
*
* @author Alex Mullen
*/
public class WaitForGreetingState extends DenoboConnectionState {

    public WaitForGreetingState(DenoboConnection connection) {
       super(connection);
    }

    @Override
    public void handleSendMessage(Message message) {

        /* 
         * Don't send messages to this peer until authentication has been 
         * performed.
         */

    }

    @Override
    public void handleReceivedPacket(Packet packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {
            case GREETINGS:
                
                System.out.println(connection.getRemoteAddress()
                    + ":" + connection.getRemotePort() + " has sent us a GREETINGS packet");
                
                Credentials masterCredentials = connection.getParentAgent() 
                       .getConfiguration().getCredentialsHandler().credentialsRequested(connection);
                
                if (masterCredentials != null) {
                    connection.send(new Packet(PacketCode.CREDENTIALS_PLZ));
                    connection.setState(new WaitingForCredentialsState(connection));
                } else {
                    connection.send(new Packet(PacketCode.ACCEPTED));
                    connection.setState(new AuthenticatedState(connection));
                }
                break;

            default:

                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
        }
    }
    
}
