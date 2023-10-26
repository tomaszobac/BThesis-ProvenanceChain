package bthesis.provenancechain.tools.metadata;

import org.openprovenance.prov.model.QualifiedName;

import java.util.List;
import java.util.Map;

public interface IPidResolver {
    List<Map<String, QualifiedName>> getNavigation_table();
    Map<String, QualifiedName> resolve(QualifiedName entity_id, QualifiedName entity_type);
    QualifiedName getMetaDoc(QualifiedName externalConnector, QualifiedName bundle_id);
    boolean isConnector(QualifiedName entity_id);
}
