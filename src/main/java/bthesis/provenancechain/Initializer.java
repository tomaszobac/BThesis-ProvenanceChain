package bthesis.provenancechain;

import bthesis.metageneration.MetaBuilder;
import bthesis.metageneration.MetaGeneration;
import bthesis.metageneration.HashDocument;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Other;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Initializer {
    private final DataHolder memory;
    private final List<String> connectors;
    private final QualifiedName metaID;

    public Initializer(HashDocument hasher, MetaBuilder meta, List<File> files) throws NoSuchAlgorithmException {
        this.memory = new DataHolder();
        this.connectors = Arrays.asList("'cpm:{{cpm_uri}}senderConnector'",
                "'cpm:{{cpm_uri}}externalInputConnector'",
                "'cpm:{{cpm_uri}}receiverConnector'");
        MetaGeneration generation = new MetaGeneration();
        this.memory.setMetadocument(generation.generate(hasher, meta, files));
        this.memory.setDocuments(files);
        Bundle temp = (Bundle) this.memory.getMetadocument().getStatementOrBundle().get(0);
        this.metaID =  temp.getId();
        fillTable();
        checkSum(hasher);
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

    private void checkSum(HashDocument hasher) throws NoSuchAlgorithmException {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        ProvFactory pFactory = new ProvFactory();
        Bundle bundle = (Bundle) this.memory.getMetadocument().getStatementOrBundle().get(0);
        for (Map.Entry<QualifiedName, Document> document : this.memory.getDocuments().entrySet()) {
            boolean sumCorrect = false;
            for (Statement statement : bundle.getStatement()) {
                if (statement instanceof Entity entity) {
                    if (entity.getId().equals(document.getKey())) {
                        System.out.print(document.getKey().getLocalPart()+ ": ");
                        Other sha256 = pFactory.newOther("HASH_URI", "sha256", "hash",
                                hasher.generateSHA256(document.getValue().toString()), null);
                        sumCorrect = sha256.equals(entity.getOther().get(0));
                        System.out.print("MD5="+(sumCorrect ? ANSI_GREEN+"OK"+ANSI_RESET : ANSI_RED+"FAILED"+ANSI_RESET));
                        Other md5 = pFactory.newOther("HASH_URI", "md5", "hash",
                                hasher.generateMD5(document.getValue().toString()), null);
                        sumCorrect = md5.equals(entity.getOther().get(1));
                        System.out.println(" | SHA256="+(sumCorrect ? ANSI_GREEN+"OK"+ANSI_RESET : ANSI_RED+"FAILED"+ANSI_RESET));
                        System.out.println(sha256 + " vs \n" + entity.getOther().get(0));
                        System.out.println(md5 + " vs \n" + entity.getOther().get(1));
                    }
                }
            }
        }
    }
}
