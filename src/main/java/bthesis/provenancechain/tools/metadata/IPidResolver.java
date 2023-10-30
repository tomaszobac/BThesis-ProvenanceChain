package bthesis.provenancechain.tools.metadata;

import org.openprovenance.prov.model.QualifiedName;

import java.util.List;
import java.util.Map;

public interface IPidResolver {
    List<Map<String, QualifiedName>> getNavigationTable();
    Map<String, QualifiedName> resolve(QualifiedName entityId, QualifiedName entityType);
    QualifiedName getMetaDoc(QualifiedName externalConnector, QualifiedName bundleId);
    boolean isConnector(QualifiedName entityId);
}
