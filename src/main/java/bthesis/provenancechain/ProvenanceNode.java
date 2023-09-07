package bthesis.provenancechain;

import org.openprovenance.prov.model.QualifiedName;

import java.util.List;

public class ProvenanceNode {
    private final QualifiedName connector;
    private final QualifiedName bundle;
    private final List<QualifiedName> activities;

    public ProvenanceNode(QualifiedName connector, QualifiedName bundle, List<QualifiedName> activities) {
        this.connector = connector;
        this.bundle = bundle;
        this.activities = activities;
    }

    public QualifiedName getConnector() {
        return connector;
    }

    public QualifiedName getBundle() {
        return bundle;
    }

    public List<QualifiedName> getActivities() {
        return activities;
    }
}
