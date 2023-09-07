package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import java.io.File;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class DocInfoExtender {
    private final Document document;
    private final Path path;
    private String md5;
    private String sha256;

    public DocInfoExtender(HashDocument hasher, File file) throws NoSuchAlgorithmException {
        InteropFramework intF = new InteropFramework();
        this.path = Path.of(file.getAbsolutePath());
        this.document = intF.readDocumentFromFile(file.getAbsolutePath());
        this.md5 = hasher.generateMD5(this.document.toString());
        this.sha256 = hasher.generateSHA256(this.document.toString());
    }

    public Document getDocument() {
        return document;
    }

    public Path getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
}
