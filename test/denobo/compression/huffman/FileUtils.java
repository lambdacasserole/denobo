package denobo.compression.huffman;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Provides helper methods for reading from and writing to text files.
 * 
 * @author Saul Johnson
 */
public class FileUtils {
 
    /**
     * Returns the contents of a text file as a string.
     * 
     * @param file  the file to read from
     * @return      the contents of the specified file as a string
     */
    public static String readFile(File file) {
        String fileText = null;
        try (final FileReader reader = new FileReader(file)) {
            
            final char[] buffer = new char[(int) file.length()];
            reader.read(buffer);
            fileText = new String(buffer);
            
        } catch (IOException e) {
            
            // TODO: Handle exception.
            System.out.println("Cannot read from file.");
            
        }
        return fileText;
     }
    
    /**
     * Writes the specified string to a file.
     * 
     * @param file      the file to write to
     * @param content   the string to write
     */
    public static void writeFile(File file, String content) {
        try (final PrintWriter writer = new PrintWriter(file)) {
            
            writer.print(content);
            
        } catch (IOException ex) {
            
            // TODO: Handle exception.
            System.out.println("Cannot read from file.");
            
        }
    }
    
    
}
