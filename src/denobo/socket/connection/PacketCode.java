package denobo.socket.connection;

import java.util.HashMap;
import java.util.Map;

/**
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
    
    NO                  (400),
    TOO_MANY_PEERS      (401),
    NOT_A_SERVER        (402),     // Redundant?
    NO_CREDENTIALS      (403),
    BAD_CREDENTIALS     (404),
    UNSUPPORTED         (405);

    
    private final int code;
    private final static Map<Integer, PacketCode> codeLookup;
    
    static {
        codeLookup = new HashMap<>();
        for (PacketCode currentCode : PacketCode.values()) {
            codeLookup.put(currentCode.toInt(), currentCode);
        }
    }
    
    private PacketCode(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }
    
    public static PacketCode valueOf(int code) {
        return codeLookup.get(code);
    }
}
