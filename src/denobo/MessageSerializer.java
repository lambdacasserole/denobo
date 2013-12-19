package denobo;

/**
 *
 * @author Alex Mullen
 */
public class MessageSerializer {
    
    public static String serialize(Message message) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.getRecipients().length; i++) {
            sb.append(i > 0 ? ";" : "").append(message.getRecipients()[i]);
        }
        return "id=" + message.getId() + "&from=" + message.getFrom() + "&to=" + sb.toString() + "&msg=" + message.getData();
    }

    public static Message deserialize(String string) {
        String id = null, from = null, message = null;
        String[] to = null;
                
        final String[] pairSplitter = string.split("&");
        for (String str : pairSplitter) {
            final String[] nameValueSplitter = str.split("=");
            switch (nameValueSplitter[0]) {
                case "id":
                    id = nameValueSplitter[1];
                    break;
                case "from":
                    from = nameValueSplitter[1];
                    break;
                case "to":
                    to = nameValueSplitter[1].split(";");
                    break;
                case "msg":
                    message = nameValueSplitter[1];
                    break;
                default:
                    // TODO: Handle invalid string parameter
                    break;
            }
        }

        return new Message(id, from, to, message);
    }
}
