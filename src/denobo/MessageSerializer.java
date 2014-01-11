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
        final Message newMessage = new Message(splitter[0], Route.deserialize(splitter[1]), splitter[2]);
        return newMessage;
        
    }
    
}
