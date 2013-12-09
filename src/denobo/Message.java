package denobo;

/**
 * 
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {
       
    private String to;
    private String from;
    private String message;
    
    public Message(String from, String to, String message) {
        
        this.to = to;
        this.from = from;
        this.message = message;
        
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
    
}
