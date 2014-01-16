package denobo.socket.connection.state.server;

import denobo.Agent;
import denobo.Message;
import denobo.QueryString;
import denobo.crypto.DiffieHellmanKeyGenerator;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Credentials;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import denobo.socket.connection.state.AuthenticatedState;
import denobo.socket.connection.state.DenoboConnectionState;
import java.math.BigInteger;

/**
* This represents the state a connection is in when the other end of the
* connection initiated the connection. 
* <p>
* This side of the connection needs to wait for a GREETINGS packet from the 
* other end before a session is allowed.
*
* @author Saul Johnson, Alex Mullen, Lee Oliver
*/
public class WaitForGreetingState extends DenoboConnectionState {

    /**
     * Initialises a new instance of a "Wait for Greeting" connection state.
     * 
     * @param connection    the parent connection to this state
     */
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
                final String remoteName = greetingsQueryString.get("name");
                if (!Agent.isValidName(remoteName)) {
                    connection.send(new Packet(PacketCode.NO, "Your name doesn't look valid to me."));
                    connection.disconnect();
                    return;
                }
                connection.setRemoteAgentName(remoteName);
                
                /*
                 * Set the compression on the connecting agent and afterwards
                 * on ourselves. Everything up to this point has been
                 * uncompressed.
                 */
                final QueryString setCompressionString = new QueryString();
                setCompressionString.add("name", connection.getParentAgent().getConfiguration().getCompression().getName());
                connection.send(new Packet(PacketCode.SET_COMPRESSION, setCompressionString.toString()));
                connection.setCompressor(connection.getParentAgent().getConfiguration().getCompression());
        
                /*
                 * The public key of the connecting agent is also included in 
                 * the packet. If we're required to be secure, transmit our 
                 * public key to the connecting agent.
                 */
                final BigInteger remotePublicKey = new BigInteger(greetingsQueryString.get("pubkey"));
                if (connection.getParentAgent().getConfiguration().getIsSecure()) {
                    final QueryString replyString = new QueryString();
                    replyString.add("pubkey", connection.getPublicKey().toString());
                    connection.send(new Packet(PacketCode.BEGIN_SECURE, replyString.toString()));
                    connection.setSharedKey(DiffieHellmanKeyGenerator.generateSharedKey(remotePublicKey, connection.getPrivateKey()));
                    System.out.println("Reciever computed shared key: " + connection.getSharedKey().toString());
                }
                
                /*
                 * Check if we have some credentials set that we require from
                 * the remote SocketAgent before we can authenticate them.
                 */
                final Credentials masterCredentials = connection.getParentAgent() 
                       .getConfiguration().getMasterCredentials();
                if (masterCredentials != null) {
                    System.out.println("Server expects credentials: " 
                            + masterCredentials.toString());
                }
                
                /* 
                 * If we have no master credentials, send back an ACCEPTED packet
                 * right away.
                 */
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
