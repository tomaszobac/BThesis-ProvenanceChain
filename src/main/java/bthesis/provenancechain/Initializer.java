package bthesis.provenancechain;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
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
import bthesis.metageneration.HashDocument;
import bthesis.metageneration.MetaGeneration;

import javax.ws.rs.DELETE;

/**
 * The Initializer class is responsible for initializing the application state,
 * including the creation of a DataHolder to store provenance documents and other metadata.
 *
 * @author Tomas Zobac
 */
public class Initializer {
    private final LocalPidResolver memory;
    private final List<QualifiedName> connectors;
    private final QualifiedName metaID;
    private final Map<QualifiedName, Document> documents;

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
    public LocalPidResolver getMemory() {
        return memory;
    }

    /**
     * Populates the navigation table with entities from the provenance documents.
     * Only entities that match predefined connector types are added to the table.
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

    /**
     * Returns the map of QualifiedName to Document objects representing the provenance documents.
     *
     * @return A map of QualifiedName to Document objects.
     */
    @Deprecated
    public Map<QualifiedName, Document> getDocuments() {
        return documents;
    }

    /**
     * Populates the document map based on the list of File objects passed.
     * Reads each file and extracts its bundle ID and document to populate the map.
     *
     * @param documents A list of File objects representing the provenance documents.
     */
    @Deprecated
    public void setDocuments(List<File> documents) {
        InteropFramework inFm = new InteropFramework();
        for (File file : documents) {
            Document temp = inFm.readDocumentFromFile(file.getAbsolutePath());
            Bundle bundle = (Bundle) temp.getStatementOrBundle().get(0);
            this.documents.put(bundle.getId(), temp);
        }
    }
}
