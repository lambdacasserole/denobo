package denobo.compression;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Saul Johnson
 */
public class FileUtils {
    
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
    
    public static void setFileBytes(File file, byte[] bytes) {
        
        final Path filepath = Paths.get(file.getAbsolutePath());
        
        try {
            Files.write(filepath, bytes);
        } catch (IOException ex) {
            System.err.println("Could not write file '" + filepath.toString() + "'.");
        }
        
    }
    
}
