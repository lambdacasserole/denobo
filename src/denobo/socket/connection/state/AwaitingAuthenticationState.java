package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;

/**
 * This represents the state where the peer who initiated the connection has
 * submitted some credentials as a reply to a 102 (CREDENTIALS_PLZ) packet and
 * is waiting for authentication.
 * 
 * @author Alex Mullen, Saul Johnson
 */
public class AwaitingAuthenticationState extends DenoboConnectionState {

    /**
     * Initialises a new instance of an awaiting authenticated connection state.
     * 
     * @param connection    the parent connection to this state
     */
    public AwaitingAuthenticationState(DenoboConnection connection) {
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
       switch (packet.getCode()) {
           case ACCEPTED:

               System.out.println("The credentials we sent were accepted.");
               
               connection.setRemoteAgentName(packet.getBody());
               connection.setState(new AuthenticatedState(connection));
               break;

           case BAD_CREDENTIALS:
               
               /*
                * Bad credentials, fall through.
                */
               
               System.out.println("We sent some bad credentials.");

           case NO:

               /**
                * Unspecified error, fall through.
                */
               
           default:

               connection.disconnect();
               break;
       }
   }

}