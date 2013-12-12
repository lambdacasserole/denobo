package denobo;

/**
 * Represents a history of message identifiers that have passed through a node.
 * 
 * @author Saul Johnson
 */
public class MessageHistory {
    
    private String[] history;
    private int i = 0;
    
    public MessageHistory() {
        history = new String[256];
    }
    
    /**
     * Updates th
     * @param id 
     */
    public void update(String id) {
        i++;
        i %= 256;
        history[i] = id;
        if (i == history.length) {
            i = 0;
        }
    }
    
    public boolean hasMessage(Message message) {
        for (int c = 0; c < history.length; c++) {
            if(message.getId().equals(history[c])) {
                return true;
            }
        }
        return false;
    }
    
}
