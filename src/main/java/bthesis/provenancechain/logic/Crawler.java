package bthesis.provenancechain.logic;

import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import bthesis.provenancechain.logic.data.ProvenanceNode;
import bthesis.provenancechain.simulation.Initializer;
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
 * Facilitates the traversal of provenance data by crawling through documents and
 * retrieving provenance entities and their relationships.
 *
 * @author Tomas Zobac
 */
public class Crawler {
    private final QualifiedName mainActivity;
    private final QualifiedName receiverConnector;
    private final QualifiedName senderConnector;
    private List<QualifiedName> done;
    private List<ProvenanceNode> nodes;
    private final LoaderResolver resolver;
    private final IMetaHashRetriever metaHashRetriever;

    /**
     * Constructs a new Crawler with the specified connectors, and meta hash retriever.
     *
     * @param connectors A map of connector identifiers to their qualified names.
     * @param metaHashRetriever The meta hash retriever for fetching hash values.
     */
    public Crawler(Map<String, QualifiedName> connectors, IMetaHashRetriever metaHashRetriever, LoaderResolver resolver) {
        this.done = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.mainActivity = connectors.get("mainActivity");
        this.receiverConnector = connectors.get("receiverConnector");
        this.senderConnector = connectors.get("senderConnector");
        this.resolver = resolver;
        this.metaHashRetriever = metaHashRetriever;
    }

    /**
     * Retrieves the list of provenance nodes found during the crawl.
     *
     * @return A list of ProvenanceNode instances.
     */
    public List<ProvenanceNode> getNodes() {
        return this.nodes;
    }

    /**
     * Resets the internal lists of processed nodes and found nodes.
     */
    public void cleanup() {
        this.done = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    /**
     * Retrieves the precursors of a given entity and bundle, potentially including activities.
     * <p>
     * Note:
     * - The 'done' list keeps track of already processed entities to avoid reprocessing.
     * - The 'nodes' list accumulates the precursor nodes found during the crawl.
     * <p>
     * The algorithm consists of three main if statements checking for the current entity's type:
     * 1. if the type is a sender connector, it will add the found precursor into the nodes list
     * 2. if the type is a receiver connector, the traversal reached the end of the document and the method is
     *    called again with new location parameters
     * 3. if the type is neither sender of receiver connector, the algorithm will loop over other statements
     *    in the document, until it finds one of a WasDerivedFrom type which it will use to find next entity
     *
     * @param entityId The ID of the entity to start the crawl.
     * @param document A document containing the entity.
     * @param includeActivity Indicates whether to include activities in the results.
     * @param hasher The hasher instance for verifying document integrity.
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available.
     */
    public void getPrecursors(QualifiedName entityId, Document document, boolean includeActivity, HashDocument hasher) throws NoSuchAlgorithmException {
        IPidResolver pidResolver = getPidResolver(entityId);
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);
        QualifiedName entityType = getEntityType(entityId, document);
        if (entityType == null) throw new RuntimeException("entityType is null for: \n" + docBundle.getId() + "\n" + entityId);

        if (entityType.equals(this.senderConnector)) {
            if (includeActivity)
                this.nodes.add(new ProvenanceNode(entityId, docBundle.getId(), retrieveMainActivity(docBundle)));
            else this.nodes.add(new ProvenanceNode(entityId, docBundle.getId(), null));
            this.done.add(entityId);
        }
        if (entityType.equals(this.receiverConnector)) {
            this.done.add(entityId);
            document = this.resolver.load(pidResolver.getConnectorEntry(entityId, this.receiverConnector).get("referenceBundleID"));
            Document metaDocument = this.resolver.load(pidResolver.getMetaDoc(docBundle.getId()));
            String checksum = checkSum(hasher, document, metaDocument);
            if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + docBundle.getId() + "\n" + checksum + "\nTerminating traversal!");
            getPrecursors(entityId, document, includeActivity, hasher);
        } else {
            for (Statement statement : docBundle.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getGeneratedEntity().equals(entityId)))
                    continue;
                if (!(isConnector(derived.getUsedEntity(),pidResolver)))
                    continue;
                QualifiedName connector = derived.getUsedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getPrecursors(connector, document, includeActivity, hasher);
                }
            }
        }
    }

    /**
     * Retrieves the successors of a given entity and bundle, potentially including activities.
     * Note:
     * - The 'done' list keeps track of already processed entities to avoid reprocessing.
     * - The 'nodes' list accumulates the successor nodes found during the crawl.
     * <p>
     * The algorithm consists of three main if statements checking for the current entity's type:
     * 1. if the type is a receiver connector, it will add the found successor into the nodes list
     * 2. if the type is a sender connector, the traversal reached the end of the document and the method is
     *    called again with new location parameters
     * 3. if the type is neither sender of receiver connector, the algorithm will loop over other statements
     *    in the document, until it finds one of a WasDerivedFrom type which it will use to find next entity
     *
     * @param entityId The ID of the entity to start the crawl.
     * @param document A document containing the entity.
     * @param includeActivity Indicates whether to include activities in the results.
     * @param hasher The hasher instance for verifying document integrity.
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available.
     */
    public void getSuccessors(QualifiedName entityId, Document document, boolean includeActivity, HashDocument hasher) throws NoSuchAlgorithmException {
        IPidResolver pidResolver = getPidResolver(entityId);
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);
        QualifiedName entityType = getEntityType(entityId, document);
        if (entityType == null) throw new RuntimeException("entityType is null for: \n" + docBundle.getId() + "\n" + entityId);

        if (entityType.equals(this.receiverConnector)) {
            if (includeActivity)
                this.nodes.add(new ProvenanceNode(entityId, docBundle.getId(), retrieveMainActivity(docBundle)));
            else this.nodes.add(new ProvenanceNode(entityId, docBundle.getId(), null));
            this.done.add(entityId);
        }
        if (entityType.equals(this.senderConnector)) {
            this.done.add(entityId);
            document = this.resolver.load(pidResolver.getConnectorEntry(entityId, this.senderConnector).get("referenceBundleID"));
            Document metaDocument = this.resolver.load(pidResolver.getMetaDoc(docBundle.getId()));
            String checksum = checkSum(hasher, document, metaDocument);
            if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + docBundle.getId() + "\n" + checksum + "\nTerminating traversal!");
            getSuccessors(entityId, document, includeActivity, hasher);
        } else {
            for (Statement statement : docBundle.getStatement()) {
                if (!(statement instanceof WasDerivedFrom derived))
                    continue;
                if (!(derived.getUsedEntity().equals(entityId)))
                    continue;
                if (!(isConnector(derived.getGeneratedEntity(),pidResolver)))
                    continue;
                QualifiedName connector = derived.getGeneratedEntity();
                if (!(this.done.contains(connector))) {
                    this.done.add(connector);
                    getSuccessors(connector, document, includeActivity, hasher);
                }
            }
        }
    }

    /**
     * Retrieves a connector type of entity from the provided document.
     *
     * @param entityId The ID of the entity whose connector's type is to be retrieved.
     * @param document The document containing the entity.
     * @return The type of the entity as a QualifiedName, or null if the entity is not found.
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
     * Retrieves the main activity associated with a given bundle.
     *
     * @param bundle The bundle containing the activity.
     * @return A list of QualifiedNames representing the main activity or null if not found.
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

    private IPidResolver getPidResolver(QualifiedName entityId) {
        return Initializer.getMemory();
    }

    /**
     * Determines if a given entity ID represents a connector by checking its presence in the navigation table.
     *
     * @param entityId The ID of the entity to be checked.
     * @return True if the entity ID is present in the navigation table, indicating it's a connector; false otherwise.
     */
    public boolean isConnector(QualifiedName entityId,IPidResolver pidResolver) {
        for (Map<String, QualifiedName> row : pidResolver.getNavigationTable(null)) {
            if (row.get("entityID").equals(entityId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies the integrity of the provided document by comparing its hash with the hash stored in the meta document.
     *
     * @param hasher   The hasher instance for generating hash values.
     * @param document The document whose integrity is to be verified.
     * @return A string indicating the result of the checksum verification.
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available.
     */
    public String checkSum(HashDocument hasher, Document document, Document metaDocument) throws NoSuchAlgorithmException {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        InteropFramework framework = new InteropFramework();
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);

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
