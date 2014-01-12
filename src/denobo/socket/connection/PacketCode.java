package denobo.socket.connection;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum that represents a {@link Packet} code that describes the type of 
 * Packet it is.
 * 
 * @author Lee Oliver, Alex Mullen
 */
public enum PacketCode {

    GREETINGS           (100),
    ACCEPTED            (101),
    CREDENTIALS_PLZ     (102),
    CREDENTIALS         (103),
    
    CHANGE_CONFIRMED    (200),
    SET_COMPRESSION     (201),
    BEGIN_SECURE        (202),
    CONFIRM_SECURE      (203),
    END_SECURE          (204),
    
    PROPAGATE           (300),
    POKE                (301),
    ROUTE_TO            (302),
    ROUTE_FOUND         (303),
    ROUTE_NOT_FOUND     (304),
    INVALIDATE_AGENTS   (305),
    
    NO                  (400),
    TOO_MANY_PEERS      (401),
    NOT_A_SERVER        (402),     // Redundant?
    NO_CREDENTIALS      (403),
    BAD_CREDENTIALS     (404),
    UNSUPPORTED         (405);

    /**
     * The actual code number of this PacketCode.
     */
    private final int code;

    /**
     * A static Map that will contain every PacketCode pairing with the code
     * number as the key so that we can efficiently retrieve a PacketCode enum
     * from a code number.
     */
    private final static Map<Integer, PacketCode> codeMap;
    
    static {
        codeMap = new HashMap<>();
        for (PacketCode currentCode : PacketCode.values()) {
            codeMap.put(currentCode.toInt(), currentCode);
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
        return codeMap.get(code);
    }
}
