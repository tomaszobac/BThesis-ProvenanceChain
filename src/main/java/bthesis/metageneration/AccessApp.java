package bthesis.metageneration;

import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class AccessApp {

    private static void generateRuntime(String input, String output) throws IOException, NoSuchAlgorithmException {
        SystemFileLoader inputFileLoader = new SystemFileLoader(input);

        MetaGeneration generation = new MetaGeneration(inputFileLoader.getFiles());
        Document document = generation.generate();

        WriteDocument outputWriter = new WriteDocument(output, document);
        outputWriter.writeDocument();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the path to the input folder:");
        String path1 = scanner.nextLine();

        System.out.println("Please enter the path to the output folder (leave blank for the same as input):");
        String path2 = scanner.nextLine();

        if (path2.isBlank()) {
            path2 = "src/main/resources"; //path1
        }

        scanner.close();
        generateRuntime(path1,path2);
    }
}
