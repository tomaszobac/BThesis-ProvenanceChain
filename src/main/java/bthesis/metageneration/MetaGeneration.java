package bthesis.metageneration;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

import org.openprovenance.prov.model.Document;

import bthesis.provenancechain.tools.security.HashDocument;

/**
 * The MetaGeneration class is responsible for generating a meta-document that encapsulates
 * information about multiple provenance documents. This includes hash values for verification.
 *
 * @author Tomas Zobac
 */
public class MetaGeneration {
    List<DocInfoExtender> documents;

    /**
     * Initializes a new instance of the MetaGeneration class.
     * Sets up an empty list to store DocInfoExtender objects.
     */
    public MetaGeneration() {
        this.documents = new ArrayList<>();
    }

    /**
     * Returns the list of DocInfoExtender objects that contain the provenance documents and their hash values.
     *
     * @return A list of DocInfoExtender objects.
     */
    public List<DocInfoExtender> getDocuments() {
        return documents;
    }

    /**
     * Generates a meta-document that contains information about the list of provenance documents specified.
     * This includes reading each file, generating hash values, and calling the MetaBuilder to construct the meta-document.
     *
     * @param hasher The HashDocument object used for generating hash values.
     * @param meta   The MetaBuilder object used for creating the meta-document.
     * @param files  A list of File objects representing the provenance documents.
     * @return A Document object representing the generated meta-document.
     * @throws NoSuchAlgorithmException If the specified algorithm is not available.
     */
    public Document generate(HashDocument hasher, MetaBuilder meta, List<File> files) throws NoSuchAlgorithmException {
        for (File file : files) {
            this.documents.add(new DocInfoExtender(hasher, file));
        }
        return meta.makeDocument(getDocuments());
    }
}
