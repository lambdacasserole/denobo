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
 * Represents an agent acting as part of a multi-agent system.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Agent implements RoutingWorkerListener {

    /**
     * A list of Agent instances connected to this one.
     */
    private final List<Agent> connectedAgents;

    /**
     * The {@link BlockingQueue} that underlies this Agent.
     */
    private final BlockingQueue<Message> messageQueue;

    /**
     * Contains messages ready for dispatching that are awaiting routing.
     */
    private final HashMap<String, List<String>> dispatchMap;
    
    /**
     * Contains the names of Agents whose routes are currently being calculated.
     */
    private final List<String> awaitingRoutingList;
    
    /**
     * The message processing thread that underlies this Agent.
     */
    private Thread underlyingThread;

    /**
     * The name of this Agent.
     */
    private final String name;

    /**
     * Whether or not this Agent is cloneable.
     */
    private final boolean cloneable;

    /**
     * The thread pool service for handling cloneable Agent instances. 
     * <p>
     * This variable is only initialised if this Agent is cloneable.
     */
    private final ExecutorService executorService;

    /**
     * Whether or not this Agent has been shut down. 
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
     * The routing table for this Agent.
     */
    public final RoutingTable routingTable;
    
    /**
     * A list of {@link MessageHandler} objects observing Messages passed to the 
     * agent.
     */
    private final List<MessageHandler> handlers;
    
    
    /* ---------- */
    
    
    /**
     * Abstract constructor to initialise a new instance of an Agent.
     *
     * @param name      the name of the Agent
     * @param cloneable whether or not the Agent is cloneable
     */
    public Agent(String name, boolean cloneable) {

        this.name = Objects.requireNonNull(name, "The name of the Agent cannot"
                + " be null.");
        this.cloneable = cloneable;
        
        shutdownLock = new Object();

        // Only construct the thread pool if cloneable.
        executorService = (cloneable ? Executors.newCachedThreadPool() : null);
        
        // Initialise lists, maps and queues.
        messageQueue = new LinkedBlockingQueue<>();
        connectedAgents = new CopyOnWriteArrayList<>();
        dispatchMap = new HashMap<>();
        awaitingRoutingList = new ArrayList<>();
        routingTable = new RoutingTable();
        handlers = new CopyOnWriteArrayList<>();
        
        // Start message processing.
        queueProcessThread();

    }

    /**
     * Initialises a new instance of a non-cloneable Agent.
     *
     * @param name  the name of the Agent
     */
    public Agent(String name) {
        this(name, false);
    }
    
    
    /* ---------- */
    
    
    /**
     * Adds a {@link MessageHandler} to listen for messages passed to this
     * Agent.
     *
     * @param handler the {@link MessageHandler} to add as an observer
     */
    public void addMessageHandler(MessageHandler handler) {
        handlers.add(Objects.requireNonNull(handler, "The message handler "
                + "to add cannot be null."));
    }

    /**
     * Removes a {@link MessageHandler} that is currently listening for messages
     * passed to this agent.
     *
     * @param handler the {@link MessageHandler} to remove as an observer
     */
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(Objects.requireNonNull(handler, "The message handler "
                + "to remove cannot be null."));
    }
    
    /**
     * Gets the name of this Agent.
     *
     * @return  the name of this Agent
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets whether or not this Agent is cloneable.
     *
     * @return  true if this Agent is cloneable, otherwise false
     */
    public final boolean isCloneable() {
        return cloneable;
    }
    
    /**
     * Gets whether or not this Agent has shut down or is currently in the 
     * process of shutting down.
     * 
     * @return  true if this Agent has shut down, otherwise false
     */
    public final boolean hasShutdown() {
        return shutdown;
    }

    /**
     * Connects this Agent to another.
     *
     * @param agent the Agent to connect to
     * @return      true if the specified Agent was successfully connected to 
     *              this Agent, otherwise false
     */
    public boolean connectAgent(Agent agent) {

        Objects.requireNonNull(agent, "Agent to connect cannot be null.");
        
        /* 
         * We don't want to connect any Agents if we are shutting down or are in
         * the process of shutting down.
         */
        synchronized (shutdownLock) {
            
            /* 
             * If the Agent is shut down, don't connect. Don't allow connection
             * of an Agent to itself.
             */
            if (shutdown || agent == this) { return false; }
            
            // Don't allow duplicates into the list.
            if (!connectedAgents.contains(agent)) {
                
                // Bidirectional registration.
                registerConnectedAgent(agent);
                agent.registerConnectedAgent(this);
                
            }
            
            return true;
            
        }
        
    }

    /**
     * Disconnects this Agent from another.
     *
     * @param agent the Agent to disconnect from
     * @return      true if the specified Agent was successfully disconnected
     *              from this Agent, otherwise false
     */
    public boolean disconnectAgent(Agent agent) {

        Objects.requireNonNull(agent, "Agent to disconnect cannot be null.");
        
        final boolean wasRemoved = connectedAgents.remove(agent);
        if (wasRemoved) {
            agent.unregisterConnectedAgent(this);
        }

        this.clearRoutingTable();
        agent.clearRoutingTable();
        
        
        final ArrayList<Agent> branches = new ArrayList<>(2);
        branches.add(this);
        branches.add(agent);
        
        final Undertaker undertaker = new Undertaker(branches, this.getName(), agent.getName());
        undertaker.undertakeAsync();
        
        return wasRemoved;
        
    }
    
    /**
     * Returns a read-only snapshot of the Agent instances that are connected to
     * this one as an unmodifiable list.
     * 
     * @return  the unmodifiable list of connected Agent instances
     */
    public List<Agent> getConnectedAgents() {
        return Collections.unmodifiableList(connectedAgents);
    }
    
    /**
     * Registers another Agent as connected to this one.
     *
     * @param agent the Agent to register
     */
    protected void registerConnectedAgent(Agent agent) {
        connectedAgents.add(agent);
    }

    /**
     * Unregisters another Agent as connected to this one.
     *
     * @param agent the Agent to unregister
     */
    protected void unregisterConnectedAgent(Agent agent) {
        connectedAgents.remove(agent);
    }

    
    /**
     * Adds a {@link Message} to this Agent's message queue.
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
     * Prevents any more messages from being added to this Agent's message queue
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

        // Remove all links to other Agents.
        for (Agent agent : connectedAgents) {
            agent.unregisterConnectedAgent(this);
        }
        connectedAgents.clear();
        
    }

    /**
     * Takes a recipient name/message data pair and stores it while it awaits
     * calculation of a route to the recipient.
     * 
     * @param recipientName the name of the recipient Agent
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
     * Originates a message from this Agent along the route stored in its
     * routing table for the recipient.
     * <p>
     * If the routing table has no entry to the recipient, an exception will
     * be thrown.
     * 
     * @param recipientName the name of the recipient Agent
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
        message.getRoute().next();

        // Queue the message here for processing.
        messageQueue.add(message);

        // Message sent, success.
        return true;
        
    }
    
    /**
     * Sends a message from this Agent to another.
     * 
     * @param recipientName the name of the recipient Agent
     * @param data          the data to attach to the message
     */
    public void sendMessage(String recipientName, String data) {
        
        // If we can't send the message right away because a route is missing.
        if (!originate(recipientName, data)) {
            
            /*
             * We need to route first, then send the message when the routing 
             * worker calls back.
             */
            System.out.println("Awaiting routing to Agent [" + recipientName + "]...");
            awaitRouting(recipientName, data);
            calculateRoute(recipientName);
            
        }
        
    }
    
    /**
     * Calculates the route to the agent with the specified name. When complete,
     * calls back on the {@link #routeCalculationSucceeded} method.
     * 
     * @param agentName the name of the agent to route to
     */
    public void calculateRoute(String agentName) {
        
        /* 
         * If we're already waiting on a route to this agent. don't start trying
         * to calculate it again.
         */
        //if (!awaitingRoutingList.contains(agentName)) {
            awaitingRoutingList.add(agentName);
            final RoutingWorker worker = new RoutingWorker(this, agentName);
            worker.addRoutingWorkerListener(this);
            worker.mapRouteAsync();
        //}
        
    }
    
    @Override
    public void routeCalculationSucceeded(String destinationAgentName, Route route) {
    
        System.out.println("Routing to Agent [" + destinationAgentName + "] complete:");
        System.out.println(route.toString()); 
        
        // Remove from awaiting list.
        awaitingRoutingList.remove(destinationAgentName);
        
        // Add to routing table.
        routingTable.addRoute(destinationAgentName, route);
        
        // Any messages waiting for this route are now free to be sent.
        if (dispatchMap.containsKey(destinationAgentName)) {
            
            // Get all waiting messages.
            final List<String> waitingMessages = dispatchMap.get(destinationAgentName);
            
            System.out.println("Found " + waitingMessages.size() + " messages waiting.");
            
            // Send all waiting messages.
            for (String current : waitingMessages) {
                originate(destinationAgentName, current);
            }
            dispatchMap.remove(destinationAgentName);
            
        }
        
    }
    
    public void invalidateAgentName(String agentName) {
        System.out.println("invalidateAgent " + agentName + " from " + name);
        routingTable.invalidateAgent(agentName);
    }
    
    public void clearRoutingTable() {
        routingTable.clear();
    }

    /**
     * Handles messages passed to this Agent through its message queue.
     * <p>
     * Any implementation of this should be thread-safe as multiple threads 
     * could be running this method if this Agent is cloneable.
     *
     * @param message   the message to handle
     * @return          true if the message was handled, otherwise false
     */
    protected boolean handleMessage(Message message) {

        /* 
         * If we are cloneable, this method will possibly be executing in
         * multiple threads.
         */
        
        // Handled flag.
        boolean wasHandled = false;
        
        /* 
         * Let any handlers know of the message received even though it may
         * not be intended for us.
         */
        for (MessageHandler handler : handlers) {
            handler.messageIntercepted(this, message);
        }
        
        /* 
         * If this Agent is the intended recipient of the message, alert each
         * registered message handler.
         */
        if (message.getRecipient().equals(this.getName())) {
            for (MessageHandler handler : handlers) {
                handler.messageRecieved(this, message);
            }
            wasHandled = true;
        } else {
            
            // Otherwise, forward to next Agent in route.
            final String nextRecipient = message.getRoute().next();
            for (Agent current : connectedAgents) {
                if (current.getName().equals(nextRecipient)) {
                    current.queueMessage(message);
                    wasHandled = true;
                }
            }
            
        }
        
        return wasHandled;
        
    }

}
