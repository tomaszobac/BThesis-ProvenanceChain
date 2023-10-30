package bthesis.provenancechain.logic;

import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import bthesis.provenancechain.logic.data.ProvenanceNode;
import bthesis.provenancechain.tools.retrieving.IMetaHashRetriever;
import bthesis.provenancechain.tools.metadata.IPidResolver;
import bthesis.provenancechain.tools.loading.LoaderResolver;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;

import bthesis.provenancechain.tools.security.HashDocument;

/**
 * The Crawler class is responsible for traversing the provenance graph
 * to find precursor and successor nodes related to a given entity.
 *
 * @author Tomas Zobac
 */
public class Crawler {
    private final IPidResolver pidResolver;
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    private List<QualifiedName> done;
    private List<ProvenanceNode> nodes;
    private final LoaderResolver resolver;
    private final IMetaHashRetriever metaHashRetriever;

    /**
     * Initializes a new instance of the Crawler class.
     *
     */
    public Crawler(IPidResolver pidResolver, Map<String, QualifiedName> connectors, IMetaHashRetriever metaHashRetriever) {
        this.pidResolver = pidResolver;
        this.done = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.mainActivity = connectors.get("mainActivity");
        this.receiverConnector = connectors.get("receiverConnector");
        this.senderConnector = connectors.get("senderConnector");
        this.resolver = new LoaderResolver();
        this.metaHashRetriever = metaHashRetriever;
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
     * @param entityId        The QualifiedName identifier of the entity.
     * @param bundleId        The QualifiedName identifier of the bundle.
     * @param includeActivity If true, activities related to precursors are included.
     * @param hasher          The HashDocument object for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    //TODO: napsat do javadocu rozdíl mezi done a nodes + stručně popsat co dělají 3 core ify
    public void getPrecursors(QualifiedName entityId, QualifiedName bundleId, boolean includeActivity, HashDocument hasher) throws NoSuchAlgorithmException {
        Document document = resolver.load(bundleId);
        String checksum = checkSum(hasher, document, true);
        if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + bundleId + "\n" + checksum + "\nTerminating traversal!");
        QualifiedName entityType = getEntityType(entityId, document);
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        if (entityType == null) throw new RuntimeException("entityType is null for: \n" + temp.getId() + "\n" + entityId);

        if (entityType.equals(this.senderConnector)) {
            if (includeActivity)
                this.nodes.add(new ProvenanceNode(entityId, bundleId, retrieveMainActivity(temp), checksum));
            else this.nodes.add(new ProvenanceNode(entityId, bundleId, null, checksum));
            this.done.add(entityId);
        }
        if (entityType.equals(this.receiverConnector)) {
            this.done.add(entityId);
            getPrecursors(entityId, this.pidResolver.resolve(entityId, this.receiverConnector).get("referenceBundleID"), includeActivity, hasher);
        } else {
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getGeneratedEntity().equals(entityId)))
                    continue;
                if (!(this.pidResolver.isConnector(derived.getUsedEntity())))
                    continue;
                QualifiedName connector = derived.getUsedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getPrecursors(connector, bundleId, includeActivity, hasher);
                }
            }
        }
    }

    /**
     * Crawls through the provenance graph to find successors of a given entity.
     *
     * @param entityId        The QualifiedName identifier of the entity.
     * @param bundleId        The QualifiedName identifier of the bundle.
     * @param includeActivity If true, activities related to successors are included.
     * @param hasher          The HashDocument object for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    public void getSuccessors(QualifiedName entityId, QualifiedName bundleId, boolean includeActivity, HashDocument hasher) throws NoSuchAlgorithmException {
        Document document = resolver.load(bundleId);
        String checksum = checkSum(hasher, document, false);
        if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + bundleId + "\n" + checksum + "\nTerminating traversal!");
        QualifiedName entityType = getEntityType(entityId, document);
        Bundle temp = (Bundle) document.getStatementOrBundle().get(0);
        if (entityType == null) throw new RuntimeException("entityType is null for: \n" + temp.getId() + "\n" + entityId);

        if (entityType.equals(this.receiverConnector)) {
            if (includeActivity)
                this.nodes.add(new ProvenanceNode(entityId, bundleId, retrieveMainActivity(temp), checksum));
            else this.nodes.add(new ProvenanceNode(entityId, bundleId, null, checksum));
            this.done.add(entityId);
        }
        if (entityType.equals(this.senderConnector)) {
            this.done.add(entityId);
            getSuccessors(entityId, this.pidResolver.resolve(entityId, this.senderConnector).get("referenceBundleID"), includeActivity, hasher);
        } else {
            for (Statement statement : temp.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getUsedEntity().equals(entityId)))
                    continue;
                if (!(this.pidResolver.isConnector(derived.getGeneratedEntity())))
                    continue;
                QualifiedName connector = derived.getGeneratedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getSuccessors(connector, bundleId, includeActivity, hasher);
                }
            }
        }
    }

    /**
     * Retrieves the type of given entity from a document.
     *
     * @param entityId The QualifiedName identifier of the entity.
     * @param document The Document object containing the entity.
     * @return The QualifiedName representing the type of the entity.
     */
    private QualifiedName getEntityType(QualifiedName entityId, Document document) {
        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Entity entity) {
                if (entity.getId().equals(entityId)) {
                    return (QualifiedName) entity.getType().get(0).getValue();
                }
            }
        }
        return null;
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
    private String checkSum(HashDocument hasher, Document document, boolean direction) throws NoSuchAlgorithmException {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        InteropFramework framework = new InteropFramework();
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);
        QualifiedName connector = direction ? this.receiverConnector : this.senderConnector;
        Document metaDocument = this.resolver.load(this.pidResolver.getMetaDoc(connector, docBundle.getId()));


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        framework.writeDocument(byteArrayOutputStream, Formats.ProvFormat.PROVN, document);

        String sha256 = hasher.generateSHA256(byteArrayOutputStream.toByteArray());
        String md5 = hasher.generateMD5(byteArrayOutputStream.toByteArray());
        String checksum = docBundle.getId().getLocalPart() + ": ";

        Map<String, String> hashes = metaHashRetriever.retrieveHash(metaDocument, docBundle.getId());

        checksum += "SHA256=" +
                (sha256.equals(hashes.get("sha256")) ?
                        ANSI_GREEN + "OK" + ANSI_RESET : ANSI_RED + "FAILED" + ANSI_RESET);
        checksum += " | MD5=" +
                (md5.equals(hashes.get("md5")) ?
                        ANSI_GREEN + "OK" + ANSI_RESET : ANSI_RED + "FAILED" + ANSI_RESET);

        return checksum;
    }
}
