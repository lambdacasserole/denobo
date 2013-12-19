package denobo;

/**
 * Implemented by any class that wants to receive events from a {@link NetworkPortal}.
 *
 * @author Alex Mullen
 */
public interface SocketAgentObserver {
    
    /**
     * Invoked whenever an incoming connection request is accepted.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param ip        The IP address of the remote host.
     * @param port      The port address the remote host is using.
     */
    public void incomingConnectionAccepted(SocketAgent agent, String ip, int port);
    
    /**
     * Invoked whenever a connection is closed or lost/dropped.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param ip        The IP address of the remote host.
     * @param port      The port address the remote host was using.
     */
    public void connectionClosed(SocketAgent agent, String ip, int port);
    
    /**
     * Invoked whenever a connection request to another network portal failed 
     * to connect.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param ip        The IP address of the remote host.
     * @param port      The port address of the remote host.
     */
    public void connectionAddFailed(SocketAgent agent, String ip, int port);
    
    /**
     * Invoked whenever a connection request to another network portal succeeded
     * in connecting.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param ip        The IP address of the remote host.
     * @param port      The port address of the remote host.
     */
    public void connectionAddSucceeded(SocketAgent agent, String ip, int port);
}