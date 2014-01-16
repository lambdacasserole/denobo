package denobo.socket.connection;

/**
 * An interface that defines a way to handle a remote connection asking us for
 * some credentials.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface ConnectionCredentialsHandler {
    
    /**
     * Invoked when a remote connection is requesting credentials from us before
     * they will allow us access.
     * 
     * @param connection    the connection represented by a DenoboConnection instance
     *                      that is requesting credentials
     * @return              implementations need to return an instance of a
     *                      DenoboConnectionCredentials object that contains the
     *                      credentials to use, or null can be returned if the
     *                      implementation wishes to refuse to give credentials
     */
    public Credentials credentialsRequested(DenoboConnection connection);
    
}
