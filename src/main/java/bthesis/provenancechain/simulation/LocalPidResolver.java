package bthesis.provenancechain.simulation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.openprovenance.prov.model.QualifiedName;

import bthesis.provenancechain.tools.metadata.IPidResolver;

/**
 * BThesis simulation file
 * Implementation of the {@link IPidResolver} interface for resolving PIDs (Persistent Identifiers)
 * in a local context. This class uses an in-memory navigation table for the resolution of PIDs.
 *
 * @author Tomas Zobac
 */
public class LocalPidResolver implements IPidResolver {
    private final List<Map<String, QualifiedName>> navigationTable;

    /**
     * Default constructor for the LocalPidResolver. Initializes the navigation table.
     */
    public LocalPidResolver() {
        this.navigationTable = new ArrayList<>();
    }

    /**
     * Retrieves the in-memory navigation table.
     *
     * @return The navigation table containing mappings of value IDs to their corresponding QualifiedNames.
     */
    @Override
    public List<Map<String, QualifiedName>> getNavigationTable(QualifiedName entityId) {
        return this.navigationTable;
    }

    /**
     * Resolves a given entity ID and entity type to a map of associated row in the navigation table.
     *
     * @param entityId The ID of the entity to be resolved.
     * @param entityType The type of the entity.
     * @return A map containing key-value pairs related to the resolved entity, or null if not found.
     */
    @Override
    public Map<String, QualifiedName> getConnectorEntry(QualifiedName entityId, QualifiedName entityType) {
        for (Map<String, QualifiedName> row : this.navigationTable) {
            if (row.get("entityID").equals(entityId) && row.get("connectorID").equals(entityType)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Retrieves the meta document ID associated with a given referenced bundle ID from the navigation table.
     *
     * @param bundleId The ID of the bundle.
     * @return The QualifiedName of the meta document associated with the given referenced bundle ID, or null if not found.
     */
    @Override
    public QualifiedName getMetaDoc(QualifiedName bundleId) {
        for (Map<String, QualifiedName> row : this.navigationTable) {
            if (row.get("referenceBundleID").equals(bundleId)) {
                return row.get("metaID");
            }
        }
        return null;
    }
}
