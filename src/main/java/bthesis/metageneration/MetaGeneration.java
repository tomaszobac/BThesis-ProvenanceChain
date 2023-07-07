package bthesis.metageneration;

import org.openprovenance.prov.model.Document;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class MetaGeneration {
    List<TooManyDocuments> documents;

    public MetaGeneration(List<File> files) {
        this.documents = new ArrayList<>();
        setDocuments(files);
    }

    public MetaGeneration(File file) {
        this.documents = new ArrayList<>();
        this.documents.add(new TooManyDocuments(file));
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
        MetaBuilder meta = new MetaBuilder();
        return meta.makeDocument(getDocuments());
    }

}
