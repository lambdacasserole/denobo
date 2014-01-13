package denobo.socket.connection.state;

import denobo.Message;
import denobo.socket.connection.DenoboConnection;
import denobo.socket.connection.Packet;
import denobo.socket.connection.PacketCode;

/**
* Represents an abstract state that a DenoboConnection can be in.
*
* @author Alex Mullen, Saul Johnson
*/
public abstract class DenoboConnectionState {

    protected final DenoboConnection connection;
    
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

}