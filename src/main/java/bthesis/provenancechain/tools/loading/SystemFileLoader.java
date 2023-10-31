package bthesis.provenancechain.tools.loading;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.File;

/**
 * Implementation of the {@link IFileLoader} interface for loading files from the local file system.
 *
 * @author Tomas Zobac
 */
public class SystemFileLoader implements IFileLoader {
    /**
     * Loads a file from the local file system based on the provided path and returns it converted to a Document.
     *
     * @param path The path to the file on the local file system.
     * @return The loaded Document if the file's extension is supported, null otherwise.
     */
    @Override
    public Document loadFile(String path) {
        InteropFramework inFm = new InteropFramework();
        File file = new File(path.replaceAll("file:/+", ""));
        if (isSupportedExtension(file.getName())) return inFm.readDocumentFromFile(file.getAbsolutePath());
        else return null;
    }

    /**
     * Checks if the provided file name has a supported extension.
     *
     * @param name The name of the file.
     * @return True if the file's extension is supported, false otherwise.
     */
    @Override
    public boolean isSupportedExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        }
        String ext = name.substring(lastIndexOf + 1);
        return SupportedExtensions.isSupported(ext);
    }
}
