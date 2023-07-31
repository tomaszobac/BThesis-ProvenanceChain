package bthesis.provenancechain;

import bthesis.metageneration.MetaGeneration;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Initializer {
    private final DataHolder memory;
    private final List<String> connectors;
    private final QualifiedName metaID;

    public Initializer(List<File> files) throws IOException, NoSuchAlgorithmException {
        this.memory = new DataHolder();
        this.connectors = Arrays.asList("'cpm:{{cpm_uri}}senderConnector'",
                "'cpm:{{cpm_uri}}externalInputConnector'",
                "'cpm:{{cpm_uri}}receiverConnector'");
        MetaGeneration generation = new MetaGeneration(files);
        this.memory.setMetadocument(generation.generate());
        this.memory.setDocuments(files);
        Bundle temp = (Bundle) this.memory.getMetadocument().getStatementOrBundle().get(0);
        this.metaID =  temp.getId();
        fillTable();
    }

    public DataHolder getMemory() {
        return memory;
    }

    private void fillTable() {
        for (Document document : memory.getDocuments().values()) {
            Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
            for (Statement statement : bundle.getStatement()) {
                if (statement instanceof Entity) {
                    Entity entity = (Entity) statement;
                    if (entity.getType().isEmpty()) {
                        continue;
                    }
                    if (connectors.contains(entity.getType().get(0).getValue().toString())) {
                        List<QualifiedName> row = new ArrayList<>();
                        row.add(entity.getId());
                        row.add((QualifiedName) entity.getType().get(0).getValue());
                        row.add((QualifiedName) entity.getOther().get(0).getValue());
                        row.add(this.metaID);
                        getMemory().getNavigation_table().add(row);
                    }
                }
            }
        }
    }
}
