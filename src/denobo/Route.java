package denobo;

import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import static java.util.Objects.requireNonNull;

/**
 * Represents a route that should be taken by a message to reach its
 * destination actor.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Route {
    
    /**
     * The list of property names used for serialising an object of this type
     * to a query string.
     */
    private static final String[] PROPERTY_NAMES = new String[] {"path", "position"};
    
    /**
     * The ordered list of agent names in the route.
     */
    private List<String> path;

    /**
     * The position we're currently at in our route.
     */
    private int position;
    
    
    /* ---------- */
    
    
    /**
     * Initialises a new instance of a route.
     * 
     * @param path      the list of agent names that make up the route
     * @param position  the current position a message is at in the route
     */
    private Route(List<String> path, int position) {
        requireNonNull(path, "List of agent names cannot be null.");
        this.path = path;
        this.position = position;
    }
    
    /**
     * Initialises a new instance of a route.
     */
    public Route() {
        this(new ArrayList<String>(), 0);
    }
    
    /**
     * Initialises a new instance of a route.
     * 
     * @param route  the route to clone
     */
    public Route(Route route) {
        requireNonNull(route, "Route to clone cannot be null.");
        path = new ArrayList<>(route.path);
        position = route.position;
    }
    
    
    /* ---------- */
    
    
    /**
     * Gets the list of agent names that underlies this instance.
     * 
     * @return  the list of agent names that underlies this instance
     */
    public List<String> getPath() {
        return unmodifiableList(path);
    }
    
    /**
     * Appends an agent to the end of this route.
     * 
     * @param actor the agent to append
     */
    public final void append(Agent actor) {
        requireNonNull(actor, "Agent to append cannot be null.");
        path.add(actor.getName());
    }
    
    /**
     * Appends an actor name to the end of this route.
     * 
     * @param name  the actor name to append
     */
    public final void append(String name) {
        requireNonNull(name, "Agent name to append cannot be null.");
        if (path.contains(name)) {
            throw new DuplicateAgentNameException(this);
        }
        path.add(name);
    }
    
    /**
     * Gets whether or not this route has another entry.
     * 
     * @return  true if the route has another entry, otherwise false
     */
    public boolean hasNext() {
        return position < path.size();
    }
    
    /**
     * Returns the name of the next actor in the route and increments the 
     * position by 1.
     * 
     * @return  the name of the next actor in the route
     */
    public String next() {
        if (!hasNext()) {
            throw new EndOfRouteException(this);
        }
        return path.get(position++);
    }
    
    /**
     * Returns the name of the next actor in the route without incrementing its
     * position.
     * 
     * @return  the name of the next actor in the route or null if the end of
     *          the route has been reached
     */
    public String peek() {
        return hasNext() ? path.get(position) : null;
    }

    /**
     * Checks to see if this route already contains the specified agent.
     *
     * @param agent the agent to check for
     * @return      true if the agent is in the route, otherwise false
     */
    public boolean has(Agent agent) {
        return path.contains(agent.getName());
    }
    
    /**
     * Checks to see if this route already contains the specified agent name.
     *
     * @param agentName the name of the agent to check for
     * @return          true if the agent name is in the route, otherwise false
     */
    public boolean has(String agentName) {
        return path.contains(agentName);
    }
    
    /**
     * Returns whether or not this route is empty.
     * 
     * @return  true if the route is empty, otherwise false
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * Returns the number of agents in this route.
     * 
     * @return  the number of agents in the route
     */
    public int size() {
        return path.size();
    }
    
    /**
     * Returns the actor name at the start of the route.
     * 
     * @return  the actor name at the start of the route or null if the route is
     *          empty
     */
    public String first() {
        return isEmpty() ? null : path.get(0);
    }
    
    /**
     * Returns the actor name at the end of the route.
     * 
     * @return  the actor name at the end of the route or null if the route is
     *          empty
     */
    public String last() {
        return isEmpty() ? null : path.get(size() - 1);
    }
    
    /**
     * Returns a serialised representation of this route.
     * 
     * @return  a serialised representation of this route
     */
    public String serialize() {
        final QueryString queryString = new QueryString();
        queryString.addAsCollection(PROPERTY_NAMES[0], path);
        queryString.add(PROPERTY_NAMES[1], Integer.toString(position));
        return queryString.toString();
    }
    
    /**
     * Deserializes a route out of a string and returns it.
     * 
     * @param string    the string from which to deserialize the route
     * @return          a route instance
     */
    public static Route deserialize(String string) {
        final QueryString queryString = new QueryString(string);
        if (queryString.has(PROPERTY_NAMES)) {
            try {
                final Route route = new Route(queryString.getAsList(PROPERTY_NAMES[0]), 
                        Integer.parseInt(queryString.get(PROPERTY_NAMES[1])));
                return route;
            } catch (NumberFormatException ex) {
                throw new InvalidQueryStringException("A route could not be"
                        + " deserialised from this query string. Invalid position.",
                        queryString);
            }
        } else {
            throw new InvalidQueryStringException("A route could not be"
                    + " deserialised from this query string. Invalid keys.", 
                    queryString);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String current : path) {
            sb.append(current).append(!current.equals(last()) ? " -> " : "");
        }
        return sb.toString();
    }
    
}
