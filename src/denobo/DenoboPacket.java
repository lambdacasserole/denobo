package denobo;

import java.io.Serializable;

/**
 *
 * @author Saul
 */
public class DenoboPacket implements Serializable {
    
    private static final String version = "DENOBO v0.9 (BENSON)";
    
    private int statusCode;
    private String body;

    public DenoboPacket(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }
    
    public String getVersion() {
        return version;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getBody() {
        return body;
    }
    
}
