package denobo.socket.connection;

/**
 * A class that represents some credentials for gaining access to a SocketAgent 
 * that has requested credentials before a session is allowed.
 *
 * @author Alex Mullen
 */
public class DenoboConnectionCredentials {
    
    /**
     * The password.
     */
    private final String password;
    
    /**
     * Instantiates a DenoboConnectionCredentials instance to represent some
     * credentials.
     * 
     * @param password the password
     */
    public DenoboConnectionCredentials(String password) {
        this.password = (password != null ? password : "");
    }
    
    /**
     * Returns the password part of these credentials.
     * <p>
     * The password is guaranteed not to be null. No password is represented as
     * "".
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
}
