package bthesis.metageneration;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import bthesis.provenancechain.tools.security.HashDocument;

/**
 * The DocInfoExtender class is responsible for extending a provenance Document with additional information.
 * It calculates the MD5 and SHA-256 hash values for the document.
 *
 * @author Tomas Zobac
 */
public class DocInfoExtender {
    private final Document document;
    private final String md5;
    private final String sha256;

    /**
     * Initializes a new instance of the DocInfoExtender class.
     * Reads a Document from a given File and calculates its MD5 and SHA-256 hash values.
     *
     * @param hasher The HashDocument object used for hashing.
     * @param file   The File object representing the provenance document.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    public DocInfoExtender(HashDocument hasher, File file) throws NoSuchAlgorithmException {
        InteropFramework intF = new InteropFramework();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.document = intF.readDocumentFromFile(file.getAbsolutePath());
        intF.writeDocument(baos, Formats.ProvFormat.PROVN, this.document);
        this.md5 = hasher.generateMD5(baos.toByteArray());
        this.sha256 = hasher.generateSHA256(baos.toByteArray());
    }

    /**
     * Returns the Document object associated with this instance.
     *
     * @return A Document object.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns the MD5 hash value calculated for the Document.
     *
     * @return A string representing the MD5 hash value.
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Returns the SHA-256 hash value calculated for the Document.
     *
     * @return A string representing the SHA-256 hash value.
     */
    public String getSha256() {
        return sha256;
    }
}
