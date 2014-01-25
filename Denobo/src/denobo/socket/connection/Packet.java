package denobo.socket.connection;

/**
 * Represents a packet of data passed between DenoboConnection objects.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Packet {
        
    /**
     * Holds a code representing the type of packet this is.
     */
    private final PacketCode code;
    
    /**
     * Holds the packet payload for this packet.
     */
    private final String body;

    /**
     * Initialises a new instance of a DenoboPacket with a body.
     * 
     * @param code    the code that represents what type of packet it will be
     * @param body    the packet payload
     */
    public Packet(PacketCode code, String body) {
        this.code = code;
        this.body = body;
    }
    
    /**
     * Initialises a new instance of a DenoboPacket without a body.
     * 
     * @param code    the code that represents what type of packet it will be
     */
    public Packet(PacketCode code) {
        this(code, "");
    }
    
    /**
     * Returns the packet code that states what type of Packet it is.
     * 
     * @return      the packet code of this Packet.
     */
    public PacketCode getCode() {
        return code;
    }
    
    /**
     * Returns the body payload of this packet.
     * 
     * @return      the body payload of this Packet.
     */
    public String getBody() {
        return body;
    }
    
}
