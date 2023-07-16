package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import java.io.File;
import java.nio.file.Path;

public class TooManyDocuments {
    private Document document;
    private Path path;
    private String md5;
    private String sha256;

    public TooManyDocuments(File file) {
        InteropFramework intF = new InteropFramework();
        this.path = Path.of(file.getAbsolutePath());
        this.document = intF.readDocumentFromFile(file.getAbsolutePath());
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
