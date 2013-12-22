package denobo.socket.connection.state;

import denobo.socket.connection.DenoboConnectionState;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
 * This represents the state a connection in when a peer has connected to us but
 * them connection to us has resulting in us exceeding our maximum peer limit so
 * we'll kindly accept their connection request and tell them that we we've
 * reached the peer limit.
 *
 * @author Alex Mullen
 */
public class TooManyPeersState extends DenoboConnectionState {

    @Override
    public void handleConnectionEstablished(DenoboConnection connection) {

        sendPacket(connection, new Packet(PacketCode.TOO_MANY_PEERS));
        connection.disconnect();
    }
}
