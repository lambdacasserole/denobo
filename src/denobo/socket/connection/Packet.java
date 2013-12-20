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
    
    
    public PacketCode getCode() {
        return code;
    }
    
    public String getBody() {
        return body;
    }
    
}
