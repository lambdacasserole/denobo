package com.sauljohnson.denobo.socket.connection.state.server;

import com.sauljohnson.denobo.Message;
import com.sauljohnson.denobo.QueryString;
import com.sauljohnson.denobo.socket.connection.Credentials;
import com.sauljohnson.denobo.socket.connection.DenoboConnection;
import com.sauljohnson.denobo.socket.connection.Packet;
import com.sauljohnson.denobo.socket.connection.PacketCode;
import com.sauljohnson.denobo.socket.connection.state.AuthenticatedState;
import com.sauljohnson.denobo.socket.connection.state.DenoboConnectionState;

/**
* This represents the state a connection is in when the other end of the
* connection initiated the connection and we need some valid credentials
* before we will let them proceed any further.
* 
* @author Saul Johnson, Alex Mullen, Lee Oliver
*/
public class WaitingForCredentialsState extends DenoboConnectionState {

    /**
     * Initialises a new instance of a state in which the server is waiting for
     * the client to provide credentials.
     * 
     * @param connection    the connection associated with the state
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

               // Bad status code that we weren't expecting.
               connection.disconnect();
       }
   }

}