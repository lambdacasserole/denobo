package denobo.compression;

import denobo.compression.huffman.BitInputStream;
import denobo.compression.huffman.BitOutputStream;
import denobo.compression.huffman.BitSequence;
import denobo.compression.huffman.ByteFrequencySet;
import denobo.compression.huffman.FrequencyTree;
import denobo.compression.huffman.PrefixCodeTable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A compressor that uses plain Huffman coding to compress a set of bytes.
 * 
 * @author Saul Johnson
 */
public class BasicCompressor implements Compressor {
    
    @Override
    public byte[] compress(byte[] data) {
        try {
        
            //  Calculate translation table.
            final ByteFrequencySet frequencies = new ByteFrequencySet(data);
            final FrequencyTree tree = FrequencyTree.fromFrequencySet(frequencies);
            final PrefixCodeTable table = new PrefixCodeTable(tree);
            
            // Compress data.
            final BitOutputStream bitOut = new BitOutputStream();
            for (int i = 0; i < data.length; i++) {
                bitOut.write(table.translateSymbol(data[i] & 0xFF));
            }
            
            // Express length in bits as 4-byte array.
            final byte[] lengthInBits = ByteBuffer.allocate(4)
                    .putInt(bitOut.length()).array();
            
            // Final output.
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            out.write(lengthInBits); // Write length in bits.
            
            // Get symbols and codes in translation table.
            final int[] symbols = table.getSymbols();
            final BitSequence[] codes = table.getCodes();
            
            // Serialise table to output.
            for (int i = 0; i < symbols.length; i++) {
                
                final byte[] codeBytes = codes[i].toArray();
                
                out.write(i == symbols.length - 1 ? 0xFF : 0x00);
                out.write((byte) symbols[i]);
                out.write((byte) codes[i].getLength());
                out.write(codeBytes);
                
            }
            
            // Write payload to output and close stream.
            out.write(bitOut.toArray());
            out.close();
                        
            System.out.println(table.toString());
            
            // Return compressed data, complete with table and header.
            return out.toByteArray();
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println("Could not compress data.");
            return null;
            
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        try {
            
            final ByteArrayInputStream in = new ByteArrayInputStream(data);
            
            final byte[] lengthBytes = new byte[4];
            in.read(lengthBytes);
            
            final int lengthInBits = ByteBuffer.allocate(4).put(lengthBytes).getInt(0);
            
            // Create lists of symbols and codes.
            final List<Integer> symbols = new ArrayList<>();
            final List<BitSequence> codes = new ArrayList<>();
            
            // Deserialise table.
            boolean exitFlag;
            do {
                
                // Header byte is 0x00, of 0xFF if this is the last entry in the serialised table.
                final int headerByte = in.read();
                exitFlag = (headerByte == 0xFF);
                
                // Get symbol and code length in bits.
                final int symbol = in.read();
                final int codeLength = in.read();
                
                // Read code bytes.
                final byte[] codeBytes = new byte[(int) Math.ceil((double) codeLength / 8.00d)];
                in.read(codeBytes);
                
                // Read code into bit sequence.
                final BitSequence code = new BitSequence();
                final BitInputStream codeReader = new BitInputStream(codeBytes);
                for (int i = 0; i < codeLength; i++) {
                    code.append(codeReader.read());
                }
                
                // Add code and symbol to lists.
                symbols.add(symbol);
                codes.add(code);
                 
            } while (!exitFlag);
            
            // Convert to primitive array.
            final int[] primitiveSymbols = new int[symbols.size()];
            for (int i = 0; i < symbols.size(); i++) {
                primitiveSymbols[i] = symbols.get(i).intValue();
            }
            
            // Initialise translation table from codes and symbols.
            final PrefixCodeTable table = new PrefixCodeTable(primitiveSymbols, 
                    codes.toArray(new BitSequence[] {}));
            
            // Get compressed data payload.
            final byte[] compressedData = new byte[in.available()];
            in.read(compressedData);
            
            in.close();
            
            final BitInputStream bitIn = new BitInputStream(compressedData);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            BitSequence buffer = new BitSequence();
            while (bitIn.getPosition() < lengthInBits) {
                buffer.append(bitIn.read());
                if (table.hasCode(buffer)) {
                    out.write(table.translateCode(buffer));
                    buffer = new BitSequence();
                }
            }
            
            return out.toByteArray();
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println("Could not decompress data.");
            return null;
            
        }
    }
    
}
