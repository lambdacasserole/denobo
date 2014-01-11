package denobo;

import denobo.socket.SocketAgent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a worker that will calculate the optimal route to an actor.
 * 
 * @author Saul Johnson
 */
public class RoutingWorker implements Runnable {

    /**
     * The thread that underlies this worker.
     */
    private Thread underlyingThread;
    
    /**
     * The routing queues returned by the algorithm.
     */
    private List<Route> routes;
    
    /**
     * Any found SocketAgents we come across.
     */
    private List<SocketAgent> foundSocketAgents;
    private List<Route> socketAgentRoutes;
    
    /**
     * The actor from which the routing algorithm will start.
     */
    private final Agent fromActor;
    
    /**
     * The name of the actor that the routing algorithm is seeking.
     */
    private final String toActorName;
    
    /**
     * The listening observers that will be notified when optimal route
     * calculation is complete.
     */
    private final List<RoutingWorkerListener> listeners;
    
    private final Route initialRoutingQueue;
    
    /**
     * Initialises a new instance of a routing worker.
     * 
     * @param fromActor     the actor from which this worker will start
     * @param toActorName   the name of the destination actor
     */
    public RoutingWorker(Agent fromActor, String toActorName) {
        this(fromActor, toActorName, new Route());
    }
    
    public RoutingWorker(Agent fromActor, String toActorName, Route initialRoutingQueue) {
        this.fromActor = fromActor;
        this.toActorName = toActorName;
        this.initialRoutingQueue = initialRoutingQueue;
        
        listeners = new ArrayList<>();
    }
    
    /**
     * Adds a listener to this routing worker.
     * 
     * @param listener  the listener to add
     */
    public void addRoutingWorkerListener(RoutingWorkerListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener from this routing worker.
     * 
     * @param listener  the listener to remove
     */
    public void removeRoutingWorkerListener(RoutingWorkerListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
    
    /**
     * Recursive function to map routes to the destination node.
     * 
     * @param actor the actor we're currently routing from
     * @param route the currently accumulated routing queue
     */
    private void route(Agent actor, Route route) {
        
        /*
         * Remember any SocketAgents we might need to ask if we cannot find a
         * local route.
         */ 
        if (actor instanceof SocketAgent) {
            foundSocketAgents.add((SocketAgent) actor);
            socketAgentRoutes.add(route);
        }

        // For each actor connected to our originator.
        final List<Agent> connections = actor.getConnectedAgents();
        for (Agent current : connections) {

            // An optimal route will never take us through the same node twice.
            if (route.has(current)) {
                continue;
            }
            
            // Clone route, append this actor.
            final Route newQueue = new Route(route);
            newQueue.append(current);
            
            // If this actor is our destination, put our route into the array.
            if (current.getName().equals(toActorName)) {
                routes.add(newQueue);
            } else {
                
                // Otherwise continue recursing through the tree.
                route(current, newQueue);
                
            }
            
        }
        
    }
    
    @Override
    public void run() {
        
        // Initialise our array of routes.
        routes = new ArrayList<>();
        foundSocketAgents = new ArrayList<>();
        socketAgentRoutes = new ArrayList<>();
        
        // Recursively map routes to destination.
        final Route queue = new Route(initialRoutingQueue);
        queue.append(fromActor);
        route(fromActor, queue);
        
        // Get shortest route from all possible routes.        
        Route shortestRoute = null;
        for (Route current : routes) {
            if (shortestRoute == null || shortestRoute.size() > current.size()) {
                shortestRoute = current;
            }
        }
        
        // Notify listeners that route calculation is complete if we found a local
        // route.
        if (shortestRoute != null) {
            for (RoutingWorkerListener current : listeners) {
                current.routeCalculationSucceeded(toActorName, shortestRoute);
            }
        } else {
            // We didn't find a local route so we'll ask any found SocketAgent's
            // to contact their connections and search for it.
            for (int i = 0; i < foundSocketAgents.size(); i++) {
                foundSocketAgents.get(i).routeToRemote(toActorName, socketAgentRoutes.get(i), listeners);
            }
        }
        
    }
    
    /**
     * Maps this route on another thread.
     * <p>
     * Registered {@link RoutingWorkerListener} instances will be notified when
     * route calculation is complete.
     */
    public void mapRouteAsync() {
        underlyingThread = new Thread(this);
        underlyingThread.start();
    }
    
}
