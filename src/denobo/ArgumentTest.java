/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package denobo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Odyssic
 */
public class ArgumentTest {

    private static HashMap<String, String> nameValueCollection;

    public static void main(String[] args) {

        String message = "me & you";
        String parsableMessage = "to=agent2&message=";

        message = prepareMessage(message);
        parsableMessage = parsableMessage + message;


        nameValueCollection = new HashMap<>();
        ArrayList<String> variables = new ArrayList<>();
        int currentBeginning = 0;
        for (int j = 0; j < parsableMessage.length(); j++) {
            if (parsableMessage.charAt(j) == '&' && (parsableMessage.charAt(j - 1) != '/')) { // (j - 1) IF j IS 0, j - 1 IS -1!!!!
                variables.add(parsableMessage.substring(currentBeginning, j));
                currentBeginning = (j + 1);
            }
        }
        variables.add(parsableMessage.substring(currentBeginning, parsableMessage.length()));
        
        for (String currentVariable : variables) {
            final String[] nameValueSplitter = currentVariable.split("=");
            System.out.println("pair (" + nameValueSplitter[0] + ", " + nameValueSplitter[1] + ")");
            nameValueCollection.put(nameValueSplitter[0], nameValueSplitter[1]);        
        }
    }

    private static String prepareMessage(String message) {
        return message.replace("&", "/&");
    }
}