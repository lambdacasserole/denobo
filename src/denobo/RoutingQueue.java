package denobo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a route that should be taken by a message to reach its
 * destination actor.
 * 
 * @author Saul Johnson
 */
public class RoutingQueue {
   
    /**
     * The list of visited actors in chronological order.
     */
    private List<String> actorList;
    
    /**
     * Initialises a new instance of a routing queue.
     */
    public RoutingQueue() {
        actorList = new ArrayList<>();
    }
    
    /**
     * Initialises a new instance of a routing queue.
     * 
     * @param originatingActor  the originating actor
     */
    public RoutingQueue(Actor originatingActor) {
        this();
        enqueueActor(originatingActor);
    }
    
    /**
     * Initialises a new instance of a routing queue.
     * 
     * @param queue the routing queue to clone
     */
    public RoutingQueue(RoutingQueue queue) {
        this.actorList = new ArrayList(queue.getActorList());
    }
    
    /**
     * Gets the list of actors that underlies this instance.
     * 
     * @return  the list of actors that underlies this instance
     */
    public List<String> getActorList() {
        return actorList;
    }
    
    /**
     * Enqueues an actor into this routing queue.
     * 
     * @param actor the actor to enqueue
     */
    public final void enqueueActor(Actor actor) {
        actorList.add(actor.getName());
    }
    
    /**
     * Dequeues an actor name from this routing queue and returns it.
     * 
     * @return  an actor name retrieved on a first-in-first-out basis
     */
    public String pollActorName() {
        
        final String value = actorList.get(0);
        actorList.remove(0);
        
        return value;
        
    }

    /**
     * Checks to see if this routing queue already contains the specified actor.
     *
     * @param actor the actor to check for
     * @return      true if the actor is in the queue, otherwise false
     */
    public boolean hasActor(Actor actor) {
        return actorList.contains(actor.getName());
    }
    
    /**
     * Returns the number of members of this routing queue.
     * 
     * @return  the number of members in the routing queue
     */
    public int getSize() {
        return actorList.size();
    }
    
    /**
     * Returns the actor name at the start of the routing queue.
     * 
     * @return  the actor name at the start of the routing queue
     */
    public String peekStart() {
        return actorList.get(0);
    }
    
    /**
     * Returns the actor name at the end of the routing queue.
     * 
     * @return  the actor name at the end of the routing queue
     */
    public String peekEnd() {
        return actorList.get(actorList.size() - 1);
    }
    
    @Override
    public String toString() {
        
        final RoutingQueue printQueue = new RoutingQueue(this);
       
        final StringBuilder sb = new StringBuilder();
        while (printQueue.getSize() > 0) {
            sb.append(printQueue.pollActorName()).append(" -> \r\n");
        }
        sb.append("[End of Route]");
        
        return sb.toString();
        
    }
    
}
