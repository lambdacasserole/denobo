package denobo;

/**
 *
 * @author Saul Johnson, Lee Oliver, Alex Mullen
 */
public class Message {

    private final String to;
    private final String from;
    private final String message;
    private final String id;

    public Message(String from, String to, String message) {

        this.to = to;
        this.from = from;
        this.message = message;
        this.id = UniqueIdFactory.getId();

    }

    public String getId() {
        return id;
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
