package denobo.socket;

import denobo.socket.connection.DenoboConnection;

/**
 * Implemented by any class that wants to receive events from a {@link NetworkPortal}.
 *
 * @author Alex Mullen
 */
public interface SocketAgentObserver {
    
    /**
     * Invoked whenever a SocketAgent has started listening for connections.
     * Connections won't start to be accepted until this method for all listeners
     * have executed.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param port      The port on which the SocketAgent is listening for connections on.
     */
    public void advertisingStarted(SocketAgent agent, int port);
    
    /**
     * Invoked whenever a SocketAgent has stopped listening for connections.
     * From the moment this method is called, no more connections will be accepted.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param port      The port on which the SocketAgent was listening for connections on
     *                  but is no more.
     */
    public void advertisingStopped(SocketAgent agent, int port);
    
    /**
     * Invoked whenever an incoming connection request is accepted.
     * 
     * @param agent         The agent that is notifying this observer.
     * @param connection    The connection that was accepted.
     */
    public void incomingConnectionAccepted(SocketAgent agent, DenoboConnection connection);
    
    /**
     * Invoked whenever a connection is closed or lost/dropped.
     * 
     * @param agent         The agent that is notifying this observer.
     * @param connection    The connection that was closed.
     */
    public void connectionClosed(SocketAgent agent, DenoboConnection connection);
    
    /**
     * Invoked whenever a connection request to another socket agent failed 
     * to connect.
     * 
     * @param agent     The agent that is notifying this observer.
     * @param hostname  The hostname of the remote host we failed to connect to.
     * @param port      The port address of the remote host.
     */
    public void connectionAddFailed(SocketAgent agent, String hostname, int port);
    
    /**
     * Invoked whenever a connection request to another socket agent succeeded
     * in connecting.
     * 
     * @param agent         The agent that is notifying this observer.
     * @param connection    The resulting DenoboConnection object from successfully connecting.
     * @param hostname      The hostname of the remote host we connected to.
     * @param port          The port address of the remote host.
     */
    public void connectionAddSucceeded(SocketAgent agent, DenoboConnection connection, String hostname, int port);
}
