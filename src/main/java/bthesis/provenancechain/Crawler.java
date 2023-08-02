package bthesis.provenancechain;

import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.xml.ProvFactory;

import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private final Initializer initializer;
    private List<QualifiedName> done;
    private List<QualifiedName> precursors;
    private List<QualifiedName> successors;
    private final QualifiedName externalInputConnector;
    private final org.openprovenance.prov.vanilla.QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    public Crawler(Initializer initializer) {
        ProvFactory provFactory = new ProvFactory();
        this.initializer = initializer;
        this.done = new ArrayList<>();
        this.precursors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.externalInputConnector = provFactory.newQualifiedName("cpm_uri", "externalInputConnector","cpm");
        this.receiverConnector = (org.openprovenance.prov.vanilla.QualifiedName) provFactory.newQualifiedName("cpm_uri", "receiverConnector","cpm");
        this.senderConnector = provFactory.newQualifiedName("cpm_uri", "senderConnector","cpm");
    }

    public List<QualifiedName> getPrec() {
        return precursors;
    }

    public List<QualifiedName> getSucc() {
        return successors;
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
    }

    public void getPrecursors(QualifiedName entity_id, QualifiedName bundle_id) {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        if (entity_type.equals(this.senderConnector)) {
            this.precursors.add(entity_id);
        }
        System.out.println("Type uri: " + entity_type.getUri());
        System.out.println("Type class: " + entity_type.getClass());
        System.out.println("My uri: " + this.receiverConnector.getUri());
        System.out.println("My class: " + this.receiverConnector.getClass());
        if (entity_type.equals(this.receiverConnector)) {
           getPrecursors(entity_id, resolve(entity_id,this.receiverConnector).get(2));
        } else {
            Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
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
            getPrecursors(entity, resolve(entity,this.senderConnector).get(2));
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
                    getPrecursors(connector,bundle);
                }
            }
        }
    }

    private QualifiedName getEntityType (QualifiedName entity_id, Document document){
        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Entity) {
                Entity entity = (Entity) statement;
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

}
