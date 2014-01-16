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

    GREETINGS           (100),
    ACCEPTED            (101),
    CREDENTIALS_PLZ     (102),
    CREDENTIALS         (103),
    
    SET_COMPRESSION     (200),
    BEGIN_SECURE        (201),
    
    SEND_MESSAGE        (300),
    POKE                (301),
    ROUTE_TO            (302),
    ROUTE_FOUND         (303),
    INVALIDATE_AGENTS   (304),
    
    NO                  (400),
    TOO_MANY_PEERS      (401),
    NO_CREDENTIALS      (402),
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
