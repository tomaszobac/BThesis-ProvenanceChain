/*
package bthesis;

import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.ProvFactory;

public class MergeProvDocs {
    ProvFactory pFactory = ProvFactory.getFactory();

    public Document mergeDocuments(Document doc1, Document doc2) {
        Document mergedDoc = pFactory.newDocument();

        for (Entity entity1 : doc1.getStatementOrBundle().get(0)) {
            Entity entity2 = findEntity(doc2, entity1.getId());
            if (entity2 != null) {
                Entity mergedEntity = mergeEntities(entity1, entity2);
                mergedDoc.getEntity().add(mergedEntity);
            } else {

                mergedDoc.getEntity().add(entity1);
            }
        }


        return mergedDoc;
    }

    private Entity findEntity(Document doc, QualifiedName id) {
        for (Entity entity : doc.getEntity()) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }

    private Entity mergeEntities(Entity entity1, Entity entity2) {
        Entity mergedEntity = pFactory.newEntity(entity1.getId());

        mergedEntity.getAttributes().putAll(entity1.getAttributes());
        mergedEntity.getAttributes().putAll(entity2.getAttributes());

        return mergedEntity;
    }
}
*/
