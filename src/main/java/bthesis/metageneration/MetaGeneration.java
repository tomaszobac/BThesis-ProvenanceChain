package bthesis.metageneration;

import org.openprovenance.prov.model.Document;

import java.util.Map;

public class MetaGeneration {
    SystemFileLoader inputFileLoader;
    Map<Document, String> documents;
    Document metadocument;

    public MetaGeneration(SystemFileLoader inputFileLoader) {
        this.inputFileLoader = inputFileLoader;
    }

    public Document getMetadocument() {
        return metadocument;
    }

    public void generate() {

    }

}
