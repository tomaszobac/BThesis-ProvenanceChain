package bthesis.provenancechain;

import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.EndOfFileException;
import org.jline.reader.impl.completer.StringsCompleter;

import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.vanilla.ProvFactory;

import bthesis.metageneration.MetaBuilder;
import bthesis.metageneration.HashDocument;
import bthesis.metageneration.SystemFileLoader;

/**
 * The AccessApp class serves as the main entry point for the application.
 * It initializes various components and provides a command-line interface
 * for interacting with the system.
 *
 * @author Tomas Zobac
 */
public class AccessApp {

    /**
     * The main method of the application.
     *
     * @param args Command-line arguments (not used).
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     * @throws InterruptedException     If the thread is interrupted.
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException {
        String path = "src/main/resources/bthesis-provenancechain-digpat";
        SystemFileLoader inputFileLoader = new SystemFileLoader(path);
        HashDocument hasher = new HashDocument();
        MetaBuilder meta = new MetaBuilder();
        Initializer initializer = new Initializer(hasher, meta, inputFileLoader.getFiles());
        Crawler crawler = new Crawler(initializer);
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        try {
            Terminal terminal = TerminalBuilder.builder().build();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(new DefaultParser())
                    .completer(new StringsCompleter("exit", "precursors", "precursors-activity", "successors",
                            "successors-activity", "resolve", "help", "list"))
                    .build();
            String prompt = "$> ";

            System.out.println("\nEnter a command (or 'exit' to quit): ");
            while (run) {
                try {
                    String line = reader.readLine(prompt);
                    switch (line) {
                        case "exit" -> run = false;
                        case "precursors" -> findPrecursors(crawler, false, hasher);
                        case "precursors-activity" -> findPrecursors(crawler, true, hasher);
                        case "successors" -> findSuccessors(crawler, false, hasher);
                        case "successors-activity" -> findSuccessors(crawler, true, hasher);
                        case "resolve" -> resolve(scanner, initializer);
                        case "help" -> help();
                        case "list" -> initializer.getMemory().getNavigation_table().forEach(System.out::println);
                        default -> System.out.println("Unknown command\n");
                    }
                } catch (EndOfFileException e) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
        System.out.println("Exiting the program.");
    }

    /**
     * Creates a QualifiedName object based on user input.
     *
     * @param subject The subject for which the QualifiedName is being created.
     * @return A new QualifiedName object.
     */
    private static QualifiedName createQN(String subject) {
        Scanner scanner = new Scanner(System.in);
        ProvFactory provFactory = new ProvFactory();

        System.out.println("Enter " + subject + " ID: ");
        String local = scanner.nextLine();
        System.out.println("Enter " + subject + " URI: ");
        String namespace = scanner.nextLine();

        return provFactory.newQualifiedName(namespace, local, null);
    }

    /**
     * Finds and displays the precursor nodes of a given entity.
     *
     * @param crawler       The Crawler object used for crawling through nodes.
     * @param find_activity If true, finds and displays activities related to the precursors.
     * @param hasher        The HashDocument object used for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    private static void findPrecursors(Crawler crawler, boolean find_activity, HashDocument hasher) throws NoSuchAlgorithmException {
        crawler.getPrecursors(createQN("entity"), createQN("bundle"), find_activity, hasher);
        if (find_activity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Precursor:\n" + node.connector() + " from " + node.bundle());
                System.out.println("Checksum:\n" + node.checksum());
                System.out.println("Activities:");
                node.activities().forEach(System.out::println);
                System.out.println();
            }
        } else crawler.getNodes().forEach(item ->
                System.out.println(item.connector() + " from " +
                        item.bundle() + "\n" + item.checksum()));
        crawler.cleanup();
    }

    /**
     * Finds and displays the successor nodes of a given entity.
     *
     * @param crawler       The Crawler object used for crawling through nodes.
     * @param find_activity If true, finds and displays activities related to the successors.
     * @param hasher        The HashDocument object used for hashing.
     * @throws NoSuchAlgorithmException If the specified algorithm does not exist.
     */
    private static void findSuccessors(Crawler crawler, boolean find_activity, HashDocument hasher) throws NoSuchAlgorithmException {
        crawler.getSuccessors(createQN("entity"), createQN("bundle"), find_activity, hasher);
        if (find_activity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Successor:\n" + node.connector() + " from " + node.bundle());
                System.out.println("Checksum:\n" + node.checksum());
                System.out.println("Activities:");
                node.activities().forEach(System.out::println);
                System.out.println();
            }
        } else crawler.getNodes().forEach(item ->
                System.out.println(item.connector() + " from " +
                        item.bundle() + "\n" + item.checksum()));
        crawler.cleanup();
    }

    /**
     * Displays a list of available commands and their descriptions.
     */
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
        help.forEach(it -> System.out.println(it + "\n"));
    }

    /**
     * Resolves and displays the table contents for a given entity ID.
     *
     * @param scanner     The Scanner object for reading user input.
     * @param initializer The Initializer object containing the application state.
     */
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
