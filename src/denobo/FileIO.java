package denobo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Offers basic helper methods for reading and writing bytes and text to and 
 * from files.
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
    public static byte[] readBytesFromFile(File file) {
        
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
    public static void writeBytesToFile(File file, byte[] bytes) {
        
        final Path filepath = Paths.get(file.getAbsolutePath());
        
        try {
            Files.write(filepath, bytes);
        } catch (IOException ex) {
            System.err.println("Could not write file '" + filepath.toString() + "'.");
        }
        
    }

    /**
     * Returns the contents of a text file as a string.
     *
     * @param file  the file to read from
     * @return      the contents of the specified file as a string
     */
    public static String readTextFromFile(File file) {
        String fileText = null;
        try (final FileReader reader = new FileReader(file)) {
            final char[] buffer = new char[(int) file.length()];
            reader.read(buffer);
            fileText = new String(buffer);
        } catch (IOException e) {
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
    public static void writeTextToFile(File file, String content) {
        try (final PrintWriter writer = new PrintWriter(file)) {
            writer.print(content);
        } catch (IOException ex) {
            System.out.println("Cannot read from file.");
        }
    }
    
}
