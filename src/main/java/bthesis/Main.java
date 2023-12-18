package bthesis;

import java.io.IOException;

import bthesis.provenancechain.MainRuntime;

/**
 * Main entry point for the application.
 *
 * @author Tomas Zobac
 */
public class Main {
    /**
     * The main method which starts the execution of the application.
     *
     * @param args Command line arguments passed to the application. (not used)
     * @throws IOException if there's an error in I/O operations during execution.
     */
    public static void main(String[] args) throws IOException {
        MainRuntime.run();
    }
}
