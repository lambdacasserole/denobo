package com.sauljohnson.denobo.socket.connection;

import com.sauljohnson.denobo.QueryString;
import com.sauljohnson.denobo.compression.CompressionProvider;
import com.sauljohnson.denobo.compression.NoopCompressionProvider;
import com.sauljohnson.denobo.encryption.EncryptionProvider;
import com.sauljohnson.denobo.encryption.NoopEncryptionProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;

/**
 * An implementation of PacketSerializer for serializing {@link Packet} objects used in
 * Denobo.
 *
 * @author  Saul Johnson, Alex Mullen, Lee Oliver
 */
public class DenoboPacketSerializer implements PacketSerializer {

    /**
     * The encryption algorithm used for packet I/O.
     */
    private EncryptionProvider crypto;
    
    /**
     * The compression algorithm used for packet I/O.
     */
    private CompressionProvider compressor;
    
    /**
     * Initialises a new instance of a packet serialiser designed to work with
     * the Denobo protocol.
     */
    public DenoboPacketSerializer() {
        crypto = new NoopEncryptionProvider();
        compressor = new NoopCompressionProvider();
    }

    public void writePacket(Writer writer, Packet packet) throws IOException {
        
        // Packet start token.
        writer.write("@");
        
        // Build query string.
        final QueryString queryString = new QueryString();
        queryString.add("code", String.valueOf(packet.getCode().toInt()));
        queryString.add("body", packet.getBody());
        
        // Compress and encrypt.
        final byte[] compressedText = compressor.compress(queryString.toString().getBytes("US-ASCII"));
        final byte[] ciphertext = crypto.encrypt(compressedText);
        final String byteString = DatatypeConverter.printBase64Binary(ciphertext);
        
        // Write packet.
        writer.write(byteString);
        
        // Packet end token.
        writer.write("$");
        
        writer.flush();
        
    }

    public Packet readPacket(Reader reader) throws IOException, StreamCorruptedException {
        
        // Read until starting token.
        int currentCharacter;
        do {
            
            // Read the next character received or block waiting
            currentCharacter = reader.read();
            
            /* 
             * Check for the end of the stream which indicates that a
             * connection has been closed.
             */
            if (currentCharacter == -1) {
                return null;
            }
            
            /* 
             * Read until we hit an '@' sign.
             */
            
        } while (currentCharacter != '@');

        // Read string up until ending token.
        int buffer;
        final StringBuilder sb = new StringBuilder(256);	
        while ((buffer = reader.read()) != '$') {
            sb.append((char) buffer);
        }

        // Decrypt and decompress.
        final byte[] ciphertext = DatatypeConverter.parseBase64Binary(sb.toString());
        final byte[] compressedText = crypto.decrypt(ciphertext);
        final byte[] plaintext = compressor.decompress(compressedText);

        final QueryString queryString = new QueryString(new String(plaintext, "US-ASCII"));
        final String code = queryString.get("code");
        final String body = queryString.get("body");

        // Convert and validate the packet code into an integer
        PacketCode packetCode = null;
        try {
            packetCode = PacketCode.valueOf(Integer.parseInt(code));
        } catch (NumberFormatException e) {
            throw new StreamCorruptedException("Packet code was an invalid number: " + code);
        }
        
        // Make sure the packet code is recognized
        if (packetCode == null) {
            throw new StreamCorruptedException("Invalid packet code: " + code);
        }

        return new Packet(packetCode, body);
        
    }

    public void setCompressor(CompressionProvider compressor) {
        Objects.requireNonNull(crypto, "The compression algorithm cannot be null.");
        this.compressor = compressor;
    }

    public void setCryptoAlgorithm(EncryptionProvider crypto) {
        Objects.requireNonNull(crypto, "The encryption algorithm cannot be null.");
        this.crypto = crypto;
    }
    
}
