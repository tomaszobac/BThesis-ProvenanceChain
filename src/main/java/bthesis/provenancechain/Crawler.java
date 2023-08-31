package bthesis.provenancechain;

import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.Type;
import org.openprovenance.prov.model.WasDerivedFrom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Crawler {
    private final Initializer initializer;
    private List<QualifiedName> done;
    private List<QualifiedName> document_done;
    private LinkedHashMap<List<QualifiedName>, List<QualifiedName>> precursors;
    private LinkedHashMap<QualifiedName, List<QualifiedName>> successors;
    private List<List<QualifiedName>> activities;
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    public Crawler(Initializer initializer) {
        this.initializer = initializer;
        this.done = new ArrayList<>();
        this.precursors = new LinkedHashMap<>();
        this.successors = new LinkedHashMap<>();
        this.document_done = new ArrayList<>();
        this.activities = new ArrayList<>(new ArrayList<>());
        this.mainActivity = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "mainActivity","cpm");
        this.receiverConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "receiverConnector","cpm");
        this.senderConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "senderConnector","cpm");
    }

    public LinkedHashMap<List<QualifiedName>, List<QualifiedName>> getPrec() {
        return this.precursors;
    }

    public LinkedHashMap<QualifiedName, List<QualifiedName>> getSucc() {
        return this.successors;
    }
    public List<List<QualifiedName>> getActiv() {
        return this.activities;
    }

    public void cleanup() {
        this.done = new ArrayList<>();
        this.precursors = new LinkedHashMap<>();
        this.successors = new LinkedHashMap<>();
        this.activities = new ArrayList<>(new ArrayList<>());
    }

    public void getPrecursors(QualifiedName entity_id, QualifiedName bundle_id, boolean include_activity) {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        if (entity_type.equals(this.senderConnector)) {
            if (include_activity) this.precursors.put(Arrays.asList(entity_id,bundle_id), retrieveMainActivity(temp));
            else this.precursors.put(Arrays.asList(entity_id,bundle_id), null);
            this.done.add(entity_id);
        }
        if (entity_type.equals(this.receiverConnector)) {
            this.done.add(entity_id);
            getPrecursors(entity_id, resolve(entity_id,this.receiverConnector).get(2), include_activity);
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
                    getPrecursors(connector,bundle_id, include_activity);
                }
            }
        }
    }

    public void getSuccessors(QualifiedName entity_id, QualifiedName bundle_id) {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        if (entity_type.equals(this.receiverConnector)) {
            this.successors.put(entity_id, retrieveMainActivity(temp));
            this.done.add(entity_type);
        }
        if (entity_type.equals(this.senderConnector)) {
            this.done.add(entity_id);
            getSuccessors(entity_id, resolve(entity_id,this.senderConnector).get(2));
        } else {
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getUsedEntity().equals(entity_id)))
                    continue;
                if (!(isConnector(derived.getGeneratedEntity())))
                    continue;
                QualifiedName connector = derived.getGeneratedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getSuccessors(connector,bundle_id);
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

    private List<QualifiedName> retrieveMainActivity (Bundle bundle) {
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Activity activity) {
                if (activity.getType().isEmpty())
                    continue;
                List<QualifiedName> temp = new ArrayList<>();
                activity.getType().forEach(type -> temp.add((QualifiedName) type.getValue()));
                if (temp.contains(this.mainActivity)) {
                    return temp;
                }
            }
        }
        return null;
    }

}
