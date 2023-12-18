package bthesis.provenancechain.tools.security;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;

import bthesis.provenancechain.tools.retrieving.IMetaHashRetriever;

/**
 * Class responsible for verifying the integrity of a document by comparing
 * its hash with the hash stored in a meta document.
 *
 * @author Tomas Zobac
 */
public class IntegrityVerifier {
    /**
     * Generates the hash values for the document using the HashDocument instance,
     * retrieves the hash values from the meta document using the IMetaHashRetriever instance,
     * and compares the generated hash values with the retrieved hash values.
     *
     * @param hasher   The hasher instance for generating hash values.
     * @param document The document whose integrity is to be verified.
     * @return A string indicating the result of the checksum verification.
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available.
     */
    public static String checkSum(HashDocument hasher,
                                  Document document, Document metaDocument,
                                  IMetaHashRetriever metaHashRetriever) throws NoSuchAlgorithmException {
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
