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
import java.util.Stack;

public class Crawler {
    private final Initializer initializer;
    private Stack<QualifiedName> stack;
    private List<QualifiedName> done;
    private final QualifiedName externalInputConnector;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    public Crawler(Initializer initializer) {
        ProvFactory provFactory = new ProvFactory();
        this.initializer = initializer;
        this.stack = new Stack<>();
        this.done = new ArrayList<>();
        this.externalInputConnector = provFactory.newQualifiedName("cpm_uri", "externalInputConnector","cpm");
        this.receiverConnector = provFactory.newQualifiedName("cpm_uri", "receiverConnector","cpm");
        this.senderConnector = provFactory.newQualifiedName("cpm_uri", "senderConnector","cpm");
    }

    public List<QualifiedName> getPrecursors(QualifiedName entity, QualifiedName bundle) {
        List<QualifiedName> result = new ArrayList<>();
        Document document = this.initializer.getMemory().getDocuments().get(bundle);
        QualifiedName entity_type = getEntityType(entity, document);
        assert entity_type != null;
        if (entity_type.equals(this.externalInputConnector)) {
            this.stack.push(entity);
            this.done.add(entity);
            Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getGeneratedEntity().equals(entity)))
                    continue;
                if (resolve(derived.getUsedEntity(), this.receiverConnector) == null)
                    continue;
                QualifiedName receiver = derived.getUsedEntity();
                if (!(this.done.contains(receiver))) {
                    QualifiedName newExInput = findExConnector(receiver, receiverConnector);
                }


            }
            return result;
        }
        if (entity_type.toString().equals("'cpm:{{cpm_uri}}receiverConnector'")) {

        }
        if (entity_type.toString().equals("'cpm:{{cpm_uri}}senderConnector'")) {

        }
        return null;
    }

    public QualifiedName getPrecursors(QualifiedName entity, QualifiedName bundle, String swtch) {
        return null;
    }

    public QualifiedName getSuccessors(QualifiedName entity, QualifiedName bundle) {
        return null;
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

    private QualifiedName findExConnector (QualifiedName connectorID, QualifiedName connectorType){
        QualifiedName target = resolve(connectorID,connectorType).get(2);
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(2).equals(target) && row.get(1).equals(this.externalInputConnector)) {
                return row.get(0);
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

}
