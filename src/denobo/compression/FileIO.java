package denobo.compression;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Offers basic helper methods for reading and writing bytes to and from files.
 * 
 * @author Saul Johnson
 */
public class FileIO {
    
    /**
     * Returns the contents of a file as a byte array.
     * 
     * @param file  the file to read from
     * @return      the contents of the file as a byte array
     */
    public static byte[] getFileBytes(File file) {
        
        final Path filepath = Paths.get(file.getAbsolutePath());
        
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(filepath);
        } catch (IOException ex) {
            System.err.println("Could not read file '" + filepath.toString() + "'.");
        }
        
        return bytes;
        
    }
    
    /**
     * Writes a byte array to a file.
     * 
     * @param file  the file to write the array to
     * @param bytes the bytes to write to the file
     */
    public static void setFileBytes(File file, byte[] bytes) {
        
        final Path filepath = Paths.get(file.getAbsolutePath());
        
        try {
            Files.write(filepath, bytes);
        } catch (IOException ex) {
            System.err.println("Could not write file '" + filepath.toString() + "'.");
        }
        
    }
    
}
