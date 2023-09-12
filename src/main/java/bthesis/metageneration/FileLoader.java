package bthesis.metageneration;

import java.io.File;
import java.util.List;

/**
 * The FileLoader interface defines the method for loading files from a specified path.
 *
 * @author Tomas Zobac
 */
public interface FileLoader {

    /**
     * Loads files from the specified path and returns them as a list of File objects.
     *
     * @param path The path to the directory from which files are to be loaded.
     * @return A list of File objects representing the files in the specified directory.
     */
    List<File> loadFiles(String path);
}
