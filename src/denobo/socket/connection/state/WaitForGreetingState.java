package denobo.socket.connection.state;

import denobo.Message;
import denobo.QueryString;
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
                        + ":" + connection.getRemotePort() 
                        + " has sent us a 100 (GREETINGS) packet.");
                          
                /* 
                 * The 100 (GREETINGS) packet contains the name of the agent 
                 * connecting.
                 */
                
                final QueryString greetingsQueryString = new QueryString(packet.getBody());
                connection.setRemoteAgentName(greetingsQueryString.get("name"));
                
                /*
                 * Check if we have some credentials set that we require from
                 * the remote SocketAgent before we can authenticate them.
                 */
                final Credentials masterCredentials = connection.getParentAgent() 
                       .getConfiguration().getCredentials();
                
                if (masterCredentials == null) {
                    
                    final QueryString acceptedQueryString = new QueryString();
                    acceptedQueryString.add("name", connection.getParentAgent().getName());
                    
                    connection.send(new Packet(PacketCode.ACCEPTED, acceptedQueryString.toString()));
                    connection.setState(new AuthenticatedState(connection));
                    
                } else {
                    
                    connection.send(new Packet(PacketCode.CREDENTIALS_PLZ));
                    connection.setState(new WaitingForCredentialsState(connection));
                    
                }
                break;

            default:

                // TODO: Bad status code that we weren't expecting.
                connection.disconnect();
        }
    }
    
}
