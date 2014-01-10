package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Credentials;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
* This represents the state a connection is in when the other end of the
* connection initiated the connection and we need some valid credentials
* before we will let them proceed any further.
* 
* @author Alex Mullen
*/
public class WaitingForCredentialsState extends DenoboConnectionState {

    /**
     * 
     * @param connection 
     */
    public WaitingForCredentialsState(DenoboConnection connection) {
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
           case CREDENTIALS:

               // Get master credentials.
               final Credentials masterCredentials = connection.getParentAgent()
                       .getConfiguration().getCredentialsHandler().credentialsRequested(connection);
               
               // Get credentuials submitted.
               final Credentials submittedCredentials = Credentials.parse(packet.getBody());
               
               // Check username/password.
               if (Credentials.validate(masterCredentials, submittedCredentials)) {
                   connection.send(new Packet(PacketCode.ACCEPTED));
                   connection.setState(new AuthenticatedState(connection));
               } else {
                   connection.send(new Packet(PacketCode.BAD_CREDENTIALS));
                   connection.disconnect();
               }
               break;

           default:

               // TODO: Bad status code that we weren't expecting.
               connection.disconnect();
       }
   }

}