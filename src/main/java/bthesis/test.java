package bthesis;

import org.apache.commons.io.IOUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.interop.InteropFramework;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/01/01_sample_acquisition.provn";


        RepositoryFile file = null;
        Document document = null;
        InteropFramework intF=new InteropFramework();

        try (GitLabApi gitLabApi = new GitLabApi("https://gitlab.ics.muni.cz/", "usLuqR2Km1yvSAkssYSe")) {
            file = gitLabApi.getRepositoryFileApi().getFile("396340/bthesis-provenancechain-digpat", "01/01_sample_acquisition.provn", "master");
            document = intF.deserialiseDocument(new ByteArrayInputStream(file.getDecodedContentAsBytes()), Formats.ProvFormat.PROVN);
        } catch (IOException | GitLabApiException e) {
            throw new RuntimeException(e);
        }



        //Document document = intF.readDocumentFromFile(provNFile);
        ProvFactory provFactory = new ProvFactory();
        IndexedDocument indexedDocument = new IndexedDocument(provFactory,document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        intF.writeDocument(baos, Formats.ProvFormat.PROVN, document);
        byte[] byteArray = baos.toByteArray();
        /*System.out.println(test.class.getResource("bthesis-provenancechain-digpat"));
        System.out.println(test.class.getResource("bthesis-provenancechain-digpat").toString());
        System.out.println(test.class.getResource("bthesis-provenancechain-digpat").toExternalForm());*/
        System.out.println(colortest());
        System.out.println("full doc: " + document.toString());
        //System.out.println("ID type: " + indexedDocument.getEntity("externalInput").getType());
        /*org.openprovenance.prov.model.Attribute attribute = indexedDocument.getEntity("sampleConnector").getType().get(0);
        System.out.println("ID type: " + attribute.getValue());*/

        Bundle bundle = (Bundle) document.getStatementOrBundle().get(0);
        for (Statement statement : bundle.getStatement()) {
            if (statement instanceof WasDerivedFrom) {
                WasDerivedFrom derived = (WasDerivedFrom) statement;
                System.out.println("WasDerivedFrom operations:");
                System.out.println("getGeneratedEntity: " + derived.getGeneratedEntity());
                System.out.println("getUsedEntity: " + derived.getUsedEntity());
                System.out.println();
            }
            else if (statement instanceof Activity) {
                Activity activity = (Activity) statement;
                System.out.println("Activity class: " + activity.getType().get(0).getValue().getClass());
                QualifiedName mainActivity = provFactory.newQualifiedName("cpm_uri", "mainActivity","cpm");
                QualifiedName mainActivity2 = new org.openprovenance.prov.vanilla.QualifiedName("cpm_uri", "mainActivity","");
                activity.getOther().forEach(x -> System.out.println(x.getValue() + " + " + x.getValue().getClass()));
            }
            else if (statement instanceof Entity) {
                Entity entity = (Entity) statement;
                System.out.println("Entity operations:");
                System.out.println("entity ID: " + entity.getId());
                System.out.println("entity type: " + entity.getType());
                if (entity.getOther().isEmpty()) continue;
                System.out.println("entity type: " + entity.getOther().get(0).getValue());
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
        System.out.println(file.getName()); //canon-test.provn*/
    }

    public static String colortest() {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_GREEN+"test"+ANSI_RESET;
    }
}
