package bthesis.provenancechain;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private List<Document> documents;
    private Document metadocument;

    private List<List<QualifiedName>> navigation_table;

    public DataHolder() {
        this.documents = new ArrayList<>();
        this.navigation_table = new ArrayList<>(new ArrayList<>());
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<File> documents) {
        InteropFramework inFm = new InteropFramework();
        for (File file : documents) {
            this.documents.add(inFm.readDocumentFromFile(file.getAbsolutePath()));
        }
    }

    public Document getMetadocument() {
        return metadocument;
    }

    public void setMetadocument(Document metadocument) {
        this.metadocument = metadocument;
    }

    public List<List<QualifiedName>> getNavigation_table() {
        return navigation_table;
    }

    public void setNavigation_table(List<List<QualifiedName>> navigation_table) {
        this.navigation_table = navigation_table;
    }
}
