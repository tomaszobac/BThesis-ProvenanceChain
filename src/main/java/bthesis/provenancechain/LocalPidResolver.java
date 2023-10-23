package bthesis.provenancechain;

import java.util.List;
import java.util.ArrayList;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

/**
 * The DataHolder class is responsible for holding and managing the data structures
 * that store the provenance documents, navigation table, and meta-document.
 *
 * @author Tomas Zobac
 */
public class LocalPidResolver implements IPidResolver {
    private final List<List<QualifiedName>> navigation_table;
    private Document metadocument;

    /**
     * Initializes a new instance of the DataHolder class.
     * Constructs empty data structures for documents and the navigation table.
     */
    public LocalPidResolver() {
        this.navigation_table = new ArrayList<>(new ArrayList<>()); //TODO: dát do interface + předělat na colection colection + gettable do interface
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
    @Override
    public List<List<QualifiedName>> getNavigation_table() {
        return navigation_table;
    }

    /**
     * Resolves the entity by finding its row in the navigation table.
     *
     * @param entity_id   The QualifiedName identifier of the entity.
     * @param entity_type The QualifiedName representing the type of the entity.
     * @return The row from the navigation table as a list of QualifiedName objects.
     */
    @Override
    public List<QualifiedName> resolve(QualifiedName entity_id, QualifiedName entity_type) {
        for (List<QualifiedName> row : this.navigation_table) {
            if (row.get(0).equals(entity_id) && row.get(1).equals(entity_type)) {
                return row;
            }
        }
        return null;
    }

    public QualifiedName getMetaDoc(QualifiedName externalConnector, QualifiedName bundle_id) {
        for (List<QualifiedName> row : this.navigation_table) {
            if (row.get(1).equals(externalConnector) && row.get(2).equals(bundle_id)) {
                return row.get(3);
            }
        }
        return null;
    }

    /**
     * Checks if a given entity is a connector.
     *
     * @param entity_id The QualifiedName identifier of the entity.
     * @return True if the entity is a connector, false otherwise.
     */
    @Override
    public boolean isConnector(QualifiedName entity_id) {
        for (List<QualifiedName> row : this.navigation_table) {
            if (row.get(0).equals(entity_id)) {
                return true;
            }
        }
        return false;
    }
}
