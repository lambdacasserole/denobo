package denobo;

/**
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
    
    public boolean hasMessage(String id) {
        for (int c = 0; c < history.length; c++) {
            if(history[c] != null && history[c].equals(id)) {
                return true;
            }
        }
        return false;
    }
    
}
