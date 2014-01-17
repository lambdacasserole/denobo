package denobo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Represents an agent acting as part of a multi-agent system.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
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
    private final Map<String, List<String>> dispatchMap;
    
    /**
     * Contains the names of Agents whose routes are currently being calculated.
     */
    private final Map<String, Long> awaitingRoutingMap;
    
    /**
     * The message processing thread that underlies this Agent.
     */
    private Thread underlyingThread;

    /**
     * The name of this Agent.
     */
    private final String name;

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
    private final RoutingTable routingTable;
    
    /**
     * A list of {@link MessageListener} objects observing Messages passed to the 
     * agent.
     */
    private final List<MessageListener> listeners;
    
    /**
     * The timer instance that will execute a task on a scheduled interval and 
     * cleanup any messages that are awaiting for a route to be calculated where
     * the route calculation has timed out.
     * <p>
     * A route calculation that has timed out should be assumed to currently not
     * exist in the network.
     */
    private final Timer dispatchCleanupTimer;
    
    
    /* ---------- */
    
    
    /**
     * The value in milliseconds at which point we consider that a route
     * calculation has timed out.
     */
    private static final long ROUTE_CALCULATION_TIMEOUT = 1000L;
    
    /**
     * The interval at which any messages waiting for dispatch on a route that
     * is assumed to be inactive are cleaned up.
     */
    private static final long DISPATCH_CLEANUP_INTERVAL = 1000L;
    
    /**
     * The regular expression that is used to validate the names of Agents.
     */
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_]*$");
    
    
    /* ---------- */
    
    
    /**
     * Abstract constructor to initialise a new instance of an Agent.
     *
     * @param name      the name of the Agent
     * @param cloneable whether or not the Agent is cloneable
     */
    public Agent(String name, boolean cloneable) {

        // Agent names cannot be null, must be alphanumeric and cannot
        // start with a number. Underscores are allowed.
        this.name = Objects.requireNonNull(name, "The name of the Agent cannot"
                + " be null.");
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid agent name.");
        }
        
        shutdownLock = new Object();

        // Only construct the thread pool if cloneable.
        executorService = (cloneable ? Executors.newCachedThreadPool() : null);
        
        // Initialise lists, maps and queues.
        messageQueue = new LinkedBlockingQueue<>();
        connectedAgents = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        
        routingTable = new RoutingTable();
        dispatchMap = Collections.synchronizedMap(new HashMap<String, List<String>>());
        awaitingRoutingMap = Collections.synchronizedMap(new HashMap<String, Long>());
        
        // Initialize the dispatch cleanup task
        dispatchCleanupTimer = new Timer(true);
        dispatchCleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanupDispatch(ROUTE_CALCULATION_TIMEOUT);
            }
        }, DISPATCH_CLEANUP_INTERVAL, DISPATCH_CLEANUP_INTERVAL);
        
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
     * Validates a potential agent name.
     * 
     * @param name  the agent name to validate
     * @return      true if the agent name is valid, otherwise false
     */
    public static boolean isValidName(String name) {
        return VALID_NAME_REGEX.matcher(name).matches();
    }
    
    /**
     * Adds a {@link MessageListener} to listen for messages passed to this
     * Agent.
     *
     * @param listener   the {@link MessageListener} to add as an observer
     */
    public void addMessageListener(MessageListener listener) {
        listeners.add(Objects.requireNonNull(listener, "The message handler "
                + "to add cannot be null."));
    }

    /**
     * Removes a {@link MessageListener} that is currently listening for messages
     * passed to this agent.
     *
     * @param listener the {@link MessageListener} to remove as an observer
     */
    public void removeMessageListener(MessageListener listener) {
        listeners.remove(Objects.requireNonNull(listener, "The message handler "
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
     * Gets this agent's routing table.
     * 
     * @return  this agent's routing table
     */
    public RoutingTable getRoutingTable() {
        return routingTable;
    }
    
    /**
     * Gets whether or not this Agent is cloneable.
     *
     * @return  true if this Agent is cloneable, otherwise false
     */
    public final boolean isCloneable() {
        return executorService != null;
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

        // Cannot connect null agents.
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

        // Cannot disconnect null agents.
        Objects.requireNonNull(agent, "Agent to disconnect cannot be null.");
        
        // Try to remove agent.
        final boolean wasRemoved = unregisterConnectedAgent(agent);
        if (wasRemoved) {
            
            agent.unregisterConnectedAgent(this);
          
            /* 
             * Spawn undertaker to crawl the local network and remove any routes
             * involving this link.
             */
            final List<Agent> branches = Arrays.asList(new Agent[] {this, agent});  
            final Undertaker undertaker = new Undertaker(branches, 
                    Arrays.asList(new String[] {Agent.this.getName(), agent.getName()}));
            undertaker.undertakeAsync();
        
        }

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
     * @return true if the agent was successfully registered, otherwise false
     */
    private boolean registerConnectedAgent(Agent agent) {
        return connectedAgents.add(agent);
    }

    /**
     * Unregisters another Agent as connected to this one.
     *
     * @param agent the Agent to unregister
     * @return true if the agent was successfully unregistered
     */
    private boolean unregisterConnectedAgent(Agent agent) {
        invalidateAgentName(agent.getName());
        return connectedAgents.remove(agent);
    }

    
    /**
     * Adds a Message to this Agent's message queue.
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
             * caller higher up the chain if we were interuppted.
             */
            if (interrupted) { Thread.currentThread().interrupt(); }
            return added;
            
        }
        
    }

    /**
     * Starts the message processing on a new thread and places the handle into 
     * the underlyingThread field.
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

                        if (!isCloneable()) {

                            // Handle message in this thread.
                            handleMessage(message);

                        } else {

                            // Handle message on a seperate thread.
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
            System.out.println("Thread was interrupted during message pump shutdown.");
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
                System.out.println("Executor service shutdown was interrupted. "
                    + "Some queued messages may not have been processed.");
            }
        }

        final ArrayList<Agent> branches = new ArrayList<>(connectedAgents.size());
        final ArrayList<String> agentNames = new ArrayList<>(connectedAgents.size());
        
        branches.add(this);
        for (Agent current : connectedAgents) {
            branches.add(current);
            agentNames.add(current.getName());
        }
        
        // Remove all links to other Agents.
        for (Agent agent : connectedAgents) {
            unregisterConnectedAgent(agent);
            agent.unregisterConnectedAgent(this);
        }
        
        final Undertaker undertaker = new Undertaker(branches, agentNames);
        undertaker.undertakeAsync();
        
        // Stop the scheduled dispatch cleaner task then clean the dispatch
        dispatchCleanupTimer.cancel();
        
        // Clear all data.
        messageQueue.clear();
        connectedAgents.clear();
        awaitingRoutingMap.clear();
        dispatchMap.clear();
        routingTable.clear();
        listeners.clear();

    }

    /**
     * Sends a message from this Agent to another.
     * 
     * @param recipientName the name of the recipient Agent
     * @param data          the data to attach to the message
     */
    public void sendMessage(String recipientName, String data) {

        Objects.requireNonNull(recipientName, "The recipient of a message cannot be null.");
        
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
        
        // Check and retrieve if there is a route to the specified recipient.
        final Route route = routingTable.getRoute(recipientName);
        if (route == null) {
            // No route.
            return false;
        }

        // Create message and attach route
        final Message message = new Message(route, data);
        
        // The first entry in the routing queue is this agent. Discard this entry.
        message.getRoute().next();

        // Queue the message here for processing.
        messageQueue.add(message);

        // Message sent, success.
        return true;

    }
    
    /**
     * Takes a recipient name/message data pair and stores it while it awaits
     * calculation of a route to the recipient.
     * 
     * @param recipientName the name of the recipient Agent
     * @param data          the data to attach to the message
     */
    private void awaitRouting(String recipientName, String data) {
        
        synchronized (dispatchMap) {
            List<String> messageList = dispatchMap.get(recipientName);
            if (messageList == null) {
                messageList = Collections.synchronizedList(new ArrayList<String>());
                dispatchMap.put(recipientName, messageList);
            }
            messageList.add(data);
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
        synchronized (awaitingRoutingMap) {
            if (!awaitingRoutingMap.containsKey(agentName)) {
                awaitingRoutingMap.put(agentName, Long.valueOf(System.currentTimeMillis()));
                final RoutingWorker worker = new RoutingWorker(this, agentName);
                worker.addRoutingWorkerListener(this);
                worker.mapRouteAsync();                
            }
        }
        
    }
    
    @Override
    public void routeCalculationSucceeded(String destinationAgentName, Route route) {
    
        System.out.println("Routing to Agent [" + destinationAgentName + "] complete:");
        System.out.println(route.toString()); 
        
        // Remove from awaiting list.
        awaitingRoutingMap.remove(destinationAgentName);
        
        // Add to routing table.
        routingTable.addRoute(destinationAgentName, route);
        
        /*
         * Any messages waiting for this route are now free to be sent.
         * Also prevent the dispatchMap from changing whilst we use it.
         */
        synchronized (dispatchMap) {
            
            final List<String> waitingMessages = dispatchMap.get(destinationAgentName);
            if (waitingMessages != null) {

                System.out.println("Found " + waitingMessages.size() + " messages waiting.");

                /* 
                 * Make sure no else can modify the list in case they acquired it
                 * somehow.
                 */
                synchronized (waitingMessages) {
                    // Send all waiting messages.
                    for (String current : waitingMessages) {
                        originate(destinationAgentName, current);
                    }
                }
                dispatchMap.remove(destinationAgentName);

            }
            
        }
        
    }
    
    /**
     * Removes any pending messages from the dispatch map that have been there
     * longer than the specified timeout.
     * <p>
     * We can assume that if messages are not delivered by a certain timeout,
     * the destination does not exist so to prevent us leaking memory we will
     * need to cleanup any messages that we will not be dispatching.
     * 
     * @param timeout the timeout in milliseconds
     */
    private void cleanupDispatch(long timeout) {
        
        /*
         * Go through every destination entry we are currently trying to find a
         * route to.
         */
        
        synchronized (awaitingRoutingMap) {
            
            final Iterator<Entry<String, Long>> routingEntry = 
                                            awaitingRoutingMap.entrySet().iterator();
            while (routingEntry.hasNext()) {
                final Entry<String, Long> currentEntry = routingEntry.next();

                // Make sure the dispatch map does not change.
                synchronized (dispatchMap) {
                    
                    /* 
                     * Check if the current destination to route has still not been
                     * calculated before the specified timeout.
                     */
                    if ((System.currentTimeMillis() - currentEntry.getValue()) >= timeout) {
                        
                        /**
                         * The calculation to this route has timed out so we will
                         * clear any waiting messages that were waiting to be 
                         * dispatched to this destination
                         */
                        final List<String> waitingMessages = dispatchMap.get(currentEntry.getKey());
                        if (waitingMessages != null) {
                            System.out.println("Cleared " + waitingMessages.size() 
                                    + " messages intended for " + currentEntry.getKey() 
                                    + " due to routing timeout.");
                            waitingMessages.clear();
                            dispatchMap.remove(currentEntry.getKey());
                        }

                        // Remove the routing entry from the awaitingRoutingMap
                        routingEntry.remove();

                    }

                }

            }
            
        }

    }
    
    /**
     * Invalidates a name from this agent's routing table.
     * 
     * @param agentName the name of the agent to invalidate
     */
    public void invalidateAgentName(String agentName) {
        routingTable.invalidateAgent(agentName);
    }
    
    /**
     * Clears this agent's routing table.
     */
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
        
        /* 
         * Let any listeners know of the message received even though it may
         * not be intended for us.
         */
        for (MessageListener handler : listeners) {
            handler.messageIntercepted(this, message);
        }
        
        /* 
         * If this Agent is the intended recipient of the message, alert each
         * registered message listener.
         */
        if (message.getRecipient().equals(this.getName())) {
            for (MessageListener handler : listeners) {
                handler.messageRecieved(this, message);
            }
            return true;
        } else {
            
            // Otherwise, forward to next Agent in route.
            final String nextRecipient = message.getRoute().next();
            for (Agent current : connectedAgents) {
                if (current.getName().equals(nextRecipient)) {
                    current.queueMessage(message);
                    return true;
                }
            }
            
        }
        
        // We did not handle the message if execution gets to here so return false
        return false;
        
    }

}
