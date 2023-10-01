package bthesis;

import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.ProvFactory;

public class MergeProvDocs {
    ProvFactory pFactory = ProvFactory.getFactory();

    public Document mergeDocuments(Document doc1, Document doc2) {
        // Create a new document for the merged result
        Document mergedDoc = pFactory.newDocument();

        // Assume a simple merging strategy based on entity identifiers
        for (Entity entity1 : doc1.getStatementOrBundle().get(0)) {
            Entity entity2 = findEntity(doc2, entity1.getId());
            if (entity2 != null) {
                // Merge attributes of entity1 and entity2
                Entity mergedEntity = mergeEntities(entity1, entity2);
                mergedDoc.getEntity().add(mergedEntity);
            } else {
                // If no matching entity in doc2, simply add entity1
                mergedDoc.getEntity().add(entity1);
            }
        }

        // ... (similarly, check entities from doc2 not present in doc1)

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
        // Create a new entity with the same identifier
        Entity mergedEntity = pFactory.newEntity(entity1.getId());

        // Merge attributes, assuming they are represented as a map
        mergedEntity.getAttributes().putAll(entity1.getAttributes());
        mergedEntity.getAttributes().putAll(entity2.getAttributes());

        return mergedEntity;
    }
}
