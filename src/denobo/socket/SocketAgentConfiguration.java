package denobo.socket;

import denobo.compression.Compressor;
import denobo.compression.DummyCompressor;
import denobo.socket.connection.ConnectionCredentialsHandler;
import denobo.socket.connection.Credentials;
import denobo.socket.connection.DummyConnectionCredentialsHandler;

/**
 * A class for holding all the configuration options for a SocketAgent.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public class SocketAgentConfiguration {

    private int maximumConnections;
    private Credentials masterCredentials;
    private Compressor compression;
    private boolean isSecure;
    private ConnectionCredentialsHandler credentialsHandler;

    public SocketAgentConfiguration() {
        maximumConnections = Integer.MAX_VALUE;
        compression = new DummyCompressor();
        isSecure = false;
        credentialsHandler = new DummyConnectionCredentialsHandler();
    }
    
    public int getMaximumConnections() {
        return maximumConnections;
    }

    public void setMaximumConnections(int maximumConnections) {
        this.maximumConnections = (maximumConnections < 1 ? 1 : 
                maximumConnections);
    }
    
    public void setCredentials(Credentials credentials) {
        this.masterCredentials = credentials;
    }
    
    public Credentials getMasterCredentials() {
        return masterCredentials;
    }

    public Compressor getCompression() {
        return compression;
    }

    public void setCompression(Compressor compression) {
        this.compression = compression;
    }

    public boolean getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(boolean isSecure) {
        this.isSecure = isSecure;
    }

    public ConnectionCredentialsHandler getCredentialsHandler() {
        return credentialsHandler;
    }

    public void setCredentialsHandler(ConnectionCredentialsHandler credentialsHandler) {
        this.credentialsHandler = credentialsHandler;
    }
    
}
