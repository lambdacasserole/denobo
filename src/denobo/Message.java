package denobo;

/**
 *
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {

    private final String[] recipients;
    private final String from;
    private final String message;
    private final String id;

    public Message(String id, String from, String[] recipients, String message) {

        this.id = id;
        this.recipients = recipients;
        this.from = from;
        this.message = message;

    }
     
    public Message(String from, String[] recipients, String message) {

        this(UniqueIdFactory.getId(), from, recipients, message);
        
    }
    
    public Message(String id, String from, String to, String message) {
        this(id, from, new String[] {to}, message);
    }
    
    public Message(String from, String to, String message) {
        this(from, new String[] {to}, message);
    }

    public String getId() {
        return id;
    }

    public String[] getRecipients() {
        return recipients;
    }
    
    public boolean hasRecipient(Actor actor) {
        for(int i = 0; i < recipients.length; i++) {
            if(actor.getName().equals(recipients[i])) { return true; }
        }
        return false;
    }
    
    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }
}
