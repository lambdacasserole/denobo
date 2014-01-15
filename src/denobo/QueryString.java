package denobo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a query string.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class QueryString {

    /**
     * The underlying key-value map.
     */
    private HashMap<String, String> map;
    
    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of a query string.
     * 
     * @param string    the string from which to initialise the query string
     */
    public QueryString(String string) {
        
        Objects.requireNonNull(string, "Query string cannot be null.");
       
        map = new HashMap<>();
        
        // Parse query string.
        final String[] entries = string.split("&");
        for (String current : entries) {
            final String[] nameValue = current.split("=");
            if (nameValue.length == 2) {
                map.put(nameValue[0], nameValue[1]);
            }
        }
        
    }
    
    /**
     * Initialises a new instance of a query string.
     */
    public QueryString() {
        this("");
    }
    
    
    /* ---------- */
    
    
    /**
     * Returns true if the character is safe for inclusion in a query string.
     * 
     * @param c the character to check
     * @return  true if the character is safe, otherwise false
     */
    private static boolean isSafeCharacter(char c) {
        return (c > 64 && c < 91) 
                || (c > 96 && c < 123) 
                || (c == 95)
                || (c > 47 && c < 58);
    }
    
    /**
     * Encodes a string in HTTP URL format.
     * 
     * @param str   the string to encode
     * @return      the string encoded in HTTP URL format
     */
    public static String htmlEncode(String str) {
        if (str == null) { return null; }
        final StringBuilder sb = new StringBuilder();
        for (char current : str.toCharArray()) {
            if (!isSafeCharacter(current)) {
                String hexCode = Integer.toHexString(current);
                if (hexCode.length() == 1) {
                    hexCode = "0" + hexCode;
                }
                sb.append("%").append(hexCode);
            } else {
                sb.append(current);
            }
        }
        return sb.toString();
    }
    
    /**
     * Decodes a string from HTTP URL format.
     * 
     * @param str   the string to be decoded
     * @return      the string decoded from HTTP URL format
     */
    public static String htmlDecode(String str) {
        if (str == null) { return null; }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '%' && i < str.length() - 2) {
                final String hexCode = str.substring(i + 1, i + 3);
                final char decodedChar = (char) Integer.parseInt(hexCode, 16);
                sb.append(decodedChar);
                i += 2;
            } else {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }
    
    
    /* ---------- */
    
    
    /**
     * Gets the value associated with the specified key.
     * 
     * @param key   the key for which to get the associated value
     * @return      the value associated with the specified key
     */
    public String get(String key) {
        return htmlDecode(map.get(key));
    }
    
    /**
     * Adds a key-value pair to the query string.
     * 
     * @param key   the key part of the new entry
     * @param value the value part of the new entry
     */
    public void add(String key, String value) {
        map.put(key, htmlEncode(value));
    }
    
    /**
     * Adds a key-value pair to the query string where the value is a 
     * collection.
     * 
     * @param key   the key part of the new entry
     * @param value the value part of the new entry
     */
    public void addAsCollection(String key, Collection<String> value) {
        final StringBuilder vals = new StringBuilder();
        for (String str : value) {
            vals.append(htmlEncode(str)).append(";");
        }
        vals.deleteCharAt(vals.length() - 1);
        map.put(key, htmlEncode(vals.toString()));
    }
    
    /**
     * Gets the value associated with the specified key as a list.
     * 
     * @param key   the key for which to get the associated value
     * @return      the value associated with the specified key as a list
     */
    public List<String> getAsList(String key) {
        final List<String> list = new ArrayList<>();
        putInCollection(key, list);
        return list;
    }
    
    /**
     * Gets the value associated with the specified key as a set.
     * 
     * @param key   the key for which to get the associated value
     * @return      the value associated with the specified key as a set
     */
    public Set<String> getAsSet(String key) {
        final Set set = new HashSet<>();
        putInCollection(key, set);
        return set;
    }
    
    /**
     * Gets the value associated with the specified key and puts it in the 
     * specified Collection.
     * 
     * @param key           the key for which to get the associated value
     * @param collection    the collection in which to put the value associated
     *                      with the specified key
     */
    public void putInCollection(String key, Collection<String> collection) {
        final String listString = htmlDecode(map.get(key));
        for (String current : listString.split(";")) {
            collection.add(htmlDecode(current));
        }
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
