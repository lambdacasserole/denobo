package denobo.socket;

import denobo.compression.Compressor;
import denobo.crypto.CryptoAlgorithm;
import denobo.socket.connection.ConnectionCredentialsHandler;

/**
 * A class for holding all the configuration options for a SocketAgent.
 *
 * @author Alex Mullen
 */
public class SocketAgentConfiguration {
    
    public int maximumConnections;
    public String password;
    public Compressor compression;
    public CryptoAlgorithm encryption;
    public ConnectionCredentialsHandler credentialsHandler;

    public SocketAgentConfiguration() {

    }
    
    public SocketAgentConfiguration(int maximumConnections) {
        this.maximumConnections = maximumConnections;
    }
    
}
