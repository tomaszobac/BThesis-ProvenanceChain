package bthesis;

import org.openprovenance.prov.model.*;
import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.interop.InteropFramework;

import javax.xml.namespace.QName;

public class test {
    public static void main(String[] args) {
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/01/01_sample_acquisition.provn";

        ProvFactory provFactory = new ProvFactory();
        InteropFramework intF=new InteropFramework();
        Document document = intF.readDocumentFromFile(provNFile);
        //Document document = intF.readDocument("https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/raw/master/01/01_sample_acquisition.provn");

        //IndexedDocument indexedDocument = new IndexedDocument(provFactory,document);
        //System.out.println("ID type: " + indexedDocument.getEntity(qualifiedName).getType());
        /*org.openprovenance.prov.model.Attribute attribute = indexedDocument.getEntity("sampleConnector").getType().get(0);
        System.out.println("ID type: " + attribute.getValue());*/

        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        QualifiedName externalInputConnector = provFactory.newQualifiedName("cpm_uri", "externalInputConnector","cpm");
        QualifiedName externalInputConnector2 = provFactory.newQualifiedName(new QName("cpm_uri", "externalInputConnector","cpm"));
        QualifiedName externalInputConnector3 = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "externalInputConnector","cpm");
        System.out.println(externalInputConnector.toString());
        System.out.println(externalInputConnector2);
        System.out.println(externalInputConnector3);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof WasDerivedFrom) {
                WasDerivedFrom derived = (WasDerivedFrom) statement;
                System.out.println("WasDerivedFrom operations:");
                System.out.println("getGeneratedEntity: " + derived.getGeneratedEntity());
                System.out.println("getUsedEntity: " + derived.getUsedEntity());
                System.out.println();
            }
            else if (statement instanceof Entity) {
                Entity entity = (Entity) statement;
                System.out.println("Entity operations:");
                System.out.println("entity ID: " + entity.getId());
                System.out.println("entity type: " + entity.getType());
                /*if (entity.getType().get(0).getValue().toString().equals("'cpm:{{cpm_uri}}senderConnector'")){
                    QualifiedName qn = (QualifiedName) entity.getType().get(0).getValue();
                    System.out.println("True");
                    System.out.println(qn.getPrefix());
                    System.out.println(qn.getLocalPart());
                } else {
                    System.out.println("False");
                    System.out.println(entity.getType().get(0).getValue().toString());
                }*/
                System.out.println();
                /*System.out.println("entity other: " + entity.getOther());
                System.out.println("entity location: " + entity.getLocation());
                System.out.println("AttributeProcessor operations:");
                AttributeProcessor attributeProcessor = new AttributeProcessor(entity.getOther());
                System.out.println("AP keys: " + attributeProcessor.attributesWithNamespace("cpm_uri").keySet());
                System.out.println("AP elements: " + attributeProcessor.attributesWithNamespace("cpm_uri").get("receiverBundleId"));
                System.out.println();*/
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
