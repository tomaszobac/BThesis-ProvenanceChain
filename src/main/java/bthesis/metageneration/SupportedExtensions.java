package bthesis.metageneration;

/**
 * The SupportedExtensions enum defines the set of file extensions that are supported for provenance documents.
 *
 * @author Tomas Zobac
 */
public enum SupportedExtensions {
    PROVX("provx"),
    XML("xml"),
    TTL("ttl"),
    JSON("json"),
    TRIG("trig"),
    PROVASN("prov-asn"),
    PROVN("provn"),
    ASN("prov-asn"),
    PN("pn"),
    RDF("rdf"),
    JSONLD("jsonld");

    private final String extension;

    /**
     * Initializes a new instance of the SupportedExtensions enum.
     *
     * @param extension The file extension string.
     */
    SupportedExtensions(String extension) {
        this.extension = extension;
    }

    /**
     * Checks whether a given file extension is supported.
     *
     * @param extension The file extension to check.
     * @return A boolean value indicating whether the extension is supported.
     */
    public static boolean isSupported(String extension) {
        for (SupportedExtensions ext : values()) {
            if (ext.getExtension().equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the file extension associated with the enum constant.
     *
     * @return A string representing the file extension.
     */
    public String getExtension() {
        return extension;
    }
}
