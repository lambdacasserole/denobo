package denobo;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Represents an abstract actor acting as part of a multi-agent system.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public abstract class Actor {

    /**
     * Holds a list of connected actors.
     */
    private final List<Actor> connectedActors;

    /**
     * The {@link BlockingQueue} that underlies the actor.
     */
    private final BlockingQueue<Message> messageQueue;

    /**
     * The message processing thread that underlies the actor.
     */
    private final Thread underlyingThread;

    /**
     * The name of the actor.
     */
    private final String name;

    /**
     * Whether or not the actor is cloneable.
     */
    private final boolean cloneable;

    /**
     * The thread pool service for handling cloneable actors.
     */
    private final ExecutorService executorService;

    /**
     * Holds the status on whether this Actor has been shutdown and also signals
     * to the underlyingThread to stop processing.
     */
    private volatile boolean shutdown;
    
    /**
     * Holding this lock will hold off a potential shutdown until released. 
     */
    private final Object shutdownLock;
    

    
    /**
     * Abstract constructor to initialise a new instance of an actor.
     *
     * @param name the name of the actor
     * @param cloneable whether or not the actor is cloneable
     */
    public Actor(String name, boolean cloneable) {

        this.name = name;
        this.cloneable = cloneable;

        messageQueue = new LinkedBlockingQueue<>();
        connectedActors = new CopyOnWriteArrayList<>();
        shutdownLock = new Object();

        // Only construct the thread pool if cloneable.
        executorService = (cloneable ? Executors.newCachedThreadPool() : null);

        // Start message processing.
        underlyingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                queueProcessLoop();
            }
        });
        underlyingThread.start();

    }

    /**
     * Abstract constructor to initialise a new instance of a non-cloneable
     * actor.
     *
     * @param name the name of the actor
     */
    public Actor(String name) {
        this(name, false);
    }

    /**
     * Gets the name of the actor.
     *
     * @return the name of the actor
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets whether or not the actor is cloneable.
     *
     * @return true if the actor is cloneable, otherwise false
     */
    public final boolean getCloneable() {
        return cloneable;
    }
    
    /**
     * Gets whether or not the actor has shutdown or is currently in the process
     * of shutting down.
     * 
     * @return true if the actor has shutdown, otherwise false
     */
    public final boolean hasShutdown() {
        return shutdown;
    }

    /**
     * Connects the actor to another.
     *
     * @param actor the actor to connect to
     */
    public void connectActor(Actor actor) {
        // We don't want to connect any Actor's if we are shutting down or are 
        // in the process of shutting down.
        synchronized (shutdownLock) {
            if (shutdown || actor == this) { return; }
            connectedActors.add(actor);
            actor.registerConnectedActor(this);
        }
    }

    /**
     * Disconnects the actor from another.
     *
     * @param actor the actor to disconnect from
     */
    public void disconnectActor(Actor actor) {
        actor.unregisterConnectedActor(this);
        connectedActors.remove(actor);
    }

    /**
     * Registers the actor as connected to another.
     *
     * @param actor the actor to register
     */
    protected void registerConnectedActor(Actor actor) {
        connectedActors.add(actor);
    }

    /**
     * Unregisters the as being a connected to another.
     *
     * @param actor the actor to unregister
     */
    protected void unregisterConnectedActor(Actor actor) {
        connectedActors.remove(actor);
    }

    
    /**
     * Adds a message to the actor's message queue.
     *
     * @param message the message to add
     */
    public void queueMessage(Message message) {
        
        // We need to make sure the thread won't be shutdown between checking
        // if it has shutdown and adding the message to the queue - we want to
        // guarantee that if our message is added, that it will be processed.
        synchronized (shutdownLock) {
            // Don't queue the message if we have shutdown or we are in the process
            // of shutting down.
            if (shutdown) {
                // TODO: I'd rather notify the caller that their message won't be
                // delivered in some way.
                return;
            }

            // Make sure our message gets added to the queue as something interrupting
            // our thread could cause our message to be lost.
            boolean added = false;
            while (!added) {
                try {
                    // Queue message for processing.
                    messageQueue.put(message);
                    added = true; 
                } catch (InterruptedException ex) {
                    // Reset the interrupt flag for this thread for any higher
                    // up the chain caller.
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * The loop that processes this Actor's message queue.
     */
    private void queueProcessLoop() {

        // Keep processing till we are given the signal to stop then process
        // everything until the queue is empty.
        while (!shutdown || !messageQueue.isEmpty()) {
            try {
                final Message message = messageQueue.take();
                System.out.println("[" + this.getName() + " takes a message from its queue]");
                // Check if we should bother handling this message
                if (!shouldHandleMessage(message)) {
                    continue;
                }

                if (!cloneable) {
                    // Handle message in this thread.
                    handleMessage(message);
                } else {
                    // Execute handleMessage on a seperate thread
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            handleMessage(message);
                        }
                    });
                }
            } catch (InterruptedException ex) {
                // Swallow the exception as a chance for us to unblock from the
                // take() call and check whether we need to begin shutting down.
                //System.out.println("Thread interupted, shutting down when finished: " + name);
            }
        }
        
    }
        
    /**
     * Prevents any more messages from being added to the queue and processes
     * any remaining in the queue. The queue is then shutdown and all links to
     * other agents are removed.
     */
    public void shutdown() {
        
        // Set the signal to not accept anymore messages into the queue and for
        // the queue to finish processing any remaining messages. We need the
        // lock in so we can guarantee that everything added to the queue before
        // this point will certainly be processed.
        synchronized (shutdownLock) {
            shutdown = true;
            underlyingThread.interrupt();
        }
        
        try {
            // Wait for the queue thread to finish
            underlyingThread.join();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        
        // Shutdown the the thread pool if we are cloneable and wait till
        // all the threads are finished.
        if (executorService != null) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                // TODO: Handle this
                System.out.println(ex.getMessage());
            }
        }    

        // Remove all links to other Actor's
        for (Actor actor : connectedActors) {
            actor.unregisterConnectedActor(this);
        }
        
        connectedActors.clear();
    }
    
    /**
     * Sends a message to every Actor connected to this Actor.
     *
     * @param message   The Message object to broadcast
     */
    protected void broadcastMessage(Message message) {
        
        // Check if the Message is wrapped in an ActorMessage wrapper. This wrapper
        // holds which agent we received the message from originally so we can
        // avoid broadcasting it back to them.
        if (message instanceof ActorMessage) {
            
            final ActorMessage actorMessage = (ActorMessage) message;
            
            // Unwrap the wrapper to get to the raw Message instance then create
            // a wrapper with us set as the sender. This is more efficent than
            // wrapping another wrapper around a wrapper haha.
            message = new ActorMessage(this, actorMessage.getRawMessage());
            
            for (Actor actor : connectedActors) {
                // Don't send it back to who we received it from
                if (actor != actorMessage.getReceivedFrom()) {
                    actor.queueMessage(message);
                }
            }
            
        } else {
            
            // The message probably originated from this Actor so we'll wrap it
            // in a ActorMessage with us set as the sender
            message = new ActorMessage(this, message);
            
            for (Actor actor : connectedActors) {
                actor.queueMessage(message);
            }
        }
        
    }

    /**
     * Determines whether handleMessage should be invoked. This method will
     * always be executed in a single thread.
     *
     * @param message the message to handle
     * @return true if handleMessage should be invoked, otherwise false if the
     * message does not need handling - thus preventing handleMessage being
     * invoked.
     */
    protected abstract boolean shouldHandleMessage(Message message);

    /**
     * Handles messages passed to this actor through its message queue. Any
     * implementation of this should be thread-safe as multiple threads could be
     * running this method if this Actor is cloneable.
     *
     * @param message the message to handle
     */
    protected abstract void handleMessage(Message message);

}
