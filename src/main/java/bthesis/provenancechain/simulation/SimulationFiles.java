package bthesis.provenancechain.simulation;

import bthesis.provenancechain.tools.loading.SupportedExtensions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * BThesis simulation file
 * Manages simulation files, providing functionalities to load and retrieve files
 * from a specified directory and its subdirectories.
 *
 * @author Tomas Zobac
 * */
public class SimulationFiles {
    List<File> files;

    /**
     * Constructor that initializes the SimulationFiles by loading files from the specified path.
     *
     * @param path The path to the directory from which the files are to be loaded.
     */
    public SimulationFiles(String path) {
        files = loadFiles(path);
    }

    /**
     * Retrieves the list of loaded simulation files.
     *
     * @return A list of loaded simulation files.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Loads simulation files from the specified path.
     *
     * @param path The path to the directory from which the files are to be loaded.
     * @return A list of loaded simulation files from the specified path.
     */
    public List<File> loadFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);

        loadFilesRecursive(directory, fileList);

        return fileList;
    }

    /**
     * Recursively loads simulation files from a specified directory and its subdirectories.
     *
     * @param directory The directory from which the files are to be loaded.
     * @param fileList The list to which the loaded files are added.
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
     * Checks if the provided file has a supported extension.
     *
     * @param file The file whose extension is to be checked.
     * @return True if the file's extension is supported, false otherwise.
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
