package bthesis.metageneration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SystemFileLoader implements FileLoader{
    String path;
    List<File> Files;

    public String getPath() {
        return path;
    }

    public List<File> getFiles() {
        return Files;
    }

    public SystemFileLoader(String path) {
        this.path = path;
        Files = loadFiles(this.path);
    }

    @Override
    public List<File> loadFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);

        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            if(files != null) {
                for(File file : files) {
                    if(file.isFile() && isSupportedExtension(file)) {
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }

    private boolean isSupportedExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        }
        String ext = name.substring(lastIndexOf+1);
        return SupportedExtensions.isSupported(ext);
    }
}
