package denobo;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Represents a list of arguments (formatted as a query string) passed as a 
 * message between agents.
 * 
 * @author Saul
 */
public class ArgumentList {

    /**
     * Holds a name-value map of argument names and values.
     */
    private final HashMap<String, String> nameValueCollection;
    
    /**
     * Initialises a new instance of an argument list from the given message 
     * string.
     * 
     * @param message   the message string from which to initialise the list
     */
    public ArgumentList(String message) {
        
        // Split into arguments.
        nameValueCollection = new HashMap<>();
        final String[] pairSplitter = message.split("&");
        for (String pair : pairSplitter) {
            final String[] nameValueSplitter = pair.split("=");
            nameValueCollection.put(nameValueSplitter[0], 
                    nameValueSplitter[1]);
        }
        
    }
    
    /**
     * Gets the value of the argument with the specified name.
     * 
     * @param key   the name of the argument
     * @return      the value of the argument with the specified name
     */
    public String getValue(String key) { 
        return nameValueCollection.get(key);
    }
    
    /**
     * Removes a parameter key-value pair from this argument list.
     * 
     * @param key   the key name of the argument to remove
     */
    public void removeParam(String key) {
        nameValueCollection.remove(key);
    }
    
    @Override
    public String toString() {
        
        // Rebuild list as message string.
        final StringBuilder sb = new StringBuilder();
        for (Entry<String, String> p : nameValueCollection.entrySet()) {
            sb.append(sb.length() == 0 ? "" : "&").append(p.getKey()).append("=").append(p.getValue());
        }
        return sb.toString();
                
    }
    
}
