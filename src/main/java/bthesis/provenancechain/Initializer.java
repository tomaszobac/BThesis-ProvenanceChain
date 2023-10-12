package bthesis.provenancechain;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.QualifiedName;

import bthesis.metageneration.MetaBuilder;
import bthesis.metageneration.HashDocument;
import bthesis.metageneration.MetaGeneration;

/**
 * The Initializer class is responsible for initializing the application state,
 * including the creation of a DataHolder to store provenance documents and other metadata.
 *
 * @author Tomas Zobac
 */
public class Initializer {
    private final DataHolder memory;
    private final List<QualifiedName> connectors;
    private final QualifiedName metaID;

    /**
     * Initializes a new instance of the Initializer class.
     * Sets up the data holder, generates the meta-document, and populates the navigation table.
     *
     * @param hasher The HashDocument object for hashing.
     * @param meta   The MetaBuilder object for metadata generation.
     * @param files  A list of File objects representing the provenance documents.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     * @throws InterruptedException     If the thread is interrupted.
     */
    public Initializer(HashDocument hasher, MetaBuilder meta, List<File> files) throws NoSuchAlgorithmException, InterruptedException {
        this.memory = new DataHolder();
        this.connectors = Arrays.asList(
                this.memory.getSenderConnector(),
                this.memory.getReceiverConnector(),
                this.memory.getExternalInputConnector());
        MetaGeneration generation = new MetaGeneration();
        this.memory.setMetadocument(generation.generate(hasher, meta, files));
        this.memory.setDocuments(files);
        Bundle temp = (Bundle) this.memory.getMetadocument().getStatementOrBundle().get(0);
        this.metaID = temp.getId();
        fillTable();
        fakeLoading();
    }

    /**
     * Simulates a loading sequence for the application.
     *
     * @throws InterruptedException If the thread is interrupted.
     */
    private static void fakeLoading() throws InterruptedException {
        System.out.print(".\r");
        Thread.sleep(500);
        System.out.print("..\r");
        Thread.sleep(500);
        System.out.print("...\r");
        Thread.sleep(750);
        System.out.println("Data initialized");
        Thread.sleep(500);
    }

    /**
     * Returns the DataHolder object containing the application state.
     *
     * @return A DataHolder object.
     */
    public DataHolder getMemory() {
        return memory;
    }

    /**
     * Populates the navigation table with entities from the provenance documents.
     * Only entities that match predefined connector types are added to the table.
     */
    private void fillTable() {
        for (Document document : memory.getDocuments().values()) {
            Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
            for (Statement statement : bundle.getStatement()) {
                if (statement instanceof Entity entity) {
                    if (entity.getType().isEmpty()) {
                        continue;
                    }
                    if (connectors.contains((QualifiedName) entity.getType().get(0).getValue())) {
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
