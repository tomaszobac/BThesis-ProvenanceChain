package bthesis.metageneration;

import java.io.File;
import java.util.List;

public interface FileLoader {
    List<File> loadFiles(String path);
}
