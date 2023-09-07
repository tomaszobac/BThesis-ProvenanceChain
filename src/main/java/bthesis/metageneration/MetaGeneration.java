package bthesis.metageneration;

import org.openprovenance.prov.model.Document;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class MetaGeneration {
    List<DocInfoExtender> documents;

    public MetaGeneration() {
        this.documents = new ArrayList<>();
    }

    public List<DocInfoExtender> getDocuments() {
        return documents;
    }

    public Document generate(HashDocument hasher, MetaBuilder meta, List<File> files) throws NoSuchAlgorithmException {
        for (File file : files) {
            this.documents.add(new DocInfoExtender(hasher, file));
        }
        return meta.makeDocument(getDocuments());
    }

}
