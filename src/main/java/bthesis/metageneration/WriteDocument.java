package bthesis.metageneration;

import java.nio.file.Paths;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.interop.InteropFramework;

/**
 * The WriteDocument class is responsible for writing a Document object to a specified file path.
 *
 * @author Tomas Zobac
 */
public class WriteDocument {
    String path;
    Document meta;

    /**
     * Initializes a new instance of the WriteDocument class.
     *
     * @param path     The path where the document will be written.
     * @param document The Document object that will be written.
     */
    public WriteDocument(String path, Document document) {
        this.path = path;
        this.meta = document;
    }

    /**
     * Returns the file path where the Document object will be written.
     *
     * @return A string representing the file path.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Sets the file path where the Document object will be written.
     *
     * @param path A string representing the new file path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the Document object that will be written.
     *
     * @return The Document object.
     */
    public Document getMeta() {
        return this.meta;
    }

    /**
     * Adjusts the file path to include the system-specific separator if not present,
     * and appends the file name "meta_provenance.provn" to the path.
     */
    private void fixPath() {
        char lastChar = this.path.charAt(this.path.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            String separator = Paths.get(this.path).getFileSystem().getSeparator();
            setPath(this.path + separator);
        }
        setPath(this.path + "meta_provenance.provn");
    }

    /**
     * Writes the Document object to the specified file path.
     */
    public void writeDocument() {
        InteropFramework framework = new InteropFramework();
        fixPath();
        framework.writeDocument(this.path, getMeta());
    }
}
