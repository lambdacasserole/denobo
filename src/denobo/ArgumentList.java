package denobo;

import java.util.HashMap;

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
    private HashMap<String, String> nameValueCollection;
    
    /**
     * Initialises a new instance of an argument list from the given message 
     * string.
     * 
     * @param message   the message string from which to initialise the list
     */
    public ArgumentList(String message) {
        
        // Split into arguments.
        nameValueCollection = new HashMap<>();
        String[] pairSplitter = message.split("&");
        
        // Split name-value pairs.
        for (int i = 0; i < pairSplitter.length; i++) {
            String[] nameValueSplitter = pairSplitter[i].split("=");
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
    
}
