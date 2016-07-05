package com.sauljohnson.denobo.socket.connection;

/**
 * Represents a ConnectionCredentialsHandler that will always refuse to give
 * credentials.
 * 
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DummyConnectionCredentialsHandler implements ConnectionCredentialsHandler {

    public Credentials credentialsRequested(DenoboConnection connection) {
        return null;
    }
    
}
