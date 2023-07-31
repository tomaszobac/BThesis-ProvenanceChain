package bthesis.provenancechain;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataHolder {
    private Map<QualifiedName,Document> documents;
    private Document metadocument;

    private List<List<QualifiedName>> navigation_table;

    public DataHolder() {
        this.documents = new LinkedHashMap<>();
        this.navigation_table = new ArrayList<>(new ArrayList<>());
    }

    public Map<QualifiedName,Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<File> documents) {
        InteropFramework inFm = new InteropFramework();
        for (File file : documents) {
            Document temp = inFm.readDocumentFromFile(file.getAbsolutePath());
            Bundle bundle = (Bundle) temp.getStatementOrBundle().get(0);
            this.documents.put(bundle.getId(),temp);
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
