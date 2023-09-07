package bthesis.provenancechain;

import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import bthesis.metageneration.*;
import java.security.NoSuchAlgorithmException;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.vanilla.ProvFactory;

public class AccessApp {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        String path = "src/main/resources/bthesis-provenancechain-digpat";
        SystemFileLoader inputFileLoader = new SystemFileLoader(path);
        HashDocument hasher = new HashDocument();
        MetaBuilder meta = new MetaBuilder();
        Initializer initializer = new Initializer(hasher, meta, inputFileLoader.getFiles());
        Crawler crawler = new Crawler(initializer);
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        fakeLoading();

        while (run) {
            System.out.print("$> ");
            String command = scanner.nextLine();

            switch (command) {
                case "exit" -> run = false;
                case "previous" -> findPrecursors(crawler, false);
                case "previousact" -> findPrecursors(crawler, true);
                case "next" -> findSuccessors(crawler, false);
                case "nextact" -> findSuccessors(crawler, true);
                case "resolve" -> resolve(scanner, initializer);
                case "help" -> help();
                case "list" -> initializer.getMemory().getNavigation_table().forEach(System.out::println);
                default -> System.out.println("Unknown command\n");
            }
        }

        scanner.close();
        System.out.println("Exiting the program.");
    }

    private static QualifiedName createQN(String subject) {
        Scanner scanner = new Scanner(System.in);
        ProvFactory provFactory = new ProvFactory();

        System.out.println("Enter " + subject + " ID: ");
        String local = scanner.nextLine();
        System.out.println("Enter " + subject + " URI: ");
        String namespace = scanner.nextLine();

        return provFactory.newQualifiedName(namespace,local,null);
    }

    private static void findPrecursors(Crawler crawler, boolean find_activity) {
        crawler.getPrecursors(createQN("entity"),createQN("bundle"),find_activity);
        if (find_activity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Precursor:\n" + node.getConnector() + " from " + node.getBundle());
                System.out.println("Activities:");
                node.getActivities().forEach(System.out::println);
                System.out.println();
            }
        }
        else crawler.getNodes().forEach(item -> System.out.println(item.getConnector() + " from " + item.getBundle()));
        crawler.cleanup();
    }

    private static void findSuccessors(Crawler crawler, boolean find_activity) {
        crawler.getSuccessors(createQN("entity"),createQN("bundle"),find_activity);
        if (find_activity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Successor:\n" + node.getConnector() + " from " + node.getBundle());
                System.out.println("Activities:");
                node.getActivities().forEach(System.out::println);
                System.out.println();
            }
        }
        else crawler.getNodes().forEach(item -> System.out.println(item.getConnector() + " from " + item.getBundle()));
        crawler.cleanup();
    }

    private static void help() {
        List<String> help = new ArrayList<>(Arrays.asList(
                "previous - get entity precursors",
                "previousact - get entity precursors and their activities",
                "next - get entity successors",
                "nextact - get entity precursors and their activities",
                "resolve - get table contents for entity",
                "get - get bundle contents",
                "list - get table",
                "help - show this",
                "exit - exit the program"
                ));
        help.forEach(it -> System.out.println(it+"\n"));
    }

    private static void fakeLoading() throws InterruptedException {
        System.out.print(".\r");
        Thread.sleep(500);
        System.out.print("..\r");
        Thread.sleep(500);
        System.out.print("...\r");
        Thread.sleep(750);
        System.out.println("Data initialized");
        Thread.sleep(500);
        System.out.println("Enter a command (or 'exit' to quit): ");
    }

    private static void resolve(Scanner scanner, Initializer initializer) {
        System.out.println("Enter entity ID ('prefix:{{uri}}local'): ");
        String id = scanner.nextLine();
        for (List<QualifiedName> row : initializer.getMemory().getNavigation_table()) {
            if (row.get(0).toString().equals(id)) {
                System.out.println(row);
            }
        }
        System.out.println();
    }
}
