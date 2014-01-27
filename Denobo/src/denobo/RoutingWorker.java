package denobo;

import denobo.exceptions.RouteToSelfException;
import denobo.socket.SocketAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Represents a worker that will calculate the optimal route to an actor.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class RoutingWorker implements Runnable {

    /**
     * The thread that underlies this worker.
     */
    private Thread underlyingThread;
    
    /**
     * The potential routes returned by the recursive algorithm.
     */
    private List<Route> routes;
    
    /**
     * Any SocketAgent instances encountered during the recursive crawl of the 
     * local network along with routes to them from the origin.
     * 
     * @see #origin
     */
    private HashMap<SocketAgent, Route> socketAgentRoutePairs;
    
    /**
     * The actor from which the routing algorithm will start.
     */
    private final Agent origin;
    
    /**
     * The name of the actor that the routing algorithm is seeking.
     */
    private final String destination;
    
    /**
     * An instance of the destination actor, if found during the last routing
     * task.
     */
    private Agent destinationInstance;
    
    /**
     * The listening observers that will be notified when optimal route
     * calculation is complete.
     */
    private final List<RoutingWorkerListener> listeners;
    
    /**
     * The existing route to append to.
     */
    private final Route initialRoute;
    
    /**
     * If a routing worker uses backtracking, it will add a reversed copy of the
     * route from the origin to the destination to the destination agent's 
     * routing table, making replying to any messages more efficient.
     */
    private boolean usesBacktracking = false;
    
    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of a routing worker that uses backtracking.
     * 
     * @param origin        the actor from which this worker will start
     * @param destination   the name of the destination actor
     */
    public RoutingWorker(Agent origin, String destination) {
        this(origin, destination, new Route(), true);
    }
    
    /**
     * Initialises a new instance of a routing worker.
     *
     * @param origin            the actor from which this worker will start
     * @param destination       the name of the destination actor
     * @param initialRoute      the existing route to append to
     * @param usesBacktracking  whether or not this worker uses backtracking
     * @see #getUsesBacktracking
     */
    public RoutingWorker(Agent origin, String destination, Route initialRoute, boolean usesBacktracking) {
        this.origin = origin;
        this.destination = destination;
        this.initialRoute = initialRoute;
        this.usesBacktracking = usesBacktracking;
        listeners = new ArrayList<>();
    }
    
    
    /* ---------- */
    
    /**
     * Gets the agent instance that spawned this routing worker.
     * 
     * @return  the agent instance that spawned this routing worker
     */
    public Agent getOrigin() {
        return origin;
    }
    
    /**
     * Gets whether or not this routing worker uses backtracking.
     * <p>
     * If a routing worker uses backtracking, it will add a reversed copy of the
     * route from the origin to the destination to the destination agent's 
     * routing table, making replying to any messages more efficient.
     * 
     * @return  true if this worker uses backtracking, otherwise false
     */
    public boolean getUsesBacktracking() {
        return usesBacktracking;
    }
    
    /**
     * Sets whether or not this routing worker uses backtracking.
     * <p>
     * If a routing worker uses backtracking, it will add a reversed copy of the
     * route from the origin to the destination to the destination agent's 
     * routing table, making replying to any messages more efficient.
     * 
     * @param useBacktracking   whether or not to use backtracking
     */
    public void setUsesBacktracking(boolean useBacktracking) {
        this.usesBacktracking = useBacktracking;
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
     * @param agent the agent we're currently routing from
     * @param route the currently accumulated routing queue
     */
    private void route(Agent agent, Route route) {
        
        /*
         * Remember any SocketAgents we might need to check if we cannot find a
         * local route.
         */ 
        if (agent instanceof SocketAgent) {
            socketAgentRoutePairs.put((SocketAgent) agent, route);
        }

        // For each agent connected to our originator.
        for (Agent current : agent.getConnectedAgents()) {

            // An optimal route will never take us through the same node twice.
            if (route.has(current)) {
                continue;
            }
            
            // Clone route, append this agent.
            final Route newQueue = new Route(route);
            newQueue.append(current);
            
            // If this agent is our destination, put our route into the array.
            if (current.getName().equals(destination)) {
                routes.add(newQueue);
                destinationInstance = current;
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
        socketAgentRoutePairs = new HashMap<>();
        destinationInstance = null;
        
        /* 
         * Check if we're trying to route to ourself because this is illegal
         * and should not be happening in the first place.
         */
        if (origin.getName().equals(destination)) {
            throw new RouteToSelfException(this);
        }
        
        // Recursively map routes to destination.
        final Route queue = new Route(initialRoute);
        queue.append(origin);    
        route(origin, queue);
        
        // Get shortest route from all possible routes.        
        Route shortestRoute = null;
        for (Route current : routes) {
            if (shortestRoute == null || shortestRoute.size() > current.size()) {
                shortestRoute = current;
            }
        }
        
        /* 
         * Notify listeners that route calculation is complete if we found a 
         * local route.
         */
        if (shortestRoute != null) {
            for (RoutingWorkerListener current : listeners) {
                current.routeCalculationSucceeded(destination, shortestRoute);
            }
            /*
             * Check if backtracking is enabled and the destination wasn't the
             * origin.
             */
            if (usesBacktracking && destinationInstance != null) {
                destinationInstance.routeCalculationSucceeded(origin.getName(), 
                        shortestRoute.reverse());
            }
        } else {
            
            /* 
             * We didn't find a local route so we'll check with any SocketAgent
             * instances we encountered. We're now passing off the listeners to
             * the SocketAgent. Routing is no longer our responsibility.
             */
            for (Entry<SocketAgent, Route> current : socketAgentRoutePairs.entrySet()) {
                current.getKey().routeToRemote(destination, current.getValue(), 
                        listeners, usesBacktracking);
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
