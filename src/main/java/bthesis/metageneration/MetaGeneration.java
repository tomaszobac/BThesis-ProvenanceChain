package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class MetaGeneration {
    List<TooManyDocuments> documents;

    public MetaGeneration(List<File> files) {
        setDocuments(files);
    }

    public void setDocuments(List<File> files) {
        for (File file : files) {
            this.documents.add(new TooManyDocuments(file));
        }
    }

    public List<TooManyDocuments> getDocuments() {
        return documents;
    }

    public Document generate() throws IOException, NoSuchAlgorithmException {
        HashDocument hasher = new HashDocument();
        hasher.addHashes(getDocuments());

        return null;//metadocument;
    }

}
