package denobo;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 *
 * @author Saul Johnson
 */
public class QueryString {

    private HashMap<String, String> map;
    
    public QueryString(String string) {
        
        Objects.requireNonNull(string, "Query string cannot be null.");
        
        // Parse query string.
        final String[] entries = string.split("&");
        for (String current : entries) {
            final String[] nameValue = current.split("=");
            if (nameValue.length == 2) {
                map.put(nameValue[0], nameValue[1]);
            }
        }
        
    }
    
    public QueryString() {
        this("");
    }
    
    public String get(String key) {
        return map.get(key);
    }
    
    public void add(String key, String value) {
        map.put(key, value);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
}
