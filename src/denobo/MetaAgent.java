package denobo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Saul
 */
public abstract class MetaAgent implements Runnable {
   
    /**
     * The blocking queue that underlies this object.
     */
    private BlockingQueue<String> messageQueue;
    
    /**
     * The message processing thread that underlies this object.
     */
    private Thread underlyingThread;
    
    /**
     * Holds the name of the agent.
     */
    private String name;
    
    /**
     * Holds whether or not the agent is cloneable.
     */
    private boolean cloneable;
    
    /**
     * Abstract constructor to initialise a new instance of a meta-agent.
     * 
     * @param name      the name of the agent
     * @param cloneable whether or not the agent is cloneable
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public MetaAgent(String name, boolean cloneable) {
        
        this.name = name;
        this.cloneable = cloneable;
        
        messageQueue = new LinkedBlockingQueue<>();
        underlyingThread = new Thread(this);
        
    }
    
    
    /**
     * Abstract constructor to initialise a new instance of a non-cloneable meta-agent.
     * 
     * @param name  the name of the agent
     */
    public MetaAgent(String name) {
        this(name, false);
    }
    
    /**
     * Gets the name of the agent.
     * 
     * @return  the name of the agent
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets whether or not the agent is cloneable.
     * 
     * @return  a boolean value indicating whether or not the agent is cloneable
     */
    public boolean getCloneable() {
       return cloneable; 
    }
        
    /**
     * Adds the given message to this agent's message queue.
     * 
     * @param message   the message to add
     */
    public void queueMessage(String message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException ex) {
            System.out.println("Thread was interrupted during message enqueue.");
        }
    }
    
    @Override
    public void run() {
        
        // Loop until interrupted.
        while (true) {
            try {
                final String message = messageQueue.take();
                if (cloneable) {
                    
                    // Handle message in new thread.
                    new Thread() {
                        @Override
                        public void run() {
                            handleMessage(message);
                        }
                    }.start();
                    
                } else {
                    
                    // Handle message in this thread.
                    handleMessage(message);
                    
                }
            } catch (InterruptedException ex) {
                System.out.println("Thread was interrupted during message de-queue.");
            }
        }
                
    }
    
    /**
     * Handles messages passed to this agent through its message queue.
     * 
     * @param message   the message to handle
     */
    public abstract void handleMessage(String message);
    
}
