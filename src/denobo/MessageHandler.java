package denobo;

/**
 * Represents a handler that can be used to listen for messages passed to an 
 * agent.
 * 
 * @author Saul Johnson
 */
public interface MessageHandler {

    /**
     * Called when the agent receives a message.
     * @param to        the name of the agent that this message is addressed to
     * @param from      the name of the agent that originated this message
     * @param message   the message itself
     */
    public void messageRecieved(String to, String from, String message);
    
}
