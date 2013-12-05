package denobo;

import java.util.List;

/**
 *
 * @author Saul
 */
public class UserAgent extends MetaAgent {
   
    private List<MessageHandler> handlers;

    public UserAgent(String name, boolean cloneable) {
        super(name, cloneable);
    }
    
    @Override
    public void handleMessage(String message) {
        
        final ArgumentList args = new ArgumentList(message);
                
        final String to = args.getValue("to");
        final String from = args.getValue("from");
        
        args.removeParam("to");
        args.removeParam("from");
        
        for (MessageHandler handler : handlers) {
            handler.messageRecieved(to, from, args.toString());
        }
        
    }
    
}
