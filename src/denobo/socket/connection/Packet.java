package denobo.socket.connection;

/**
 * Represents a packet of data passed between DenoboConnection objects.
 * 
 * @author Saul Johnson, Alex Mullen
 */
public class Packet {
        
    /**
     * Holds a code representing the type of packet.
     */
    private final PacketCode code;
    
    /**
     * Holds the packet payload.
     */
    private final String body;

    /**
     * Initialises a new instance of a DenoboPacket with a body.
     * 
     * @param code    the code contained within the packet
     * @param body    the packet payload.
     */
    public Packet(PacketCode code, String body) {
        
        this.code = code;
        this.body = body;
        
    }
    
    /**
     * Initialises a new instance of a DenoboPacket without a body.
     * 
     * @param code    the code contained within the packet
     */
    public Packet(PacketCode code) {
        this(code, "");
    }
    
    /**
     * Returns the packet code that states what type of Packet it is.
     * 
     * @return      the packet code of the packet.
     */
    public PacketCode getCode() {
        return code;
    }
    
    /**
     * Returns the body payload of this packet.
     * 
     * @return      the body payload of this packet.
     */
    public String getBody() {
        return body;
    }
    
}
