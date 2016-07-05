package com.sauljohnson.denobo.socket.connection.state.client;

import com.sauljohnson.denobo.Message;
import com.sauljohnson.denobo.QueryString;
import com.sauljohnson.denobo.socket.connection.DenoboConnection;
import com.sauljohnson.denobo.socket.connection.Packet;
import com.sauljohnson.denobo.socket.connection.state.AuthenticatedState;
import com.sauljohnson.denobo.socket.connection.state.DenoboConnectionState;

/**
 * Represents a connection state in which the peer who initiated the connection is waiting for authentication.
 *
 * @version 1.0 05 July 2016
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class AwaitingAuthenticationState extends DenoboConnectionState {

    /**
     * Initialises a new instance of an "Awaiting Authentication" connection state.
     * @param connection    the parent connection to this state
     */
    public AwaitingAuthenticationState(DenoboConnection connection) {
       super(connection);
    }

    @Override
    public void handleSendMessage(Message message) {
        // Don't send messages to this peer until authentication has been performed.
    }

    @Override
    public void handleReceivedPacket(Packet packet) {

        //
        switch (packet.getCode()) {
            case ACCEPTED:
                System.out.println("The credentials we sent were accepted.");
                final QueryString acceptedQueryString = new QueryString(packet.getBody());
                connection.setRemoteAgentName(acceptedQueryString.get("name"));
                connection.setState(new AuthenticatedState(connection));
                break;
            case BAD_CREDENTIALS:
                // Bad credentials, fall through.
                System.out.println("We sent some bad credentials.");
            case NO:
                // Unspecified error, fall through.
            default:
                connection.disconnect();
                break;
        }
    }
}
