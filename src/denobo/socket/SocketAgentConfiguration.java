package denobo.socket;

import denobo.compression.Compressor;
import denobo.compression.DummyCompressor;
import denobo.crypto.CryptoAlgorithm;
import denobo.crypto.DummyCryptoAlgorithm;
import denobo.socket.connection.ConnectionCredentialsHandler;
import denobo.socket.connection.DummyConnectionCredentialsHandler;

/**
 * A class for holding all the configuration options for a SocketAgent.
 *
 * @author Alex Mullen, Saul Johnson
 */
public class SocketAgentConfiguration {

    public int maximumConnections;
    public Compressor compression;
    public CryptoAlgorithm encryption;
    public ConnectionCredentialsHandler credentialsHandler;

    public SocketAgentConfiguration() {
        maximumConnections = Integer.MAX_VALUE;
        compression = new DummyCompressor();
        encryption = new DummyCryptoAlgorithm();
        credentialsHandler = new DummyConnectionCredentialsHandler();
    }
    
    public int getMaximumConnections() {
        return maximumConnections;
    }

    public void setMaximumConnections(int maximumConnections) {
        this.maximumConnections = (maximumConnections < 1 ? 1 : 
                maximumConnections);
    }

    public Compressor getCompression() {
        return compression;
    }

    public void setCompression(Compressor compression) {
        this.compression = compression;
    }

    public CryptoAlgorithm getEncryption() {
        return encryption;
    }

    public void setEncryption(CryptoAlgorithm encryption) {
        this.encryption = encryption;
    }

    public ConnectionCredentialsHandler getCredentialsHandler() {
        return credentialsHandler;
    }

    public void setCredentialsHandler(ConnectionCredentialsHandler credentialsHandler) {
        this.credentialsHandler = credentialsHandler;
    }
    
}
