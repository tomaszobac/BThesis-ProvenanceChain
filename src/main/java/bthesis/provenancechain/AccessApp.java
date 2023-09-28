package bthesis.provenancechain;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;

import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.vanilla.ProvFactory;

import bthesis.metageneration.MetaBuilder;
import bthesis.metageneration.HashDocument;
import bthesis.metageneration.SystemFileLoader;

/**
 * The AccessApp class serves as the entry point for the application.
 * It initializes the necessary objects, and provides a user interface
 * for interacting with the application through a command-line interface.
 *
 * @author Tomas Zobac
 */
public class AccessApp {
    private static final String path;
    private static final Crawler crawler;
    private static final Scanner scanner;
    private static final MetaBuilder meta;
    private static final Terminal terminal;
    private static final HashDocument hasher;
    private static final LineReader reader;
    private static final Initializer initializer;
    private static final SystemFileLoader inputFileLoader;

    static {
        try {
            path = "src/main/resources/bthesis-provenancechain-digpat";
            inputFileLoader = new SystemFileLoader(path);
            hasher = new HashDocument();
            meta = new MetaBuilder();
            initializer = new Initializer(hasher, meta, inputFileLoader.getFiles());
            crawler = new Crawler(initializer);
            scanner = new Scanner(System.in);
            terminal = TerminalBuilder.builder().build();
            reader = initReader();
        } catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The main method is the entry point to the application.
     * It provides a command-line interface with autofill for the user to interact with the application.
     *
     * @param args The command-line arguments. Currently not used.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        boolean run = true;

        try {
            System.out.println("\nEnter a command (or 'exit' to quit): ");

            while (run) {
                try {
                    String line = reader.readLine("$> ");

                    switch (line) {
                        case "exit" -> run = false;
                        case "precursors" -> findPrecursors(false);
                        case "precursors-activity" -> findPrecursors(true);
                        case "successors" -> findSuccessors(false);
                        case "successors-activity" -> findSuccessors(true);
                        case "resolve" -> resolve();
                        case "help" -> help();
                        case "list" -> initializer.getMemory().getNavigation_table().forEach(System.out::println);
                        default -> System.out.println("Unknown command\n");
                    }
                } catch (EndOfFileException e) {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Cause: " + e.getCause());
            throw new RuntimeException(e);
        }
        System.out.println("Exiting the program.");
        scanner.close();
        terminal.close();
    }

    /**
     * Initializes a LineReader object for reading user input from the terminal.
     *
     * @return The initialized LineReader object.
     */
    private static LineReader initReader() {
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser())
                .completer(new StringsCompleter("exit", "precursors", "precursors-activity", "successors",
                        "successors-activity", "resolve", "help", "list"))
                .build();
    }

    /**
     * Prompts the user to enter information to create a QualifiedName object.
     *
     * @param subject The subject for which the QualifiedName is being created.
     * @return The created QualifiedName object.
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
     * Finds and displays the precursors of a specified entity and bundle.
     *
     * @param find_activity A boolean indicating whether to include activities in the output.
     * @throws NoSuchAlgorithmException if the algorithm used for hashing is not found.
     */
    private static void findPrecursors(boolean find_activity) throws NoSuchAlgorithmException {
        crawler.getPrecursors(createQN("entity"), createQN("bundle"), find_activity, hasher);
        System.out.println();
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
     * Finds and displays the successors of a specified entity and bundle.
     *
     * @param find_activity A boolean indicating whether to include activities in the output.
     * @throws NoSuchAlgorithmException if the algorithm used for hashing is not found.
     */
    private static void findSuccessors(boolean find_activity) throws NoSuchAlgorithmException {
        crawler.getSuccessors(createQN("entity"), createQN("bundle"), find_activity, hasher);
        System.out.println();
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
                "precursors - Prints the precursors of the provided entity and bundle",
                "precursors-activity - Prints the precursors and their activities of the provided entity and bundle",
                "successors - Prints the successors of the provided entity and bundle",
                "successors-activity - Prints the successors and their activities of the provided entity and bundle",
                "resolve - Prints a row of a provided entity from the navigational table",
                "list - Prints the contents of the navigational table",
                "help - Shows this help page",
                "exit - Exits the program"
        ));
        help.forEach(it -> System.out.println(it + "\n"));
    }

    /**
     * Prompts the user to enter an entity ID, then displays the corresponding row from the navigation table.
     */
    private static void resolve() {
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
