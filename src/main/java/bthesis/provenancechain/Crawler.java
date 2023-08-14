package bthesis.provenancechain;

import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.WasDerivedFrom;

import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final Initializer initializer;
    private List<QualifiedName> done;
    private List<QualifiedName> document_done;
    private List<QualifiedName> precursors;
    private List<QualifiedName> successors;
    private List<List<QualifiedName>> activities;
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    public Crawler(Initializer initializer) {
        this.initializer = initializer;
        this.done = new ArrayList<>();
        this.precursors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.document_done = new ArrayList<>();
        this.activities = new ArrayList<>(new ArrayList<>());
        this.mainActivity = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "mainActivity","cpm");
        this.receiverConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "receiverConnector","cpm");
        this.senderConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "senderConnector","cpm");
    }

    public List<QualifiedName> getPrec() {
        return this.precursors;
    }

    public List<QualifiedName> getSucc() {
        return this.successors;
    }
    public List<List<QualifiedName>> getActiv() {
        return this.activities;
    }

    public void crawl(String entity, String bundle, int mode) {
        QualifiedName entity_id = null;
        QualifiedName bundle_id = null;
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(0).toString().equals(entity)) {
                entity_id = row.get(0);
            }
        }
        for (QualifiedName document_id : this.initializer.getMemory().getDocuments().keySet()) {
            if (document_id.toString().equals(bundle)) {
                bundle_id = document_id;
            }
        }
        switch (mode) {
            case 0 -> getPrecursors(entity_id, bundle_id);
            case 1 -> getSuccessors(entity_id, bundle_id);
        }
    }

    public void cleanup() {
        this.done = new ArrayList<>();
        this.precursors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.activities = new ArrayList<>(new ArrayList<>());
    }

    public void getPrecursors(QualifiedName entity_id, QualifiedName bundle_id) {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        if (entity_type.equals(this.senderConnector)) {
            if (!(this.document_done.contains(bundle_id))) {
                retrieveMainActivity(temp);
                this.document_done.add(bundle_id);
            }
            this.precursors.add(entity_id);
            this.done.add(entity_type);
        }
        if (entity_type.equals(this.receiverConnector)) {
            this.done.add(entity_id);
            getPrecursors(entity_id, resolve(entity_id,this.receiverConnector).get(2));
        } else {
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getGeneratedEntity().equals(entity_id)))
                    continue;
                if (!(isConnector(derived.getUsedEntity())))
                    continue;
                QualifiedName connector = derived.getUsedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getPrecursors(connector,bundle_id);
                }
            }
        }
    }

    public void getSuccessors(QualifiedName entity, QualifiedName bundle) {
        Document document = this.initializer.getMemory().getDocuments().get(bundle);
        QualifiedName entity_type = getEntityType(entity, document);
        assert entity_type != null;
        if (entity_type.equals(this.receiverConnector)) {
            this.successors.add(entity);
        }
        if (entity_type.equals(this.senderConnector)) {
            getSuccessors(entity, resolve(entity,this.senderConnector).get(2));
        } else {
            Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getUsedEntity().equals(entity)))
                    continue;
                if (!(isConnector(derived.getGeneratedEntity())))
                    continue;
                QualifiedName connector = derived.getGeneratedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getSuccessors(connector,bundle);
                }
            }
        }
    }

    private QualifiedName getEntityType (QualifiedName entity_id, Document document){
        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Entity entity) {
                if (entity.getId().equals(entity_id)) {
                    return (QualifiedName) entity.getType().get(0).getValue();
                }
            }
        }
        return null;
    }


    public List<QualifiedName> resolve (QualifiedName entity_id, QualifiedName entity_type) {
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(0).equals(entity_id) && row.get(1).equals(entity_type)) {
                return row;
            }
        }
        return null;
    }

    private boolean isConnector (QualifiedName entity_id) {
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(0).equals(entity_id)) {
                return true;
            }
        }
        return false;
    }

    private void retrieveMainActivity (Bundle bundle) {
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Activity activity) {
                if (activity.getType().isEmpty())
                    continue;
                if (activity.getType().get(0).getValue().equals(this.mainActivity)) {
                    List<QualifiedName> temp = new ArrayList<>();
                    activity.getOther().forEach(x -> temp.add((QualifiedName) x.getValue()));
                    this.activities.add(temp);
                }
            }
        }
    }

}
