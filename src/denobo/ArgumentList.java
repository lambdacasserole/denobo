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
    public static String firstMessage;
    public static String secondMessage;
    public static String fullMessage = "";

    /**
     * Initialises a new instance of an argument list from the given message
     * string.
     *
     * @param message the message string from which to initialise the list
     */
    public ArgumentList(String message) {

        String message2 = message.replace("&", "/&");

        for (int i = 0; i < message2.length(); i++) {
            if (message2.charAt(i) == '/') {
                if (message2.charAt(i + 1) == '&') {
                    firstMessage = message2.substring(0, i);
                    secondMessage = message2.substring(i + 2);
                    break;
                }
            }
        }
        String actualMessage = fullMessage.concat(firstMessage + "&" + secondMessage);

        nameValueCollection = new HashMap<>();
        int currentBeginning = 0;
        for (int j = 0; j < actualMessage.length(); j++) {
            if (j > 1) {
                if (actualMessage.charAt(j) == '&') {
                    if (actualMessage.charAt(j - 1) != '/') {   // (j - 1) IF j IS 0, j - 1 IS -1!!!!
                        final String subPair = message.substring(currentBeginning, j - 1);
                        currentBeginning = (j + 1);

                        final String[] nameValueSplitter = subPair.split("=");
                        nameValueCollection.put(nameValueSplitter[0], nameValueSplitter[1]);
                    }
                }
            }
        }

    }

    private void prepareMessage(String message) {
        message.replace("&", "/&");
    }

    /**
     * Gets the value of the argument with the specified name.
     *
     * @param key the name of the argument
     * @return the value of the argument with the specified name
     */
    public String getValue(String key) {
        return nameValueCollection.get(key);
    }

    /**
     * Removes a parameter key-value pair from this argument list.
     *
     * @param key the key name of the argument to remove
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
