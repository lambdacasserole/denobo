package denobo.socket.connection.state;

import denobo.Message;
import denobo.QueryString;
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
                       .getConfiguration().getMasterCredentials();
               
               // Get credentials submitted.
               final Credentials submittedCredentials = Credentials.parse(packet.getBody());
               System.out.println("Server got credentials: " + submittedCredentials.toString());
               
               // Check username/password.
               if (Credentials.validate(masterCredentials, submittedCredentials)) {
                   
                   final QueryString acceptedQueryString = new QueryString();
                   acceptedQueryString.add("name", connection.getParentAgent().getName());
                   
                   connection.send(new Packet(PacketCode.ACCEPTED, acceptedQueryString.toString()));
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