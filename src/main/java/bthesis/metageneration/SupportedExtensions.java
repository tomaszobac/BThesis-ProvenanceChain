package bthesis.metageneration;

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

    private String extension;

    SupportedExtensions(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isSupported(String extension) {
        for (SupportedExtensions ext : values()) {
            if (ext.getExtension().equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
