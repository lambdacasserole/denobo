package denobo;

import denobo.socket.SocketAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Represents a worker that will calculate the optimal route to an actor.
 * 
 * @author Saul Johnson, Alex Mullen, Lee Oliver
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
     * The listening observers that will be notified when optimal route
     * calculation is complete.
     */
    private final List<RoutingWorkerListener> listeners;
    
    /**
     * The existing route to append to.
     */
    private final Route initialRoute;
    
    /**
     * Initialises a new instance of a routing worker.
     * 
     * @param origin        the actor from which this worker will start
     * @param destination   the name of the destination actor
     */
    public RoutingWorker(Agent origin, String destination) {
        this(origin, destination, new Route());
    }
    
    /**
     * Initialises a new instance of a routing worker.
     *
     * @param origin        the actor from which this worker will start
     * @param destination   the name of the destination actor
     * @param initialRoute  the existing route to append to
     */
    public RoutingWorker(Agent origin, String destination, Route initialRoute) {
        this.origin = origin;
        this.destination = destination;
        this.initialRoute = initialRoute;
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
        
        // Recursively map routes to destination.
        final Route queue = new Route(initialRoute);
        queue.append(origin);
        
        if (origin.getName().equals(destination)) {
            routes.add(queue);
        } else {
            route(origin, queue);
        }
        
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
        } else {
            
            /* 
             * We didn't find a local route so we'll check with any SocketAgent
             * instances we encountered. We're now passing off the listeners to
             * the SocketAgent. Routing is no longer our responsibility.
             */
            for (Entry<SocketAgent, Route> current : socketAgentRoutePairs.entrySet()) {
               current.getKey().routeToRemote(destination, current.getValue(), listeners);
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
