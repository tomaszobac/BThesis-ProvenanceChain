package bthesis.metageneration;

import org.openprovenance.prov.model.*;
import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.interop.InteropFramework;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/01/01_sample_acquisition.provn";

        ProvFactory provFactory = new ProvFactory();
        InteropFramework intF=new InteropFramework();
        Pattern pattern = Pattern.compile("\\}\\}(.+)$");

        Document document = intF.readDocumentFromFile(provNFile);
        // Retrieve bundles
        for (StatementOrBundle sob : document.getStatementOrBundle()) {
            if (sob instanceof Bundle) {
                Bundle bundle = (Bundle) sob;
                System.out.println();
                System.out.println("Bundle Id: " + bundle.getId());
                System.out.println();
                System.out.println("Bundle namespace: " + bundle.getNamespace());
                System.out.println();
                System.out.println("Bundle def namespace: " + bundle.getNamespace().getDefaultNamespace());
                System.out.println();
                System.out.println("Bundle qualified name: " + bundle.getNamespace().qualifiedName("ns_surgery", "test", provFactory));
                System.out.println();
                System.out.println("Bundle Statement: " + bundle.getStatement());
            }
        }
    }
}
