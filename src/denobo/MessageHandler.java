package denobo;

/**
 * Represents a handler that can be used to listen for messages passed to an 
 * agent.
 * 
 * @author Saul Johnson
 */
public interface MessageHandler {

    /**
     * Called when an agent receives a message. The message might not be addressed
     * to the agent. If it is a intended for agent being observed, this method
     * is called first then messageRecieved will be called after.
     * 
     * @param agent     The agent who intercepted the message.
     * @param message   The message intercepted.
     */
    public void messageIntercepted(Agent agent, Message message);
    
    /**
     * Called when an agent receives a message that is intended for it.
     * 
     * @param agent     The agent who received the message.
     * @param message   The message received.
     */
    public void messageRecieved(Agent agent, Message message);
    
}
