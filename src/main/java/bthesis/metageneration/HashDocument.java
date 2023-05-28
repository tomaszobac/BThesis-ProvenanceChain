package bthesis.metageneration;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashDocument {
    private String generateMD5(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(path);
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    private String generateSHA256(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(path);
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    public void addHashes(List<TooManyDocuments> documents) throws IOException, NoSuchAlgorithmException {
        for (TooManyDocuments document : documents) {
            document.setMd5(generateMD5(document.getPath()));
            document.setSha256(generateSHA256(document.getPath()));
        }
    }
}
