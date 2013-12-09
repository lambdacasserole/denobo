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
     * @param message   the message received
     */
    public void messageRecieved(Message message);
    
}
