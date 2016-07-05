package com.sauljohnson.denobo.socket.connection.state.server;

import com.sauljohnson.denobo.socket.connection.DenoboConnection;
import com.sauljohnson.denobo.socket.connection.Packet;
import com.sauljohnson.denobo.socket.connection.PacketCode;
import com.sauljohnson.denobo.socket.connection.state.DenoboConnectionState;

/**
* This represents the state a connection in when a peer has connected to us 
* but them connecting to us has resulting in us exceeding our maximum peer limit 
* so we'll gracefully accept their connection request and tell them that we've 
* reached the peer limit.
*
* @author   Saul Johnson, Alex Mullen, Lee Oliver
*/
public class TooManyPeersState extends DenoboConnectionState {

    /**
     * Initialises a new instance of a "Too Many Peers" connection state.
     * 
     * @param connection    the parent connection to this state
     */
    public TooManyPeersState(DenoboConnection connection) {
        super(connection);
    }
    
    @Override
    public void handleConnectionEstablished() {

        connection.send(new Packet(PacketCode.TOO_MANY_PEERS));
        connection.disconnect();

    }
}
