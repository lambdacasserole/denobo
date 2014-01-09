package denobo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
public abstract class Actor implements RoutingWorkerListener {

    /**
     * A list of Actor instances connected to this one.
     */
    private final List<Actor> connectedActors;

    /**
     * The {@link BlockingQueue} that underlies this Actor.
     */
    private final BlockingQueue<Message> messageQueue;

    /**
     * Contains messages ready for dispatching that are awaiting routing.
     */
    private final HashMap<String, List<String>> dispatchMap;
    
    /**
     * Contains the names of Actors whose routes are currently being calculated.
     */
    private final List<String> awaitingRoutingList;
    
    /**
     * The message processing thread that underlies this Actor.
     */
    private Thread underlyingThread;

    /**
     * The name of this Actor.
     */
    private final String name;

    /**
     * Whether or not this Actor is cloneable.
     */
    private final boolean cloneable;

    /**
     * The thread pool service for handling cloneable Actor instances. 
     * <p>
     * This variable is only initialised if this Actor is cloneable.
     */
    private final ExecutorService executorService;

    /**
     * Whether or not this Actor has been shut down. 
     * <p>
     * Signals underlyingThread to stop processing when set to true.
     * 
     * @see #underlyingThread
     */
    private volatile boolean shutdown;
    
    /**
     * Holding this lock will hold off a potential shutdown until released. 
     */
    private final Object shutdownLock;
    
    /**
     * The routing table for this Actor.
     */
    private final RoutingTable routingTable;
    
    
    /* ---------- */
    
    
    /**
     * Abstract constructor to initialise a new instance of an Actor.
     *
     * @param name      the name of the Actor
     * @param cloneable whether or not the Actor is cloneable
     */
    public Actor(String name, boolean cloneable) {

        this.name = Objects.requireNonNull(name, "The name of the Actor is null");
        this.cloneable = cloneable;

        messageQueue = new LinkedBlockingQueue<>();
        connectedActors = new CopyOnWriteArrayList<>();
        shutdownLock = new Object();

        // Only construct the thread pool if cloneable.
        executorService = (cloneable ? Executors.newCachedThreadPool() : null);

        dispatchMap = new HashMap<>();
        awaitingRoutingList = new ArrayList<>();
        routingTable = new RoutingTable();
        
        // Start message processing.
        queueProcessThread();

    }

    /**
     * Abstract constructor to initialise a new instance of a non-cloneable
     * Actor.
     *
     * @param name  the name of the Actor
     */
    public Actor(String name) {
        this(name, false);
    }
    
    
    /* ---------- */
    
    
    /**
     * Gets the name of this Actor.
     *
     * @return  the name of this Actor
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets whether or not this Actor is cloneable.
     *
     * @return  true if this Actor is cloneable, otherwise false
     */
    public final boolean isCloneable() {
        return cloneable;
    }
    
    /**
     * Gets whether or not this Actor has shut down or is currently in the 
     * process of shutting down.
     * 
     * @return  true if this Actor has shut down, otherwise false
     */
    public final boolean hasShutdown() {
        return shutdown;
    }

    /**
     * Connects this Actor to another.
     *
     * @param actor the Actor to connect to
     * @return      true if the specified Actor was successfully connected to 
     *              this Actor, otherwise false
     */
    public boolean connectActor(Actor actor) {

        Objects.requireNonNull(actor, "Actor to connect cannot be null.");
        
        /* 
         * We don't want to connect any Actors if we are shutting down or are in
         * the process of shutting down.
         */
        synchronized (shutdownLock) {
            
            /* 
             * If the Actor is shut down, don't connect. Don't allow connection
             * of an Actor to itself.
             */
            if (shutdown || actor == this) { return false; }
            
            // Don't allow duplicates into the list.
            if (!connectedActors.contains(actor)) {
                
                // Bidirectional registration.
                registerConnectedActor(actor);
                actor.registerConnectedActor(this);
                
            }
            
            return true;
        }
        
    }

    /**
     * Disconnects this Actor from another.
     *
     * @param actor the Actor to disconnect from
     * @return      true if the specified Actor was successfully disconnected
     *              from this Actor, otherwise false
     */
    public boolean disconnectActor(Actor actor) {

        Objects.requireNonNull(actor, "Actor to disconnect cannot be null.");
        
        final boolean wasRemoved = connectedActors.remove(actor);
        if (wasRemoved) {
            actor.unregisterConnectedActor(this);
        }
        return wasRemoved;
        
    }
    
    /**
     * Returns a read-only snapshot of the Actor instances that are connected to
     * this one as an unmodifiable list.
     * 
     * @return  the unmodifiable list of connected Actor instances
     */
    public List<Actor> getConnectedActors() {
        return Collections.unmodifiableList(connectedActors);
    }
    
    /**
     * Registers another Actor as connected to this one.
     *
     * @param actor the Actor to register
     */
    protected void registerConnectedActor(Actor actor) {
        connectedActors.add(actor);
    }

    /**
     * Unregisters another Actor as connected to this one.
     *
     * @param actor the Actor to unregister
     */
    protected void unregisterConnectedActor(Actor actor) {
        connectedActors.remove(actor);
    }

    
    /**
     * Adds a {@link Message} to this Actor's message queue.
     *
     * @param message   the message to add
     * @return          true if the message was successfully submitted into the 
     *                  message queue, otherwise false
     */
    public boolean queueMessage(Message message) {
        
        /* 
         * We need to make sure the thread won't be shut down between checking
         * if it has shutdown and adding the message to the queue - we want to
         * guarantee that if our message is added, that it will be processed.
         */
        synchronized (shutdownLock) {
            
            /* 
             * Don't queue the message if we have shut down or we are in the 
             * process of shutting down.
             */
            if (shutdown) { return false; }
            
            /* 
             * Make sure our message gets added to the queue as something 
             * interrupting our thread could cause our message to be lost.
             */
            boolean added = false, interrupted = false;
            do {
                try {
                    
                    // Queue message for processing.
                    messageQueue.put(message);
                    added = true; 
                    
                } catch (InterruptedException ex) {
                    
                    interrupted = true;
                    
                }
            } while (!added);
            
            /* 
             * Remember to reset the interrupt flag for this thread for any 
             * higher up the chain caller if we were interuppted.
             */
            if (interrupted) { Thread.currentThread().interrupt(); }
            return added;
            
        }
        
    }

    /**
     * Starts the message processing on a new thread and places the handle 
     * into the underlyingThread field.
     * 
     * @see #underlyingThread
     */
    private void queueProcessThread() {

        // Process queued messages on another thread.
        underlyingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                /* 
                 * Keep processing till we are given the signal to stop then 
                 * process everything until the queue is empty.
                 */
                while (!shutdown || !messageQueue.isEmpty()) {
                    try {
                        final Message message = messageQueue.take();

                        // Check if we should bother handling this message.
                        if (!shouldHandleMessage(message)) { continue; }

                        if (!cloneable) {

                            // Handle message in this thread.
                            handleMessage(message);

                        } else {

                            // Execute handleMessage on a seperate thread.
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    handleMessage(message);
                                }
                            });

                        }
                    } catch (InterruptedException ex) {

                        /* 
                         * Swallow the exception as a chance for us to unblock 
                         * from the take() call and check whether we need to 
                         * begin shutting down.
                         */
                        System.out.println("Thread interupted, shutting down when finished: " + name);

                    }
                }
            }
        });
        underlyingThread.start();
        
    }
        
    /**
     * Prevents any more messages from being added to this Actor's message queue
     * and processes any that remain. 
     * <p>
     * The queue is then shut down and all links to other agents are removed.
     */
    public void shutdown() {
        
        /* 
         * Set the signal to not accept any more messages into the queue and for
         * the queue to finish processing any remaining messages. We need the
         * lock in so we can guarantee that everything added to the queue before
         * this point will be processed. 
         */
        synchronized (shutdownLock) {
            shutdown = true;
            underlyingThread.interrupt();
        }
        
        // Wait for the queue thread to finish.
        try {
            underlyingThread.join();
        } catch (InterruptedException ex) {
            // TODO: Handle exception.
            System.out.println(ex.getMessage());
        }
        
        /* 
         * Shutdown the the thread pool if we are cloneable and wait till all 
         * the threads are finished. 
         */
        if (executorService != null) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                // TODO: Handle exception.
                System.out.println(ex.getMessage());
            }
        }    

        // Remove all links to other Actors.
        for (Actor actor : connectedActors) {
            actor.unregisterConnectedActor(this);
        }
        connectedActors.clear();
        
    }

    /**
     * Takes a recipient name/message data pair and stores it while it awaits
     * calculation of a route to the recipient.
     * 
     * @param recipientName the name of the recipient Actor
     * @param data          the data to attach to the message
     */
    private void awaitRouting(String recipientName, String data) {
        
        List<String> messageList;
        if (!dispatchMap.containsKey(recipientName)) {
            messageList = new ArrayList<>();
            dispatchMap.put(recipientName, messageList);
        } else {
            messageList = dispatchMap.get(recipientName);
        }
        
        messageList.add(data);
        
    }
        
    /**
     * Forwards a message to the next actor along its route.
     * <p>
     * If this actor is the intended recipient of the message, it will be queued
     * for processing, and not forwarded any further.
     * 
     * @param message   the Message object to forward
     */
    protected void forwardMessage(Message message) {
     
        // Are we this message's intended recipient?
        if (message.getRecipient().equals(getName())) {
            
            // If so, queue it for processing.
            queueMessage(message);
            
        } else {
            
            // Otherwise, forward to next actor in route.
            final String nextRecipient = message.getNextActorName();
            for (Actor current : connectedActors) {
                if (current.getName().equals(nextRecipient)) {
                    current.forwardMessage(message);
                }
            }
        
        }
        
    }
    
    /**
     * Originates a message from this actor along the route stored in its
     * routing table for the recipient.
     * <p>
     * If the routing table has no entry to the recipient, an exception will
     * be thrown.
     * 
     * @param recipientName the name of the recipient Actor
     * @param data          the data to attach to the message
     * @return              true if message sending was successful, otherwise
     *                      false
     */
    private boolean originate(String recipientName, String data) {
        
        // No routing entry, failure.
        if (!routingTable.hasRoute(recipientName)) {
            return false;
        }
        
        // Create message, attach routing queue.
        final Message message = new Message(routingTable.getRoute(recipientName), data);    
            
        // The first entry in the routing queue is this agent. Discard this entry.
        message.getNextActorName();

        // Foward the message on.
        forwardMessage(message);

        // Message sent, success.
        return true;
        
    }
    
    /**
     * Sends a message from this Actor to another.
     * 
     * @param recipientName the name of the recipient Actor
     * @param data          the data to attach to the message
     */
    public void sendMessage(String recipientName, String data) {
        
        // If we can't send the message right away because a route is missing.
        if (!originate(recipientName, data)) {
            
            /*
             * We need to route first, then send the message when the routing 
             * worker calls back.
             */
            System.out.println("Awaiting routing to actor [" + recipientName + "]...");
            awaitRouting(recipientName, data);
            calculateRoute(recipientName);
            
        }
        
    }
    
    /**
     * Calculates the route to the agent with the specified name. When complete,
     * calls back on the {@link #routeCalculated} method.
     * 
     * @param agentName the name of the agent to route to
     */
    public void calculateRoute(String agentName) {
        
        // If we're already waiting on a route to this agent. don't start trying
        // to calculate it again.
        if (!awaitingRoutingList.contains(agentName)) {
            awaitingRoutingList.add(agentName);
            final RoutingWorker worker = new RoutingWorker(this, agentName);
            worker.addRoutingWorkerListener(this);
            worker.mapRouteAsync();
        }
        
    }
    
    @Override
    public void routeCalculated(String destinationActorName, RoutingQueue route) {
    
        System.out.println("Routing to actor [" + destinationActorName + "] complete:");
        System.out.println(route.toString()); 
        
        // Remove from awaiting list.
        awaitingRoutingList.remove(destinationActorName);
        
        // Add to routing table.
        routingTable.addRoute(destinationActorName, route);
        
        // Any messages waiting for this route are now free to be sent.
        if (dispatchMap.containsKey(destinationActorName)) {
            
            // Get all waiting messages.
            final List<String> waitingMessages = dispatchMap.get(destinationActorName);
            
            System.out.println("Found " + waitingMessages.size() + " messages waiting.");
            
            // Send all waiting messages.
            for (String current : waitingMessages) {
                originate(destinationActorName, current);
            }
            dispatchMap.remove(destinationActorName);
            
        }
        
    }
    
    /**
     * Determines whether handleMessage should be invoked. 
     * <p>
     * This method will always be executed in a single thread.
     * 
     * @param message   the message to handle
     * @return          true if handleMessage should be invoked, otherwise false
     * @see             #handleMessage
     */
    protected abstract boolean shouldHandleMessage(Message message);

    /**
     * Handles messages passed to this Actor through its message queue.
     * <p>
     * Any implementation of this should be thread-safe as multiple threads 
     * could be running this method if this Actor is cloneable.
     *
     * @param message   the message to handle
     */
    protected abstract void handleMessage(Message message);

}
