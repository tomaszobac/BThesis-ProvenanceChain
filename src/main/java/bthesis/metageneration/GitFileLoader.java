package bthesis.metageneration;

import org.openprovenance.prov.model.Document;

import java.io.File;
import java.util.List;

public class GitFileLoader implements FileLoader {
    @Override
    public List<File> loadFiles(String path) {
        throw new UnsupportedOperationException("Git file loading not implemented yet.");
    }
}
