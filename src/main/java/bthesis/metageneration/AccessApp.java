package bthesis.metageneration;

import java.util.Scanner;
import java.security.NoSuchAlgorithmException;

import org.openprovenance.prov.model.Document;

/**
 * The AccessApp class serves as the main entry point for generating metaprovenance data.
 * It reads files from an input directory, generates a metaprovenance document, and writes it to an output directory.
 *
 * @author Tomas Zobac
 */
public class AccessApp {

    /**
     * The main method of the application.
     * It prompts the user for the input and output directories and then calls the generateRuntime method to perform the operation.
     *
     * @param args Command-line arguments (not used).
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the path to the input folder:");
        String path1 = scanner.nextLine();

        System.out.println("Please enter the path to the output folder (leave blank for the same as input):");
        String path2 = scanner.nextLine();

        if (path2.isBlank()) {
            path2 = path1;
        }

        scanner.close();
        generateRuntime(path1, path2);
    }

    /**
     * Generates a metaprovenance document based on the files in the input directory.
     * The generated document is then written to the output directory.
     *
     * @param input  The path to the input directory containing files to be processed.
     * @param output The path to the output directory where the generated document will be written.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    private static void generateRuntime(String input, String output) throws NoSuchAlgorithmException {
        SystemFileLoader inputFileLoader = new SystemFileLoader(input);

        HashDocument hasher = new HashDocument();
        MetaBuilder meta = new MetaBuilder();
        MetaGeneration generation = new MetaGeneration();
        Document document = generation.generate(hasher, meta, inputFileLoader.getFiles());

        WriteDocument outputWriter = new WriteDocument(output, document);
        outputWriter.writeDocument();
    }
}
