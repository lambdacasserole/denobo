package denobo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents an abstract agent acting as part of a multi-agent system.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public abstract class MetaAgent implements Runnable {
   
    /**
     * The blocking queue that underlies this object.
     */
    private BlockingQueue<String> messageQueue;

    /**
     * Holds a list of parent portals for this agent.
     */
    protected List<Portal> portals;
    
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
        portals = new ArrayList<>();
        
        underlyingThread = new Thread(this);
        underlyingThread.start();
        
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
     * Registers a portal as being a parent to this agent.
     * 
     * @param portal    the portal to register
     */
    public void registerParentPortal(Portal portal) {
        portals.add(portal);
    }
    
    /**
     * Unregisters a portal as being a parent to this agent.
     * 
     * @param portal    the  portal to unregister
     */
    public void unregisterParentPortal(Portal portal) {
        portals.remove(portal);
    }
    
    /**
     * Handles messages passed to this agent through its message queue.
     * 
     * @param message   the message to handle
     */
    public abstract void handleMessage(String message);
    
    /**
     * Gets whether or not this agent is, or has a route to, the agent with
     * the specified name.
     * 
     * @param name  the name of the agent to seek
     * @return      whether or not this agent has a route to the agent with the specified name
     */
    public abstract boolean hasRouteToAgent(String name);
    
}
