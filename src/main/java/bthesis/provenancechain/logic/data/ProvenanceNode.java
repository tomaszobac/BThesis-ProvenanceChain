package bthesis.provenancechain.logic.data;

import java.util.List;

import org.openprovenance.prov.model.QualifiedName;

/**
 * The ProvenanceNode record represents a node in the provenance graph.
 * It contains information about the connector, the bundle where it is located,
 * any related activities, and a checksum for verification.
 *
 * @author Tomas Zobac
 */
public record ProvenanceNode(QualifiedName connector, QualifiedName bundle, List<QualifiedName> activities,
                             String checksum) {
}
