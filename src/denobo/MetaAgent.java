package denobo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents an abstract agent acting as part of a multi-agent system.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public abstract class MetaAgent {
       
    /**
     * The {@link BlockingQueue} queue that underlies this object.
     */
    private BlockingQueue<Message> messageQueue;

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
     * Holds the locally unique identifier for this agent.
     */
    private final String id;
    
    /**
     * The thread pool service for handling cloneable agents.
     */
    private ExecutorService executorService = null;
    
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
        this.id = UniqueIdFactory.getId();
        
        messageQueue = new LinkedBlockingQueue<>();
        portals = new ArrayList<>();
                
        // Only construct the thread pool if we are cloneable
        if (cloneable) {
            executorService = Executors.newCachedThreadPool();
        }
        
        underlyingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                queueProcessLoop();
            }
        });
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
     * Gets the locally unique identifier of the agent.
     * 
     * @return  the locally unique identifier of the agent
     */
    public String getId() {
        return id;   
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
    public void queueMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException ex) {
            System.out.println("Thread was interrupted during message enqueue.");
        }
    }
    
    private void queueProcessLoop() {
        
        // Loop until interrupted.
        while (true) {
            try {
                final Message message = messageQueue.take();
                if (cloneable) {
                    
                    // Execute the handleMessage on a seperate thread
                    executorService.submit(
                        new Runnable() {
                            @Override
                            public void run() {
                                handleMessage(message);
                            }
                        }
                    );

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
    protected abstract void handleMessage(Message message);
    
//    /**
//     * Gets whether or not this agent is, or has a route to, the agent with
//     * the specified name.
//     * 
//     * @param name  the name of the agent to seek
//     * @return      whether or not this agent has a route to the agent with the specified name
//     */
//    public abstract boolean hasRouteToAgent(String name);    
    
}
