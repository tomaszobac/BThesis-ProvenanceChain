package bthesis.provenancechain.tools.loading;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoaderResolver {
    private IFileLoader loader;

    public LoaderResolver() {
    }

    public Document load(QualifiedName bundleid) {
        String type = checkPath(bundleid.getUri());
        switch (type) {
            case "git" -> this.loader = new GitFileLoader();
            case "local" -> this.loader = new SystemFileLoader();
            case "unknown" -> throw new UnsupportedOperationException("Path type not recognized: " + bundleid.getUri());
        }
        return this.loader.loadFile(bundleid.getUri());
    }

    private String checkPath(String path) {
        Pattern gitPattern = Pattern.compile("^(http://|https://|git@)");
        Pattern systemPattern = Pattern.compile("^[0-9.a-zA-Z]+/|^/|^[a-zA-Z]:\\\\|^[a-zA-Z]:/[^\\\\]*|file:/+");

        Matcher gitMatcher = gitPattern.matcher(path);
        Matcher systemMatcher = systemPattern.matcher(path);

        if (gitMatcher.find()) {
            return "git";
        } else if (systemMatcher.find()) {
            return "local";
        } else {
            return "unknown";
        }
    }
}
