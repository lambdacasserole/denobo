package denobo;

/**
 * Represents a history of message identifiers that have passed through a node.
 * 
 * @author Saul Johnson
 */
public class MessageHistory {
    
    private final String[] history;
    private int i = 0;
    //private final HashSet<String> history;
    
    /**
     * Creates a new MessageHistory instance.
     */
    public MessageHistory() {
        history = new String[256];
        //history = new HashSet<>();
    }
    
    /**
     * Updates the history to include the specified id.
     * @param id    The id to add to the history.
     */
    public void update(String id) {
        //history.add(id);
        i++;
        i %= history.length;
        history[i] = id;
        if (i == history.length) {
            i = 0;
        }
    }
    
    public boolean hasMessage(Message message) {
        //return history.contains(message.getId());
        for (int c = 0; c < history.length; c++) {
            if (message.getId().equals(history[c])) {
                return true;
            }
        }
        return false;
    }
    
}
