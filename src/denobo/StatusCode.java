
package denobo;

/**
 * 
 * @author Lee Oliver
 */
public enum StatusCode {

    SEND(100),
    ERROR(503),
    ANOTHER(300); // TODO: rename

    private int code;
    
    private StatusCode(int code) {
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
}
