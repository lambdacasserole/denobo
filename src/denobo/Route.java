package denobo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a route that should be taken by a message to reach its
 * destination actor.
 * 
 * @author Saul Johnson
 */
public class Route {
   
    /**
     * The list of actors in the route in chronological order.
     */
    private List<String> route;

    /**
     * The position we're currently at in our route.
     */
    private int position;
    
    /**
     * Initialises a new instance of a route.
     */
    public Route() {
        route = new ArrayList<>();
        position = 0;
    }
    
    /**
     * Initialises a new instance of a route.
     * 
     * @param originatingActor  the originating actor
     */
    public Route(Agent originatingActor) {
        this();
        append(originatingActor);
    }
    
    /**
     * Initialises a new instance of a route.
     * 
     * @param route the route to clone
     */
    public Route(Route route) {
        this.route = new ArrayList(route.getActorList());
    }
    
    /**
     * Gets the list of actors that underlies this instance.
     * 
     * @return  the list of actors that underlies this instance
     */
    public List<String> getActorList() {
        return route;
    }
    
    /**
     * Appends an actor to the end of this route.
     * 
     * @param actor the actor to append
     */
    public final void append(Agent actor) {
        append(actor.getName());
    }
    
    /**
     * Appends an actor name to the end of this route.
     * 
     * @param name  the actor name to append
     */
    public final void append(String name) {
        route.add(name);
    }
    
    /**
     * Returns the name of the next actor in the route and increments the 
     * position by 1.
     * 
     * @return  the name of the next actor in the route
     */
    public String next() {
        final String nextName = route.get(position);
        position++;
        return nextName;
    }
    
    /**
     * Returns the name of the next actor in the route without incrementing the
     * position.
     * 
     * @return  the name of the next actor in the route
     */
    public String peek() {
        
        if (position == route.size()) {
            return null;
        }
        
        return route.get(position);
        
    }

    /**
     * Checks to see if this route already contains the specified agent instance.
     *
     * @param agent the agent instance to check for
     * @return      true if the agent is in the route, otherwise false
     */
    public boolean has(Agent agent) {
        return route.contains(agent.getName());
    }
    
    /**
     * Checks to see if this route already contains the specified agent name.
     *
     * @param agentName the name of the agent to check for
     * @return          true if the agent is in the route, otherwise false
     */
    public boolean has(String agentName) {
        return route.contains(agentName);
    }
    
    /**
     * Returns the number of members of this route.
     * 
     * @return  the number of members in the route
     */
    public int size() {
        return route.size();
    }
    
    /**
     * Returns the actor name at the start of the route.
     * 
     * @return  the actor name at the start of the route
     */
    public String first() {
        return route.get(0);
    }
    
    /**
     * Returns the actor name at the end of the route.
     * 
     * @return  the actor name at the end of the route
     */
    public String last() {
        return route.get(route.size() - 1);
    }
    
    /**
     * Sets the current position in the route.
     * 
     * @param position  the new position in the route
     */
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * Returns a serialised representation of this route.
     * 
     * @return  a serialised representation of this route
     */
    public String serialize() {
        final StringBuilder sb = new StringBuilder();
        for (String current : route) {
            sb.append(current).append(",");
        }
        if (sb.length() > 0) { sb.deleteCharAt(sb.length() - 1); }
        final QueryString queryString = new QueryString();
        queryString.add("position", Integer.toString(position));
        queryString.add("route", sb.toString());
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
        final Route queue = new Route();
        final String[] nameSplitter = queryString.get("route").split(",");
        for (String current : nameSplitter) {
            queue.append(current);
        }
        queue.setPosition(Integer.parseInt(queryString.get("position")));
        return queue;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String current : route) {
            sb.append(current).append(" -> \r\n");
        }
        sb.append("[End of Route]");
        return sb.toString();
    }
    
}
