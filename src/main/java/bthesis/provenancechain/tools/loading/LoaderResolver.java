package bthesis.provenancechain.tools.loading;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for resolving the appropriate file loader based on the type of the provided bundle's URI.
 * Supports loading from GitLab repositories and local file systems.
 *
 * @author Tomas Zobac
 */
public class LoaderResolver {
    private IFileLoader loader;

    /**
     * Default constructor for the LoaderResolver.
     */
    public LoaderResolver() {
    }

    /**
     * Loads a Document based on the given bundle ID. The appropriate file loader
     * is determined based on the bundle's URI.
     *
     * @param bundleId The QualifiedName containing the URI of the bundle to be loaded.
     * @return The loaded Document.
     * @throws UnsupportedOperationException if the URI type is not recognized.
     */
    public Document load(QualifiedName bundleId) {
        String type = checkPath(bundleId.getUri());
        switch (type) {
            case "gitlab" -> this.loader = new GitLabFileLoader();
            case "local" -> this.loader = new SystemFileLoader();
            case "unknown" -> throw new UnsupportedOperationException("Path type not recognized: " + bundleId.getUri());
        }
        return this.loader.loadFile(bundleId.getUri());
    }

    /**
     * Checks the type of path provided and determines whether it's a GitLab path, a local file system path,
     * or an unknown type.
     *
     * @param path The path to be checked.
     * @return A string representing the type of the path ("gitlab", "local", or "unknown").
     */
    private String checkPath(String path) {
        Pattern gitPattern = Pattern.compile("^(http://|https://|git@)");
        Pattern systemPattern = Pattern.compile("^[0-9.a-zA-Z]+/|^/|^[a-zA-Z]:\\\\|^[a-zA-Z]:/[^\\\\]*|file:/+");

        Matcher gitMatcher = gitPattern.matcher(path);
        Matcher systemMatcher = systemPattern.matcher(path);

        if (gitMatcher.find()) {
            return "gitlab";
        } else if (systemMatcher.find()) {
            return "local";
        } else {
            return "unknown";
        }
    }
}
