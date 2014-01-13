package denobo.socket.connection.state;

import denobo.Message;
import denobo.QueryString;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Credentials;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
* This represents the state a connection is in when we have have initialised a
* connection to another peer. 
* <p>
* It is our responsibility to send a GREETINGS packet to them to initiate  a 
* session.
*
* @author Alex Mullen
*/
public class InitiateGreetingState extends DenoboConnectionState {

    public InitiateGreetingState(DenoboConnection connection) {
        super(connection);
    }
        
    @Override
    public void handleConnectionEstablished() {

       System.out.println("Sending a 100 (GREETINGS) packet to - ["
               + connection.getRemoteAddress() + ":" 
               + connection.getRemotePort() + "]");
       
       final QueryString queryString = new QueryString();
       queryString.add("name", connection.getParentAgent().getName());
       
       connection.send(new Packet(PacketCode.GREETINGS, queryString.toString()));

   }

    @Override
    public void handleSendMessage(Message message) {

        /*
         * Don't send messages to this peer until handshake has been performed.
         */
        
    }

    @Override
    public void handleReceivedPacket(Packet packet) {

        // Process packet according to status code.
        switch(packet.getCode()) {
            case ACCEPTED:

                System.out.println(connection.getRemoteAddress()
                        + ":" + connection.getRemotePort() + " has accepted our "
                        + "GREETINGS request.");
                
                /* 
                 * The 101 (ACCEPTED) packet contains the name of the agent 
                 * we connected to.
                 */

                final QueryString acceptedQueryString = new QueryString(packet.getBody());
                connection.setRemoteAgentName(acceptedQueryString.get("name"));
                
                connection.setState(new AuthenticatedState(connection));
                break;

            case CREDENTIALS_PLZ:

                System.out.println(connection.getRemoteAddress()
                    + ":" + connection.getRemotePort() + " is asking for credentials...");

                // Ask/retrieve the credentials to use
                final Credentials credentials = 
                        connection.getParentAgent().getConfiguration().getCredentialsHandler().credentialsRequested(connection);
                
                if (credentials == null) {
                    System.out.println("We have no credentials to send, disconnecting.");
                    connection.send(new Packet(PacketCode.NO_CREDENTIALS));
                    connection.disconnect();
                } else {
                    System.out.println("Sending our credentials: " + credentials.toString());
                    connection.send(new Packet(PacketCode.CREDENTIALS, credentials.toString()));                    
                    connection.setState(new AwaitingAuthenticationState(connection));
                }

                break;

                /*
                 * All these mean we need to disconnect anyway so just let them
                 * fall through to the default.
                 */

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

        }

    }

}