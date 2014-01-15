package denobo.socket.connection.state.server;

import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import denobo.socket.connection.state.DenoboConnectionState;

/**
* This represents the state a connection in when a peer has connected to us 
* but them connecting to us has resulting in us exceeding our maximum peer limit 
* so we'll gracefully accept their connection request and tell them that we've 
* reached the peer limit.
*
* @author Saul Johnson, Alex Mullen, Lee Oliver
*/
public class TooManyPeersState extends DenoboConnectionState {

    public TooManyPeersState(DenoboConnection connection) {
        super(connection);
    }
    
    @Override
    public void handleConnectionEstablished() {

        connection.send(new Packet(PacketCode.TOO_MANY_PEERS));
        connection.disconnect();

    }
}
