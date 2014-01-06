package denobo;

/**
 * Represents a history of Message identifiers that have passed through a node.
 * 
 * @author Saul Johnson
 */
public class MessageHistory {
    
    private final String[] history;
    private int i = 0;
    
    /**
     * Initialises a new instance of MessageHistory.
     */
    public MessageHistory() {
        history = new String[256];
    }
    
    /**
     * Updates the history to include the specified ID.
     * 
     * @param id    the ID to add to the history.
     */
    public void update(String id) {
        i = (i + 1) % history.length;
        history[i] = id;
    }
    
    public void update(Message message) {
        update(message.getId());
    }
    
    public boolean hasMessage(Message message) {
        for (int c = 0; c < history.length; c++) {
            if (message.getId().equals(history[c])) {
                return true;
            }
        }
        return false;
    }
    
}
