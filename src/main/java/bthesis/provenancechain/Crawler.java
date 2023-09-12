package bthesis.provenancechain;

import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;

import bthesis.metageneration.HashDocument;

/**
 * The Crawler class is responsible for traversing the provenance graph
 * to find precursor and successor nodes related to a given entity.
 *
 * @author Tomas Zobac
 */
public class Crawler {
    private final Initializer initializer;
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    private List<QualifiedName> done;
    private List<ProvenanceNode> nodes;

    /**
     * Initializes a new instance of the Crawler class.
     *
     * @param initializer The Initializer object that contains the application state.
     */
    public Crawler(Initializer initializer) {
        this.initializer = initializer;
        this.done = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.mainActivity = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "mainActivity", "cpm");
        this.receiverConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "receiverConnector", "cpm");
        this.senderConnector = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "senderConnector", "cpm");
    }

    /**
     * Returns the list of provenance nodes discovered during crawling.
     *
     * @return A list of ProvenanceNode objects.
     */
    public List<ProvenanceNode> getNodes() {
        return this.nodes;
    }

    /**
     * Clears the list of done nodes and discovered nodes.
     */
    public void cleanup() {
        this.done = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    /**
     * Crawls through the provenance graph to find precursors of a given entity.
     *
     * @param entity_id        The QualifiedName identifier of the entity.
     * @param bundle_id        The QualifiedName identifier of the bundle.
     * @param include_activity If true, activities related to precursors are included.
     * @param hasher           The HashDocument object for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    public void getPrecursors(QualifiedName entity_id, QualifiedName bundle_id, boolean include_activity, HashDocument hasher) throws NoSuchAlgorithmException {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);

        if (entity_type.equals(this.senderConnector)) {
            if (include_activity)
                this.nodes.add(new ProvenanceNode(entity_id, bundle_id, retrieveMainActivity(temp), checkSum(hasher, document)));
            else this.nodes.add(new ProvenanceNode(entity_id, bundle_id, null, checkSum(hasher, document)));
            this.done.add(entity_id);
        }
        if (entity_type.equals(this.receiverConnector)) {
            this.done.add(entity_id);
            getPrecursors(entity_id, resolve(entity_id, this.receiverConnector).get(2), include_activity, hasher);
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
                    getPrecursors(connector, bundle_id, include_activity, hasher);
                }
            }
        }
    }

    /**
     * Crawls through the provenance graph to find successors of a given entity.
     *
     * @param entity_id        The QualifiedName identifier of the entity.
     * @param bundle_id        The QualifiedName identifier of the bundle.
     * @param include_activity If true, activities related to successors are included.
     * @param hasher           The HashDocument object for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    public void getSuccessors(QualifiedName entity_id, QualifiedName bundle_id, boolean include_activity, HashDocument hasher) throws NoSuchAlgorithmException {
        Document document = this.initializer.getMemory().getDocuments().get(bundle_id);
        QualifiedName entity_type = getEntityType(entity_id, document);
        assert entity_type != null;
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);

        if (entity_type.equals(this.receiverConnector)) {
            if (include_activity)
                this.nodes.add(new ProvenanceNode(entity_id, bundle_id, retrieveMainActivity(temp), checkSum(hasher, document)));
            else this.nodes.add(new ProvenanceNode(entity_id, bundle_id, null, checkSum(hasher, document)));
            checkSum(hasher, document);
            this.done.add(entity_id);
        }
        if (entity_type.equals(this.senderConnector)) {
            this.done.add(entity_id);
            getSuccessors(entity_id, resolve(entity_id, this.senderConnector).get(2), include_activity, hasher);
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
                    getSuccessors(connector, bundle_id, include_activity, hasher);
                }
            }
        }
    }

    /**
     * Retrieves the type of given entity from a document.
     *
     * @param entity_id The QualifiedName identifier of the entity.
     * @param document  The Document object containing the entity.
     * @return The QualifiedName representing the type of the entity.
     */
    private QualifiedName getEntityType(QualifiedName entity_id, Document document) {
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

    /**
     * Resolves the entity by finding its row in the navigation table.
     *
     * @param entity_id   The QualifiedName identifier of the entity.
     * @param entity_type The QualifiedName representing the type of the entity.
     * @return The row from the navigation table as a list of QualifiedName objects.
     */
    public List<QualifiedName> resolve(QualifiedName entity_id, QualifiedName entity_type) {
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(0).equals(entity_id) && row.get(1).equals(entity_type)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Checks if a given entity is a connector.
     *
     * @param entity_id The QualifiedName identifier of the entity.
     * @return True if the entity is a connector, false otherwise.
     */
    private boolean isConnector(QualifiedName entity_id) {
        for (List<QualifiedName> row : this.initializer.getMemory().getNavigation_table()) {
            if (row.get(0).equals(entity_id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the main activity related to a bundle.
     *
     * @param bundle The Bundle object containing the statements.
     * @return A list of QualifiedName objects representing the main activity.
     */
    private List<QualifiedName> retrieveMainActivity(Bundle bundle) {
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

    /**
     * Computes and verifies the checksum for a given document.
     *
     * @param hasher   The HashDocument object for hashing.
     * @param document The Document object to be hashed.
     * @return A string representation of the checksum verification result.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    private String checkSum(HashDocument hasher, Document document) throws NoSuchAlgorithmException {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        InteropFramework framework = new InteropFramework();
        Bundle bundle = (Bundle) this.initializer.getMemory().getMetadocument().getStatementOrBundle().get(0);
        Bundle docbundle = (Bundle) document.getStatementOrBundle().get(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        framework.writeDocument(baos, Formats.ProvFormat.PROVN, document);

        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Entity entity) {
                if (entity.getId().equals(docbundle.getId())) {
                    String sha256 = hasher.generateSHA256(baos.toByteArray());
                    String md5 = hasher.generateMD5(baos.toByteArray());
                    String checksum = docbundle.getId().getLocalPart() + ": ";
                    checksum += "SHA256=" +
                            (sha256.equals(entity.getOther().get(0).getValue()) ?
                                    ANSI_GREEN + "OK" + ANSI_RESET : ANSI_RED + "FAILED" + ANSI_RESET);
                    checksum += " | MD5=" +
                            (md5.equals(entity.getOther().get(1).getValue()) ?
                                    ANSI_GREEN + "OK" + ANSI_RESET : ANSI_RED + "FAILED" + ANSI_RESET);
                    return checksum;
                }
            }
        }
        return "checksum failed";
    }
}
