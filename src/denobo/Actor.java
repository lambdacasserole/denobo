package denobo;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
     * Adds a message to the actor's message queue.
     *
     * @param message the message to add
     */
    public void queueMessage(Message message) {
        try {

            // Queue message for processing.
            messageQueue.put(message);

        } catch (InterruptedException ex) {

            // TODO: Handle exception.
            System.out.println(ex.getMessage());

        }
    }

    /**
     * The loop that processes this Actor's message queue.
     */
    private void queueProcessLoop() {

        // Loop until interrupted.
        while (true) {
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

                // TODO: Handle exception.
                System.out.println(ex.getMessage());

            }
        }
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
     * Connects the actor to another.
     *
     * @param actor the actor to connect to
     */
    public void connectActor(Actor actor) {
        //if (actor == this) { return; }
        connectedActors.add(actor);
        actor.registerConnectedActor(this);
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
