package bthesis.provenancechain;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import bthesis.metageneration.*;
import org.openprovenance.prov.model.QualifiedName;

public class AccessApp {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        String path = "src/main/resources/bthesis-provenancechain-digpat";
        SystemFileLoader inputFileLoader = new SystemFileLoader(path);
        Initializer initializer = new Initializer(inputFileLoader.getFiles());
        Crawler crawler = new Crawler(initializer);

        Scanner scanner = new Scanner(System.in);

        System.out.print(".\r");
        Thread.sleep(500);
        System.out.print("..\r");
        Thread.sleep(500);
        System.out.print("...\r");
        Thread.sleep(750);
        System.out.println("Data initialized");
        Thread.sleep(500);
        System.out.println("Enter a command (or 'exit' to quit): ");
        while (true) {
            System.out.print("$> ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                break;
            } else if (command.equalsIgnoreCase("previous")) {
                System.out.println("Enter entity ID ('prefix:{{uri}}local'): ");
                String entity_id = scanner.nextLine();
                System.out.println("Enter bundle ID ('prefix:{{uri}}local'): ");
                String document_id = scanner.nextLine();
                crawler.crawl(entity_id,document_id,0);
                System.out.println(crawler.getPrec());
                crawler.cleanup();
                System.out.println();
            } else if (command.equalsIgnoreCase("next")) {
                System.out.println("Not implemented yet");
                System.out.println();
            } else if (command.equalsIgnoreCase("resolve")) {
                System.out.println("Enter entity ID ('prefix:{{uri}}local'): ");
                String id = scanner.nextLine();
                for (List<QualifiedName> row : initializer.getMemory().getNavigation_table()) {
                    if (row.get(0).toString().equals(id)) {
                        System.out.println(row);
                    }
                }
                System.out.println();
            } else if (command.equalsIgnoreCase("get")) {
                break;
            } else if (command.equalsIgnoreCase("help")) {
                System.out.println("previous - get entity precursors");
                System.out.println();
                System.out.println("next - get entity successors");
                System.out.println();
                System.out.println("resolve - get table contents for entity");
                System.out.println();
                System.out.println("get - get bundle contents");
                System.out.println();
                System.out.println("list - get table");
                System.out.println();
                System.out.println("help - show this");
                System.out.println();
                System.out.println("exit - exit the program");
                System.out.println();
            } else if (command.equalsIgnoreCase("list")) {
                for (List<QualifiedName> row : initializer.getMemory().getNavigation_table()) {
                    System.out.println(row);
                }
                System.out.println();
            } else {
                System.out.println("Unknown command");
                System.out.println();
            }
        }

        scanner.close();
        System.out.println("Exiting the program.");
    }
}
