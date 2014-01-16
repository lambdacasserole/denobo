package denobo;

/**
 * An adapter class that allows implementing code to implement a MessageListener
 * without having to be forced to implement all of its methods. 
 * <p>
 * This makes anonymous classes much cleaner to use.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class MessageHandler implements MessageListener {

    @Override
    public void messageIntercepted(Agent agent, Message message) {
        /*
         * Default behaviour is to do nothing.
         */
    }

    @Override
    public void messageRecieved(Agent agent, Message message) {
        /*
         * Default behaviour is to do nothing.
         */
    }

}
