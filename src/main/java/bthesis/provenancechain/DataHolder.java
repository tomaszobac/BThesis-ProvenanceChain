package bthesis.provenancechain;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.vanilla.ProvFactory;

/**
 * The DataHolder class is responsible for holding and managing the data structures
 * that store the provenance documents, navigation table, and meta-document.
 *
 * @author Tomas Zobac
 */
public class DataHolder {
    private final Map<QualifiedName, Document> documents;
    private final List<List<QualifiedName>> navigation_table;
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    private final QualifiedName externalInputConnector;
    private Document metadocument;

    /**
     * Initializes a new instance of the DataHolder class.
     * Constructs empty data structures for documents and the navigation table.
     */
    public DataHolder() {
        this.documents = new LinkedHashMap<>();
        this.navigation_table = new ArrayList<>(new ArrayList<>());
        ProvFactory provFactory = new ProvFactory();
        Configuration config = ConfigLoader.loadConfig();
        this.mainActivity = provFactory.newQualifiedName(config.cpmUri, config.mainActivity, null);
        this.receiverConnector = provFactory.newQualifiedName(config.cpmUri, config.receiverConnector, null);
        this.senderConnector = provFactory.newQualifiedName(config.cpmUri, config.senderConnector, null);
        this.externalInputConnector = provFactory.newQualifiedName(config.cpmUri, config.externalConnector, null);
    }

    /**
     * Returns the map of QualifiedName to Document objects representing the provenance documents.
     *
     * @return A map of QualifiedName to Document objects.
     */
    public Map<QualifiedName, Document> getDocuments() {
        return documents;
    }

    /**
     * Populates the document map based on the list of File objects passed.
     * Reads each file and extracts its bundle ID and document to populate the map.
     *
     * @param documents A list of File objects representing the provenance documents.
     */
    public void setDocuments(List<File> documents) {
        InteropFramework inFm = new InteropFramework();
        for (File file : documents) {
            Document temp = inFm.readDocumentFromFile(file.getAbsolutePath());
            Bundle bundle = (Bundle) temp.getStatementOrBundle().get(0);
            this.documents.put(bundle.getId(), temp);
        }
    }

    /**
     * Returns the meta-document that contains metadata about the provenance documents.
     *
     * @return A Document object representing the meta-document.
     */
    public Document getMetadocument() {
        return metadocument;
    }

    /**
     * Sets the meta-document that contains metadata about the provenance documents.
     *
     * @param metadocument A Document object representing the meta-document.
     */
    public void setMetadocument(Document metadocument) {
        this.metadocument = metadocument;
    }

    /**
     * Returns the navigation table that aids in navigating through the provenance documents.
     *
     * @return A list of lists of QualifiedName objects representing the navigation table.
     */
    public List<List<QualifiedName>> getNavigation_table() {
        return navigation_table;
    }

    public QualifiedName getMainActivity() {
        return mainActivity;
    }

    public QualifiedName getReceiverConnector() {
        return receiverConnector;
    }

    public QualifiedName getSenderConnector() {
        return senderConnector;
    }

    public QualifiedName getExternalInputConnector() {
        return externalInputConnector;
    }
}
