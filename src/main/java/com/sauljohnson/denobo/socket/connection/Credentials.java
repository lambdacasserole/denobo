package com.sauljohnson.denobo.socket.connection;

import com.sauljohnson.denobo.QueryString;

/**
 * A class that represents some credentials for gaining access to a SocketAgent 
 * that has requested credentials before a session is allowed.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class Credentials {
    
    /**
     * The username.
     */
    private final String username;
    
    /**
     * The password.
     */
    private final String password;
    
    /**
     * Instantiates a DenoboConnectionCredentials instance to represent some
     * credentials.
     * 
     * @param username  the username
     * @param password  the password
     */
    public Credentials(String username, String password) {
        this.username = (username != null ? username : "");
        this.password = (password != null ? password : "");
    }
    
    /**
     * Creates a new set of credentials based on a query string.
     * <p>
     * Query string entries for keys "password" and "username" will be taken
     * to initialise the new set of credentials.
     * 
     * @param string    the query string from which to initialise
     * @return          a new credentials instance
     */
    public static Credentials parse(String string) {
        final QueryString queryString = new QueryString(string);
        return new Credentials(queryString.get("username"), 
                queryString.get("password"));
    }
    
    /**
     * Returns the password part of these credentials.
     * <p>
     * The password is guaranteed not to be null. No password is represented as
     * an empty string.
     * 
     * @return  the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Returns the username part of these credentials.
     * <p>
     * The username is guaranteed not to be null. No username is represented as
     * an empty string.
     * 
     * @return  the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Validates a set of credentials against another.
     * 
     * @param master        the set of credentials to authenticate against
     * @param credentials   the set of credentials to authenticate
     * @return              true if the credentials matched, otherwise false
     */
    public static boolean validate(Credentials master, Credentials credentials) {
        return master.toString().equals(credentials.toString());
    }
    
    @Override
    public String toString() {
        final QueryString queryString = new QueryString();
        queryString.add("username", username);
        queryString.add("password", password);
        return queryString.toString();
    }
    
}
