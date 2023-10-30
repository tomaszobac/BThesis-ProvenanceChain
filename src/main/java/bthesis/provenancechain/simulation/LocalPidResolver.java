package bthesis.provenancechain.simulation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import bthesis.provenancechain.tools.metadata.IPidResolver;
import org.openprovenance.prov.model.QualifiedName;

/**
 * The DataHolder class is responsible for holding and managing the data structures
 * that store the provenance documents, navigation table, and meta-document.
 *
 * @author Tomas Zobac
 */
public class LocalPidResolver implements IPidResolver {
    private final List<Map<String, QualifiedName>> navigationTable;

    /**
     * Initializes a new instance of the DataHolder class.
     * Constructs empty data structures for documents and the navigation table.
     */
    public LocalPidResolver() {
        this.navigationTable = new ArrayList<>();
    }

    /**
     * Returns the navigation table that aids in navigating through the provenance documents.
     *
     * @return A list of lists of QualifiedName objects representing the navigation table.
     */
    @Override
    public List<Map<String, QualifiedName>> getNavigationTable() {
        return this.navigationTable;
    }

    /**
     * Resolves the entity by finding its row in the navigation table.
     *
     * @param entityId   The QualifiedName identifier of the entity.
     * @param entityType The QualifiedName representing the type of the entity.
     * @return The row from the navigation table as a list of QualifiedName objects.
     */
    @Override
    public Map<String, QualifiedName> resolve(QualifiedName entityId, QualifiedName entityType) {
        for (Map<String, QualifiedName> row : this.navigationTable) {
            if (row.get("entityID").equals(entityId) && row.get("connectorID").equals(entityType)) {
                return row;
            }
        }
        return null;
    }

    @Override
    public QualifiedName getMetaDoc(QualifiedName connector, QualifiedName bundleId) {
        for (Map<String, QualifiedName> row : this.navigationTable) {
            if (row.get("referenceBundleID").equals(bundleId)) {
                return row.get("metaID");
            }
        }
        return null;
    }

    /**
     * Checks if a given entity is a connector.
     *
     * @param entityId The QualifiedName identifier of the entity.
     * @return True if the entity is a connector, false otherwise.
     */
    @Override
    public boolean isConnector(QualifiedName entityId) {
        for (Map<String, QualifiedName> row : this.navigationTable) {
            if (row.get("entityID").equals(entityId)) {
                return true;
            }
        }
        return false;
    }
}
