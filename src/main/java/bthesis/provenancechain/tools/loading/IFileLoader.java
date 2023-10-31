package bthesis.provenancechain.tools.loading;

import org.openprovenance.prov.model.Document;

/**
 * Represents an interface for loading files based on provided paths.
 *
 * @author Tomas Zobac
 */
public interface IFileLoader {
    /**
     * Loads a file based on the given path and returns it converted to a Document.
     *
     * @param path The path to the file to be loaded.
     * @return The loaded Document.
     */
    Document loadFile(String path);

    /**
     * Checks if the provided file name has a supported extension.
     *
     * @param name The name of the file.
     * @return True if the file's extension is supported, false otherwise.
     */
    boolean isSupportedExtension(String name);
}
