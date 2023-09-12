package bthesis.metageneration;

import java.io.File;
import java.util.List;

/**
 * The GitFileLoader class implements the FileLoader interface to load files from a Git repository.
 * Currently, this class throws an UnsupportedOperationException as Git file loading is not implemented.
 *
 * @author Tomas Zobac
 */
public class GitFileLoader implements FileLoader {

    /**
     * Attempts to load files from a specified Git repository path.
     * Currently, this method throws an UnsupportedOperationException as it is not implemented.
     *
     * @param path The path to the directory in the Git repository from which files are to be loaded.
     * @return A list of File objects (not supported in the current implementation).
     * @throws UnsupportedOperationException When the method is invoked.
     */
    @Override
    public List<File> loadFiles(String path) {
        throw new UnsupportedOperationException("Git file loading not implemented yet.");
    }
}
