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

    public static String serialize(Message msg) {
        return "from=" + msg.from + "&to=" + msg.to + "&msg=" + msg.message;
    }

    public static Message deserialize(String rawMessage) {

        String to = null, from = null, message = null;
                
        final String[] pairSplitter = rawMessage.split("&");
        for (String str : pairSplitter) {
            final String[] nameValueSplitter = str.split("=");
            if (nameValueSplitter[0].equals("to")) {
                to = nameValueSplitter[1];
            } else if (nameValueSplitter[0].equals("from")) {
                from = nameValueSplitter[1];
            } else if (nameValueSplitter[0].equals("msg")) {
                message = nameValueSplitter[1];
            } else {
                // invalid name string
            }
        }

        return new Message(from, to, message);
    }
}
