package bthesis.provenancechain.simulation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import bthesis.metageneration.WriteDocument;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.QualifiedName;

import bthesis.metageneration.MetaBuilder;
import bthesis.provenancechain.tools.security.HashDocument;
import bthesis.metageneration.MetaGeneration;

/**
 * BThesis simulation file
 * Initializes and manages the simulation data structures, including the PID resolution memory,
 * meta document generation, and document loading.
 *
 * @author Tomas Zobac
 */
public class Initializer {
    private final LocalPidResolver memory;
    private final List<QualifiedName> connectors;
    private final QualifiedName metaID;
    private final Map<QualifiedName, Document> documents;

    /**
     * Constructor that initializes the simulation data structures.
     *
     * @param hasher The HashDocument instance used for hashing.
     * @param meta The MetaBuilder instance used for meta document construction.
     * @param files A list of files to be processed.
     * @param connectors A map of connector identifiers to their qualified names.
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available.
     * @throws InterruptedException if the fake loading is interrupted.
     */
    public Initializer(HashDocument hasher,
                       MetaBuilder meta,
                       List<File> files,
                       Map<String, QualifiedName> connectors) throws NoSuchAlgorithmException, InterruptedException {
        this.documents = new HashMap<>();
        this.memory = new LocalPidResolver();
        this.connectors = List.of(
                connectors.get("senderConnector"),
                connectors.get("receiverConnector"),
                connectors.get("externalConnector"));
        MetaGeneration generation = new MetaGeneration();
        Document document = generation.generate(hasher, meta, files);
        WriteDocument outputWriter = new WriteDocument("src/main/resources/", document);
        outputWriter.writeDocument();
        setDocuments(files);
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        this.metaID = temp.getId();
        fillTable();
        fakeLoading();
    }

    /**
     * Simulates a loading process with a visual feedback.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
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
     * Retrieves the local PID resolver instance.
     *
     * @return The LocalPidResolver instance.
     */
    public LocalPidResolver getMemory() {
        return memory;
    }

    /**
     * Fills the in-memory navigation table using the loaded documents.
     */
    private void fillTable() {
        for (Document document : this.documents.values()) {
            Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
            for (Statement statement : bundle.getStatement()) {
                if (statement instanceof Entity entity) {
                    if (entity.getType().isEmpty()) {
                        continue;
                    }
                    if (connectors.contains((QualifiedName) entity.getType().get(0).getValue())) {
                        Map<String, QualifiedName> row = new HashMap<>();
                        row.put("entityID", entity.getId());
                        row.put("connectorID", (QualifiedName) entity.getType().get(0).getValue());
                        row.put("referenceBundleID", (QualifiedName) entity.getOther().get(0).getValue());
                        row.put("metaID", this.metaID);
                        getMemory().getNavigationTable().add(row);
                    }
                }
            }
        }
    }

    /**
     * Converts files from {@link SimulationFiles} to Document objects.
     *
     * @param documents A list of files to be processed.
     */
    private void setDocuments(List<File> documents) {
        InteropFramework inFm = new InteropFramework();
        for (File file : documents) {
            Document temp = inFm.readDocumentFromFile(file.getAbsolutePath());
            Bundle bundle = (Bundle) temp.getStatementOrBundle().get(0);
            this.documents.put(bundle.getId(), temp);
        }
    }
}
