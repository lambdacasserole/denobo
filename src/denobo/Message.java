package denobo;

import java.util.HashSet;

/**
 * 
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {
       
    private String to;
    private String from;
    private String message;
    private HashSet<Integer> visitedPortals;
    
    public Message(String from, String to, String message) {
        
        this.to = to;
        this.from = from;
        this.message = message;
        visitedPortals = new HashSet<Integer>();
    }
    
    public Message(String messageString) {
        
        String[] pairSplitter = messageString.split("&");
        for(String str : pairSplitter) {
            String[] nameValueSplitter = str.split("=");
            if(nameValueSplitter[0].equals("to")) {
                to = nameValueSplitter[1];
            } else if(nameValueSplitter[1].equals("from")) {
                from = nameValueSplitter[1];
            } else if(nameValueSplitter[1].equals("msg")) {
                message = nameValueSplitter[1];
            }
        }
        
    }
    
    public String getTo() {
        return to;
    }
    
    public String getFrom() {
        return from;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void addVisited(int id) {
        visitedPortals.add(id);
    }
    
    public boolean hasVisited(int id) {
        return visitedPortals.contains(id);
    }
    
    @Override
    public String toString() {
        return "to=" + getTo() + "&from=" + getFrom() + "&msg=" + getMessage();
    }
    
}
