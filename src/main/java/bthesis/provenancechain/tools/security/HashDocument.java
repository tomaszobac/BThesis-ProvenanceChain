package bthesis.provenancechain.tools.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The HashDocument class provides utility methods for generating hash values.
 * It currently supports MD5 and SHA-256 hash algorithms.
 *
 * @author Tomas Zobac
 */
public class HashDocument {

    /**
     * Generates a MD5 hash value for the given byte array.
     *
     * @param data The byte array for which the MD5 hash is to be generated.
     * @return A string representing the MD5 hash value.
     * @throws NoSuchAlgorithmException If the MD5 algorithm is not available.
     */
    public String generateMD5(byte[] data) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    /**
     * Generates a SHA-256 hash value for the given byte array.
     *
     * @param data The byte array for which the SHA-256 hash is to be generated.
     * @return A string representing the SHA-256 hash value.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
     */
    public String generateSHA256(byte[] data) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }
}
