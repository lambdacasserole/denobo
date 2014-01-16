package denobo.socket.connection;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum that represents a {@link Packet} code that describes the type of 
 * Packet it is.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public enum PacketCode {

    /**
     * Sent by the local peer to the remote peer to begin the session.
     */
    GREETINGS           (100),
    
    /**
     * Sent by the remote peer to the local peer to signal the end of
     * handshaking/initialisation and enter the live session.
     */
    ACCEPTED            (101),
    
    /**
     * Sent by the remote peer to request authentication credentials from the
     * local peer.
     */
    CREDENTIALS_PLZ     (102),
    
    /**
     * Sent by the local peer in response to a 102 (CREDENTIALS_PLZ) code to
     * offer up its username and password for authentication.
     */
    CREDENTIALS         (103),
    
    /**
     * Sent by the remote peer during handshaking/initialisation to set the
     * compression scheme on the connection.
     */
    SET_COMPRESSION     (200),
    
    /**
     * Sent by the remote peer during handshaking/initialisation to specify
     * that the connection should be encrypted.
     */
    BEGIN_SECURE        (201),
    
    /**
     * Sent by a peer to transmit a message over the connection.
     */
    SEND_MESSAGE        (300),
    
    /**
     * Sent by a peer to test the health of a remote agent/connection.
     */
    POKE                (301),
    
    /**
     * Sent by a peer to request a route to a remote agent.
     */
    ROUTE_TO            (302),
    
    /**
     * Sent by a peer in reply to a 302 (ROUTE_TO) packet to pass back a route
     * to an agent once calculated.
     */
    ROUTE_FOUND         (303),
    
    /**
     * Sent by a peer to specify that a set of agents are no longer valid in
     * routing tables.
     */
    INVALIDATE_AGENTS   (304),
    
    /**
     * A generic error code that can be sent by either peer.
     */
    NO                  (400),
    
    /**
     * Sent by the remote peer, specifying that the agent has reached its peer
     * limit before disconnecting.
     */
    TOO_MANY_PEERS      (401),
    
    /**
     * Sent by the local peer in response to a 102 (CREDENTIALS_PLZ) packet to
     * specify that we don't have any credentials to provide.
     */
    NO_CREDENTIALS      (402),
    
    /**
     * Sent by the remote peer in response to a 103 (CREDENTIALS) packet
     * indicating that the credentials provided were not acceptable.
     */
    BAD_CREDENTIALS     (403);

    /**
     * The actual code number of this PacketCode.
     */
    private final int code;

    /**
     * A static Map that will contain every PacketCode pairing with the code
     * number as the key so that we can efficiently retrieve a PacketCode enum
     * from a code number.
     */
    private final static Map<Integer, PacketCode> CODE_MAP;
    
    static {
        CODE_MAP = new HashMap<>();
        for (PacketCode currentCode : PacketCode.values()) {
            CODE_MAP.put(currentCode.toInt(), currentCode);
        }
    }
    
    /**
     * Instantiates a new enum with the given code.
     * 
     * @param code the code that will represent this enum
     */
    private PacketCode(int code) {
        this.code = code;
    }

    /**
     * Returns the number code that represents this enum.
     * 
     * @return the number code
     */
    public int toInt() {
        return code;
    }
    
    /**
     * Converts a number code into its PacketCode representation.
     * 
     * @param code  the code
     * @return      the PacketCode representation or null if it is not a valid
     *              code
     */
    public static PacketCode valueOf(int code) {
        return CODE_MAP.get(code);
    }
    
}
