package bthesis;

import bthesis.provenancechain.tools.loading.LoaderResolver;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.interop.InteropFramework;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        String provNFile = "src/main/resources/bthesis-provenancechain-digpat/01/01_sample_acquisition.provn";

        InteropFramework intF=new InteropFramework();
        /*RepositoryFile file = null;
        Document document = null;
        URL testurl = null;

        try {
            testurl = new URL("https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/blob/master/01/01_sample_acquisition.provn");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String[] pathSegments = testurl.getPath().split("/");
        StringBuilder filePath = new StringBuilder();
        for (int i = 6; i != pathSegments.length; i++) filePath.append("/").append(pathSegments[i]);
        filePath.deleteCharAt(0);
        System.out.println(filePath);
        try (GitLabApi gitLabApi = new GitLabApi(testurl.getProtocol()+"://"+testurl.getHost(), "usLuqR2Km1yvSAkssYSe")) {
            file = gitLabApi.getRepositoryFileApi().getFile(pathSegments[1]+"/"+pathSegments[2], filePath.toString(), pathSegments[5]);
            document = intF.deserialiseDocument(new ByteArrayInputStream(file.getDecodedContentAsBytes()), Formats.ProvFormat.PROVN);
        } catch (IOException | GitLabApiException e) {
            throw new RuntimeException(e);
        }*/

        System.out.println("file:///C:/sa/sas/asa.provn".replaceAll("file:/+", ""));

        Pattern systemPattern = Pattern.compile("^[.0-9a-zA-Z]+/|^/|^[a-zA-Z]:\\\\|file:/+");

        Matcher mSYSTEM = systemPattern.matcher("src/main/resources/meta_provenance.provn");
        Document iddocument = intF.readDocumentFromFile("C:\\Users\\Tom\\Documents\\bthesis-provenancechain\\src\\main\\resources\\meta_provenance.provn");
        Bundle idbundle = (Bundle) iddocument.getStatementOrBundle().get(0);

        ProvFactory provFactory = new ProvFactory();
        LoaderResolver resolver = new LoaderResolver();
        System.out.println(idbundle.getId());
        Document document = resolver.load(provFactory.newQualifiedName(
                "https://gitlab.ics.muni.cz/396340/bthesis-provenancechain-digpat/-/tree/master/04/",
                "06_evaluation.provn",
                null));


        //Document document = intF.readDocumentFromFile(provNFile);
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
        System.out.println(file.getName()); //canon-test.provn

        Pattern gitPattern = Pattern.compile("^(http://|https://|git@)");
        Pattern windowsPathPattern = Pattern.compile("^[a-zA-Z]:\\\\");
        Pattern unixPathPattern = Pattern.compile("^/");
        Pattern systemUriPattern = Pattern.compile("file:/+");
        Pattern systemPattern = Pattern.compile("^[0-9.a-zA-Z]+/|^/|^[a-zA-Z]:\\\\|^[a-zA-Z]:/[^\\\\]*");

        Matcher gitMatcher = gitPattern.matcher(path);
        Matcher systemMatcher = systemPattern.matcher(path);
        Matcher windowsPathMatcher = windowsPathPattern.matcher(path);
        Matcher unixPathMatcher = unixPathPattern.matcher(path);
        Matcher systemUriMatcher = systemUriPattern.matcher(path);
        */
    }

    public static String colortest() {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_GREEN+"test"+ANSI_RESET;
    }
}
