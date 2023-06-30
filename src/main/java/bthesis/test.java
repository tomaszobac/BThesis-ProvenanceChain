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
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/01/01_sample_acquisition.provn";

        ProvFactory provFactory = new ProvFactory();
        InteropFramework intF=new InteropFramework();
        //Pattern pattern = Pattern.compile("\\}\\}(.+)$");
        //File file = new File(provNFile);


        /*for (StatementOrBundle sob : document.getStatementOrBundle()) {
            if (sob instanceof org.openprovenance.prov.model.Entity) {
                Entity e = (Entity) sob;
                for (org.openprovenance.prov.model.Attribute attr : e.getOther()) {
                    if (attr.getQualifiedName(attr.getKind()).getLocalPart().equals("prov:type")) {
                        if (attr.getValue().toString().equals("cpm:senderConnector")) {
                            System.out.println("Entity with prov:type=senderconnector: " + e.getId().getUri());
                        }
                    }
                }
            }
        }*/

        AttributeProcessor attributeProcessor = new AttributeProcessor()
        Document document = intF.readDocumentFromFile(provNFile);
        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof Entity) { // Check if the statement is an Entity
                Entity entity = (Entity) statement;
                System.out.println("entity: " + entity.getId());
                System.out.println("entity: " + entity.getOther());
                System.out.println("entity: " + entity.getLocation());
                for (org.openprovenance.prov.model.Attribute attr : entity.getOther()) {
                    System.out.println("attribute: " + attr.getValue());
                    /*if (attr.getQualifiedName(attr.getKind()).getLocalPart().equals("prov:type")) {
                        if (attr.getValue().toString().equals("cpm:senderConnector")) {
                            System.out.println("Entity with prov:type=senderconnector: " + entity.getId().getUri());
                        }
                    }*/
                }
                System.out.println();
            }
        }

        /*System.out.println();
        System.out.println("Bundle ID: " + document.getNamespace().getDefaultNamespace()); //null
        System.out.println();
        System.out.println("Bundle ID: " + document.getNamespace().toString()); //[Namespace (null) {xsd=http://www.w3.org/2001/XMLSchema#, prov=http://www.w3.org/ns/prov#, ns_surgery=https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/01/}, parent: null]
        System.out.println();
        System.out.println("Bundle ID: " + document.getNamespace().getParent()); //null
        System.out.println();
        System.out.println("Bundle ID: " + bundle.getId()); //'ns_surgeryy:{{https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/012/}}01_sample_acquisition2.provn'
        System.out.println();
        System.out.println("Bundle prefix: " + bundle.getId().getPrefix()); //ns_surgeryy
        System.out.println();
        System.out.println("Bundle local: " + bundle.getId().getLocalPart()); //01_sample_acquisition2.provn
        System.out.println();
        System.out.println("Bundle nsURI: " + bundle.getId().getNamespaceURI()); //https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/012/
        System.out.println();
        System.out.println("Bundle URI: " + bundle.getId().getUri()); //https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/012/01_sample_acquisition2.provn
        System.out.println();
        System.out.println("Bundle namespace: " + bundle.getNamespace()); //[Namespace (null) {dct=http://purl.org/dc/terms/, xsd=http://www.w3.org/2001/XMLSchema#, cpm=cpm_uri, prov=http://www.w3.org/ns/prov#, ns_pathology=pathology_uri, ns_surgeryy=https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/012/}, parent: [Namespace (null) {xsd=http://www.w3.org/2001/XMLSchema#, prov=http://www.w3.org/ns/prov#, ns_surgery=https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/01/}, parent: null]]
        System.out.println();
        System.out.println("Bundle def namespace: " + bundle.getNamespace().getDefaultNamespace()); //null
        System.out.println();
        System.out.println("Bundle qualified name: " + bundle.getNamespace().qualifiedName("ns_surgery", "test", provFactory)); //'ns_surgery:{https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/01/}test'
        System.out.println();
        System.out.println("Bundle Statement: " + bundle.getStatement()); //celý file (není stejný jako origo)
        System.out.println();
        System.out.println(file.getName()); //test.provn*/
    }
}
