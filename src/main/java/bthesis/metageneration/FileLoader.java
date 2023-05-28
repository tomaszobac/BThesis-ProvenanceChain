package bthesis.metageneration;

import org.openprovenance.prov.model.Document;

import java.io.File;
import java.util.List;

public interface FileLoader {
    List<File> loadFiles(String path);
}
