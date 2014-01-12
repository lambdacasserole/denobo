package denobo;

/**
 * Helper class to handle the serialisation/deserialisation of {@link Message} 
 * instances.
 * 
 * @author Alex Mullen, Saul Johnson
 */
public class MessageSerializer {
    
    public static String serialize(Message message) {
        
        System.out.println(message.getId() + "," + message.getRoute().serialize() 
                + "," + message.getData());
        return message.getId() + "," + message.getRoute().serialize() 
                + "," + message.getData();
        
    }

    public static Message deserialize(String string) {

        final String[] splitter = string.split(",");
        
        Message newMessage = null;
        
        /*
         * Check how many strings we split into, a length of 2 means we were sent
         * a blank message so we to handle that to avoid an exception.
         */ 
        if (splitter.length == 2) {
            newMessage = new Message(splitter[0], Route.deserialize(splitter[1]), null);
        } else if (splitter.length == 3) {
            newMessage = new Message(splitter[0], Route.deserialize(splitter[1]), splitter[2]);
        } else {
            // TODO: Handle this
        }

        return newMessage;
        
    }
    
}
