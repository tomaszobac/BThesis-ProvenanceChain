package bthesis.provenancechain.tools.retrieving;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.util.Map;

/**
 * Represents an interface for retrieving hash values from meta documents.
 *
 * @author Tomas Zobac
 */
public interface IMetaHashRetriever {
    /**
     * Retrieves the hash values from the provided meta document based on a specific bundle ID.
     *
     * @param metaDocument The document containing meta information.
     * @param bundleId The specific ID of the bundle for which the hash should be retrieved.
     * @return A map containing key-value pairs where the key represents the name of the hash (sha256 for example)
     * and the value represents its corresponding hash.
     */
    Map<String,String> retrieveHash(Document metaDocument, QualifiedName bundleId);
}
