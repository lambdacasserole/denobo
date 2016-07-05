package com.sauljohnson.denobo.socket;

import com.sauljohnson.denobo.compression.CompressionProvider;
import com.sauljohnson.denobo.compression.NoopCompressionProvider;
import com.sauljohnson.denobo.socket.connection.ConnectionCredentialsHandler;
import com.sauljohnson.denobo.socket.connection.Credentials;
import com.sauljohnson.denobo.socket.connection.DummyConnectionCredentialsHandler;

/**
 * A class for holding all the configuration options for a SocketAgent.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class SocketAgentConfiguration {

    /**
     * The maximum number of connections allowed for by this configuration.
     */
    private int maximumConnections;
    
    /**
     * The master set of credentials that connecting agents will be 
     * authenticated against under this configuration.
     */
    private Credentials masterCredentials;
    
    /**
     * The compression scheme used for packet compression by agents operating 
     * under this configuration.
     */
    private CompressionProvider compression;
    
    /**
     * Whether or not agents operating under this configuration will be 
     * encrypted.
     */
    private boolean isSecure;
    
    /**
     * The credentials handler used in case a remote peer asks for a
     * username and password.
     */
    private ConnectionCredentialsHandler credentialsHandler;

    /**
     * Initialises a new instance of a socket agent configuration class.
     */
    public SocketAgentConfiguration() {
        maximumConnections = Integer.MAX_VALUE;
        compression = new NoopCompressionProvider();
        isSecure = false;
        credentialsHandler = new DummyConnectionCredentialsHandler();
    }
    
    /**
     * Gets the maximum number of connections allowed for by this configuration.
     * 
     * @return  the maximum number of connections allowed for by this 
     *          configuration
     */
    public int getMaximumConnections() {
        return maximumConnections;
    }

    /**
     * Sets the maximum number of connections allowed for by this configuration.
     * 
     * @param maximumConnections    the maximum number of connections allowed 
     *                              for by this configuration
     */
    public void setMaximumConnections(int maximumConnections) {
        this.maximumConnections = (maximumConnections < 1 ? 1 
                : maximumConnections);
    }
    
    /**
     * Gets the master set of credentials that connecting agents will be
     * authenticated against under this configuration.
     * 
     * @return  the master set of credentials
     */
    public Credentials getMasterCredentials() {
        return masterCredentials;
    }
    
    /**
     * Sets the master set of credentials that connecting agents will be
     * authenticated against under this configuration.
     * 
     * @param credentials   the master set of credentials
     */
    public void setMasterCredentials(Credentials credentials) {
        this.masterCredentials = credentials;
    }

    /**
     * Gets the compression scheme used for packet compression by agents
     * operating under this configuration.
     * 
     * @return  the compression scheme used for packet compression
     */
    public CompressionProvider getCompression() {
        return compression;
    }

    /**
     * Sets the compression scheme used for packet compression by agents
     * operating under this configuration.
     * 
     * @param compression   the compression scheme to use for packet compression
     */
    public void setCompression(CompressionProvider compression) {
        this.compression = compression;
    }

    /**
     * Gets whether or not agents operating under this configuration will be 
     * encrypted.
     * 
     * @return  true if agents should be encrypted, otherwise false
     */
    public boolean getIsSecure() {
        return isSecure;
    }

    /**
     * Sets whether or not agents operating under this configuration will be 
     * encrypted.
     * 
     * @param isSecure  whether or not gents should be encrypted
     */
    public void setIsSecure(boolean isSecure) {
        this.isSecure = isSecure;
    }

    /**
     * Gets the credentials handler used in case a remote peer asks for a
     * username and password.
     * 
     * @return  the credentials handler used
     */
    public ConnectionCredentialsHandler getCredentialsHandler() {
        return credentialsHandler;
    }
    
    /**
     * Sets the credentials handler to use in case a remote peer asks for a
     * username and password.
     * 
     * @param credentialsHandler    the credentials handler to use
     */
    public void setCredentialsHandler(ConnectionCredentialsHandler credentialsHandler) {
        this.credentialsHandler = credentialsHandler;
    }
    
}
