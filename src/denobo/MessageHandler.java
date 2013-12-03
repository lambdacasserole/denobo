package denobo;

/**
 *
 * @author Saul
 */
public interface MessageHandler {

    /**
     * 
     * @param to
     * @param from
     * @param message 
     */
    public void messageRecieved(String to, String from, String message);
    
}
