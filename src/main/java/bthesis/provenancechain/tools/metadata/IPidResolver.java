package bthesis.provenancechain.tools.metadata;

import org.openprovenance.prov.model.QualifiedName;

import java.util.List;
import java.util.Map;

/**
 * Represents an interface for resolving PIDs (Persistent Identifiers) and working with the navigational table.
 *
 * @author Tomas Zobac
 */
public interface IPidResolver {
    /**
     * Returns the navigational table corresponding to the provided entity.
     * It should be a collection of maps each containing the valueID:value pairs
     * Example:
     * "entityID":QN of the entity
     * "connectorID":QN of the connector name
     * "referenceBundleID":QN of the bundle where the entity is pointing
     * "metaID":QN of the meta-document in which the referenced bundle is included
     *
     * @return A list of maps where each map contains key-value pairs representing
     * value identifiers and their associated QualifiedNames.
     */
    List<Map<String, QualifiedName>> getNavigationTable(QualifiedName entityId);

    /**
     * Resolves a given entity ID and entity type to a row from the navigational table.
     *
     * @param entityId The ID of the entity to be resolved.
     * @param entityType The type of the entity.
     * @return A map containing key-value pairs related to the resolved entity.
     */
    Map<String, QualifiedName> getConnectorEntry(QualifiedName entityId, QualifiedName entityType);

    /**
     * Retrieves the meta document from the navigational table by finding an entry referencing the provided bundleId.
     *
     * @param bundleId The ID of the bundle.
     * @return The QualifiedName of the meta document associated with the given parameters.
     */
    QualifiedName getMetaDoc(QualifiedName bundleId);

    /**
     * Determines if a given entity ID represents a connector.
     *
     * @param entityId The ID of the entity to be checked.
     * @return True if the entity represents a connector, false otherwise.
     */
    boolean isConnector(QualifiedName entityId);
}
