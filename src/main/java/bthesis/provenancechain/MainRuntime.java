package bthesis.provenancechain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
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
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.vanilla.ProvFactory;

import bthesis.metageneration.MetaBuilder;
import bthesis.provenancechain.logic.Crawler;
import bthesis.provenancechain.logic.data.ProvenanceNode;
import bthesis.provenancechain.simulation.Initializer;
import bthesis.provenancechain.simulation.SimMetaHashRetriever;
import bthesis.provenancechain.simulation.SimulationFiles;
import bthesis.provenancechain.config.ConfigLoader;
import bthesis.provenancechain.config.Configuration;
import bthesis.provenancechain.tools.loading.LoaderResolver;
import bthesis.provenancechain.tools.retrieving.IMetaHashRetriever;
import bthesis.provenancechain.tools.metadata.IPidResolver;
import bthesis.provenancechain.tools.security.IntegrityVerifier;
import bthesis.provenancechain.tools.security.HashDocument;

/**
 * Main runtime class responsible for the execution of the application.
 *
 * @author Tomas Zobac
 */
public class MainRuntime {
    private static final String path;
    private static final Crawler crawler;
    private static final Scanner scanner;
    private static final MetaBuilder meta;
    private static final Terminal terminal;
    private static final HashDocument hasher;
    private static final LineReader reader;
    private static final IPidResolver pidResolver;
    private static final SimulationFiles simulationFiles;
    private static final Map<String, QualifiedName> connectors;
    private static final IMetaHashRetriever metaHashRetriever;
    private static final LoaderResolver resolver;

    static {
        try {
            Configuration config = ConfigLoader.loadConfig();
            ProvFactory provFactory = new ProvFactory();
            connectors = new HashMap<>(){{
                put("mainActivity", provFactory.newQualifiedName(config.cpmUri, config.mainActivity, null));
                put("receiverConnector", provFactory.newQualifiedName(config.cpmUri, config.receiverConnector, null));
                put("senderConnector", provFactory.newQualifiedName(config.cpmUri, config.senderConnector, null));
                put("externalConnector", provFactory.newQualifiedName(config.cpmUri, config.externalConnector, null));
            }};
            path = config.dataPath;
            simulationFiles = new SimulationFiles(path);
            hasher = new HashDocument();
            meta = new MetaBuilder();
            metaHashRetriever = new SimMetaHashRetriever();
            new Initializer(hasher, meta, simulationFiles.getFiles(),connectors);
            pidResolver = Initializer.getMemory();
            resolver = new LoaderResolver();
            crawler = new Crawler(connectors, metaHashRetriever, resolver);
            scanner = new Scanner(System.in);
            terminal = TerminalBuilder.builder().build();
            reader = initReader();
        } catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method to run the application. It provides a command-line interface
     * to interact with various functionalities of the application.
     *
     * @throws IOException if there's an error in I/O operations.
     */
    public static void run() throws IOException {
        boolean run = true;

        try {
            System.out.println("\nEnter a command (or 'exit' to quit): ");

            while (run) {
                try {
                    String line = reader.readLine("$> ");

                    switch (line.trim()) {
                        case "exit" -> run = false;
                        case "precursors" -> findPrecursors(false);
                        case "precursors-activity" -> findPrecursors(true);
                        case "successors" -> findSuccessors(false);
                        case "successors-activity" -> findSuccessors(true);
                        case "resolve" -> resolve();
                        case "help" -> help();
                        case "list" -> pidResolver.getNavigationTable(null).forEach(System.out::println);
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
     * Initializes a LineReader to handle user input.
     *
     * @return an instance of LineReader.
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
     * Creates a QualifiedName based on user input.
     *
     * @param subject The subject name to prompt the user.
     * @return a new instance of QualifiedName.
     */
    private static QualifiedName createQN(String subject) {
        Scanner scanner = new Scanner(System.in);
        ProvFactory provFactory = new ProvFactory();

        System.out.println("Enter " + subject + " ID: ");
        String local = scanner.nextLine();
        System.out.println("Enter " + subject + " URI: ");
        String namespace = scanner.nextLine();

        if ((namespace.isBlank() || local.isBlank())) return null;
        return provFactory.newQualifiedName(namespace, local, null);
    }

    /**
     * Finds and prints the precursors of a given entity and bundle.
     *
     * @param findActivity Specifies if the activities of the precursors should be printed.
     * @throws NoSuchAlgorithmException if there's an error with the hashing algorithm.
     */
    private static void findPrecursors(boolean findActivity) throws NoSuchAlgorithmException {
        QualifiedName entity = createQN("entity");
        QualifiedName bundle = createQN("bundle");
        if (entity == null || bundle == null) {
            System.out.println("All values must be specified");
            return;
        }

        Document document = resolver.load(bundle);
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);
        Document metaDocument = resolver.load(pidResolver.getMetaDoc(docBundle.getId()));
        String checksum = IntegrityVerifier.checkSum(hasher, document, metaDocument, metaHashRetriever);

        if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + docBundle.getId() + "\n" + checksum + "\nTerminating traversal!");

        crawler.getPrecursors(entity, document, findActivity, hasher); //TODO: try/catch a vypsat alespoň to co to našlo

        String finalChecksum = "SHA256=" + "\u001B[32m" + "OK" + "\u001B[0m" + " | MD5=" + "\u001B[32m" + "OK" + "\u001B[0m";

        System.out.println();
        if (findActivity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Precursor:\n" + node.connector() + " from " + node.bundle());
                System.out.println("Checksum:\n" + finalChecksum);
                System.out.println("Activities:");
                node.activities().forEach(System.out::println);
                System.out.println();
            }
        } else {
            crawler.getNodes().forEach(item ->
                    System.out.println(item.connector() + " from " +
                            item.bundle() + "\n" + finalChecksum + "\n"));
        }
        crawler.cleanup();
    }

    /**
     * Finds and prints the successors of a given entity and bundle.
     *
     * @param findActivity Specifies if the activities of the successors should be printed.
     * @throws NoSuchAlgorithmException if there's an error with the hashing algorithm.
     */
    private static void findSuccessors(boolean findActivity) throws NoSuchAlgorithmException {
        QualifiedName entity = createQN("entity");
        QualifiedName bundle = createQN("bundle");
        if (entity == null || bundle == null) {
            System.out.println("All values must be specified");
            return;
        }

        Document document = resolver.load(bundle);
        Bundle docBundle = (Bundle) document.getStatementOrBundle().get(0);
        Document metaDocument = resolver.load(pidResolver.getMetaDoc(docBundle.getId()));
        String checksum = IntegrityVerifier.checkSum(hasher, document, metaDocument, metaHashRetriever);

        if (checksum.contains("FAILED")) throw new RuntimeException("Checksum failed for: " + docBundle.getId() + "\n" + checksum + "\nTerminating traversal!");

        crawler.getSuccessors(entity, document, findActivity, hasher);

        String finalChecksum = "SHA256=" + "\u001B[32m" + "OK" + "\u001B[0m" + " | MD5=" + "\u001B[32m" + "OK" + "\u001B[0m";

        System.out.println();
        if (findActivity) {
            for (ProvenanceNode node : crawler.getNodes()) {
                System.out.println("Successor:\n" + node.connector() + " from " + node.bundle());
                System.out.println("Checksum:\n" + finalChecksum);
                System.out.println("Activities:");
                node.activities().forEach(System.out::println);
                System.out.println();
            }
        } else crawler.getNodes().forEach(item ->
                System.out.println(item.connector() + " from " +
                        item.bundle() + "\n" + finalChecksum));
        crawler.cleanup();
    }

    /**
     * Displays the list of available commands to the user.
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
        System.out.println("-----\nYou can auto-complete the commands using the TAB key\n-----\n");
        help.forEach(it -> System.out.println(it + "\n"));
    }

    /**
     * Resolves and prints a row of a provided entity from the navigation table.
     */
    private static void resolve() {
        QualifiedName entity = createQN("entity");

        for (Map<String, QualifiedName> row : pidResolver.getNavigationTable(null)) {
            if (row.get("entityID").equals(entity)) {
                System.out.println(row);
            }
        }

        System.out.println();
    }
}
