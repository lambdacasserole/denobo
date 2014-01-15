package denobo;

/**
 * Represents a handler that can be used to listen for Messages passed to an 
 * agent.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface MessageHandler {

    /**
     * Called when an Agent receives a Message. 
     * <p>
     * The Message might not be addressed to the Agent. If it is a intended for 
     * Agent being observed, this method is called first then 
     * {@link #messageRecieved} will be called after.
     * 
     * @param agent     the Agent who intercepted the Message
     * @param message   the Message intercepted
     */
    public void messageIntercepted(Agent agent, Message message);
    
    /**
     * Called when an Agent receives a Message that is intended for it.
     * 
     * @param agent     the Agent who received the Message
     * @param message   the Message received
     */
    public void messageRecieved(Agent agent, Message message);
    
}
