package bthesis;

import org.openprovenance.prov.model.*;
import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.interop.InteropFramework;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/test.provn";

        ProvFactory provFactory = new ProvFactory();
        InteropFramework intF=new InteropFramework();
        Pattern pattern = Pattern.compile("\\}\\}(.+)$");
        File file = new File(provNFile);

        Document document = intF.readDocumentFromFile(provNFile);
        Bundle bundle = (Bundle) document.getStatementOrBundle().get(1);
        System.out.println();
        System.out.println("Bundle ID: " + bundle.getId());
        System.out.println();
        System.out.println("Bundle prefix: " + bundle.getId().getPrefix());
        System.out.println();
        System.out.println("Bundle local: " + bundle.getId().getLocalPart());
        System.out.println();
        System.out.println("Bundle nsURI: " + bundle.getId().getNamespaceURI());
        System.out.println();
        System.out.println("Bundle URI: " + bundle.getId().getUri());
        System.out.println();
        System.out.println("Bundle namespace: " + bundle.getNamespace());
        System.out.println();
        System.out.println("Bundle def namespace: " + bundle.getNamespace().getDefaultNamespace());
        System.out.println();
        System.out.println("Bundle qualified name: " + bundle.getNamespace().qualifiedName("ns_surgery", "test", provFactory));
        System.out.println();
        System.out.println("Bundle Statement: " + bundle.getStatement());
        System.out.println();
        System.out.println(file.getName());
    }
}
