package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

public class WriteDocument {
    String path;
    Document meta;

    public WriteDocument(String path, Document document) {
        this.path = path;
        this.meta = document;
    }

    public void writeDocument(){
        InteropFramework framework = new InteropFramework();
        framework.writeDocument(path, meta);
    }
}
