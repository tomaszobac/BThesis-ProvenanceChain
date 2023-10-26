package bthesis.provenancechain.tools.loading;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.File;

/**
 * The SystemFileLoader class implements the FileLoader interface and provides
 * functionality to load files from the local file system.
 *
 * @author Tomas Zobac
 */
public class SystemFileLoader implements IFileLoader {
    /**
     * Initializes a new instance of the SystemFileLoader class.
     * Calls the loadFiles method to populate the internal files list.
     */
    public SystemFileLoader() {
    }

    /**
     * Returns the list of files loaded from the specified directory.
     *
     * @return A list of File objects representing the loaded files.
     */

    @Override
    public Document loadFile(String path) {
        InteropFramework inFm = new InteropFramework();
        File file = new File(path.replaceAll("file:/+", ""));
        if (isSupportedExtension(file.getName())) return inFm.readDocumentFromFile(file.getAbsolutePath());
        else return null;
    }

    /**
     * Checks if a given file has a supported extension.
     *
     * @return A boolean value indicating whether the file has a supported extension.
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
