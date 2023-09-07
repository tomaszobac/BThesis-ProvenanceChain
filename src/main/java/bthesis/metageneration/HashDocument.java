package bthesis.metageneration;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashDocument {
    public String generateMD5(String documentContents) throws NoSuchAlgorithmException {
        byte[] data = documentContents.getBytes();
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    public String generateSHA256(String documentContents) throws NoSuchAlgorithmException {
        byte[] data = documentContents.getBytes();
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }
}
