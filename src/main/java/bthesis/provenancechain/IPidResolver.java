package bthesis.provenancechain;

import org.openprovenance.prov.model.QualifiedName;

import java.util.List;

public interface IPidResolver {
    //TODO: setTable (pořádí v tabulce je důležité)
    List<List<QualifiedName>> getNavigation_table();
    List<QualifiedName> resolve(QualifiedName entity_id, QualifiedName entity_type); //TODO: listy předělat na mapu kvůli settable problému
    QualifiedName getMetaDoc(QualifiedName externalConnector, QualifiedName bundle_id);
    boolean isConnector(QualifiedName entity_id);
}
