package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import java.nio.file.Paths;

public class WriteDocument {
    String path;
    Document meta;

    public WriteDocument(String path, Document document) {
        this.path = path;
        this.meta = document;
    }

    public String getPath() {
        return path;
    }

    public Document getMeta() {
        return meta;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void fixPath() {
        char lastChar = getPath().charAt(getPath().length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            String separator = Paths.get(getPath()).getFileSystem().getSeparator();
            setPath(getPath() + separator);
        }
        setPath(getPath() + "meta_provenance.provn");
    }

    public void writeDocument(){
        InteropFramework framework = new InteropFramework();
        fixPath();
        framework.writeDocument(getPath(), getMeta());
    }
}
