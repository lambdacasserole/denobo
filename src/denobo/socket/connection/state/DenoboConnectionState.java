package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;
import java.util.concurrent.TimeoutException;

/**
* Represents an abstract state that a DenoboConnection can be in.
*
* @author   Saul Johnson, Alex Mullen, Lee Oliver
*/
public abstract class DenoboConnectionState {

    /**
     * The connection this state is associated with.
     */
    protected final DenoboConnection connection;
    
    /**
     * Abstract constructor for a state that a {@link DenoboConnection} can be 
     * in.
     * 
     * @param connection    the connection this state is associated with
     */
    public DenoboConnectionState(DenoboConnection connection) {
        this.connection = connection;
    }
    
   /**
    * Handles the moments after the connection has been established and
    * communication can begin.
    */
   public void handleConnectionEstablished() {

   }

   /**
    * Handles a received packet from a connection.
    * 
    * @param packet the packet that was received
    */
   public void handleReceivedPacket(Packet packet) {

   }

   /**
    * Handles a request to send a message to this connected peer. 
    * <p>
    * It is useful to override this to prevent messages been sent until 
    * authentication has occurred.
    * 
    * @param message the message to send
    */
   public void handleSendMessage(Message message) {

       connection.send(new Packet(PacketCode.SEND_MESSAGE, message.serialize()));

   }
   /**
    * Sends a POKE packet to the remote peer connected to this connection which 
    * should reply with one back.
    * <p>
    * This process is measured to give an indicator to how healthy the connection
    * to this peer is. The lower the number, the faster packets get there and
    * the faster they are getting processed.
    * 
    * @param timeout   the maximum time to wait for a reply in milliseconds.
    * @return          the total round time it taken for us to send a packet
    *                  and receive one back in milliseconds or -1 could be
    *                  returned if the current state does not allow a POKE to be
    *                  sent.
    * @throws TimeoutException     if we did not receive a reply before the 
    *                              specified timeout.
    */
   public long handleSendPoke(long timeout) throws TimeoutException {
       
       // By default, return -1 to indicate no poke was sent.
       return -1;
       
   }

}