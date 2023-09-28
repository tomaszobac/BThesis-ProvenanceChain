package bthesis;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.List;

public class ProvNormalization {
    public static final String PROVBOOK_NS = "http://www.provbook.org";
    public static final String PROVBOOK_PREFIX = "provbook";

    public static final String JIM_PREFIX = "jim";
    public static final String JIM_NS = "http://www.cs.rpi.edu/~hendler/";

    private final ProvFactory pFactory;
    private final Namespace ns;
    public ProvNormalization(ProvFactory pFactory) {
        this.pFactory = pFactory;
        ns=new Namespace();
        ns.addKnownNamespaces();
        ns.register(PROVBOOK_PREFIX, PROVBOOK_NS);
        ns.register(JIM_PREFIX, JIM_NS);
    }

    public static void main(String[] args) {
        String provNFilePath = "src/main/resources/canon-test.provn";
        String fileout = "target/canon-test.provn";

        ProvNormalization tutorial=new ProvNormalization(InteropFramework.getDefaultFactory());

        ProvFactory provFactory = InteropFramework.getDefaultFactory();
        InteropFramework interopFramework = new InteropFramework();

        Document document = interopFramework.readDocumentFromFile(provNFilePath);

        List<StatementOrBundle> statements = document.getStatementOrBundle();

        Document mergedDocument = provFactory.newDocument();
        for (StatementOrBundle statement : statements) {
            mergedDocument.getStatementOrBundle().add(statement);
        }

        interopFramework.writeDocument(fileout,mergedDocument);
    }
}
