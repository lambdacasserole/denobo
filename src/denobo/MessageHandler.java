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
     * 
     * @param agent     The agent who received the message.
     * @param message   The message received.
     */
    public void messageRecieved(Agent agent, Message message);
    
}
