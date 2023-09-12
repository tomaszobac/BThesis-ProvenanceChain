package bthesis.metageneration;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * The SystemFileLoader class implements the FileLoader interface and provides
 * functionality to load files from the local file system.
 *
 * @author Tomas Zobac
 */
public class SystemFileLoader implements FileLoader {
    List<File> files;

    /**
     * Initializes a new instance of the SystemFileLoader class.
     * Calls the loadFiles method to populate the internal files list.
     *
     * @param path The path to the directory containing files to be loaded.
     */
    public SystemFileLoader(String path) {
        files = loadFiles(path);
    }

    /**
     * Returns the list of files loaded from the specified directory.
     *
     * @return A list of File objects representing the loaded files.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Loads files from a given directory and returns them as a list of File objects.
     *
     * @param path The path to the directory containing the files to be loaded.
     * @return A list of File objects representing the loaded files.
     */
    @Override
    public List<File> loadFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);

        loadFilesRecursive(directory, fileList);

        return fileList;
    }

    /**
     * Recursively loads files from a given directory and adds them to the provided fileList.
     *
     * @param directory The directory from which files are to be loaded.
     * @param fileList  The list where loaded files will be added.
     */
    private void loadFilesRecursive(File directory, List<File> fileList) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadFilesRecursive(file, fileList);
                    } else if (isSupportedExtension(file)) {
                        fileList.add(file);
                    }
                }
            }
        } else if (isSupportedExtension(directory)) {
            fileList.add(directory);
        }
    }

    /**
     * Checks if a given file has a supported extension.
     *
     * @param file The file to check.
     * @return A boolean value indicating whether the file has a supported extension.
     */
    private boolean isSupportedExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        }
        String ext = name.substring(lastIndexOf + 1);
        return SupportedExtensions.isSupported(ext);
    }
}
