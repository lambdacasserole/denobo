package denobo;

/**
 * A synchronized decorator class for a MessageHistory instance.
 * 
 * @author Saul Johnson
 */
public class SynchronizedMessageHistory extends MessageHistory {
    
    /**
     * The MessageHistory instance we are synchronizing access to.
     */
    private final MessageHistory messageHistory;

    /**
     * Creates a SynchronizedMessageHistory for providing synchronized access
     * to a MessageHistory instance.
     * 
     * @param messageHistory    The instance to provide synchronized access to.
     */
    public SynchronizedMessageHistory(MessageHistory messageHistory) {
        this.messageHistory = messageHistory;
    }
    
    @Override
    public synchronized void update(String id) {
        messageHistory.update(id);
    }
    
    @Override
    public synchronized boolean hasMessage(Message message) {
        return messageHistory.hasMessage(message);
    }
}
