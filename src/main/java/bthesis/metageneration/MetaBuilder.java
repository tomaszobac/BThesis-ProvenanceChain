package bthesis.metageneration;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Identifiable;
import org.openprovenance.prov.interop.InteropFramework;

/**
 * The MetaBuilder class is responsible for creating a meta-document that
 * contains information about multiple provenance documents, including their
 * hash values for verification.
 *
 * @author Tomas Zobac
 */
public class MetaBuilder {
    private final ProvFactory pFactory;
    private final Namespace namespace;

    /**
     * Initializes a new instance of the MetaBuilder class.
     * Sets up the Provenance Factory and initializes the namespace.
     */
    public MetaBuilder() {
        this.pFactory = InteropFramework.getDefaultFactory();
        this.namespace = new Namespace();
        this.namespace.addKnownNamespaces();
    }

    /**
     * Creates a new Document that serves as a meta-document containing
     * information about the given provenance documents.
     *
     * @param resources A list of DocInfoExtender objects, each containing a
     *                  provenance document and its hash values.
     * @return A Document object representing the meta-document.
     */
    public Document makeDocument(List<DocInfoExtender> resources) {
        Map<Statement, Statement> statements = new LinkedHashMap<>();
        List<Statement> specialization = new ArrayList<>();

        Document document = pFactory.newDocument();
        Namespace bundleNamespace = new Namespace();
        bundleNamespace.register("meta", "src/main/resources/");
        namespace.register("meta", "src/main/resources/");
        namespace.register("hash", "HASH_URI");

        for (DocInfoExtender resource : resources) {
            Bundle bundle = (Bundle) resource.getDocument().getStatementOrBundle().get(0);
            namespace.register(bundle.getId().getPrefix(), bundle.getId().getNamespaceURI());
            Entity bundleTemp = pFactory.newEntity(namespace.qualifiedName(bundle.getId().getPrefix(), "abstact_" + bundle.getId().getLocalPart(), pFactory));
            Entity temp = pFactory.newEntity(namespace.qualifiedName(bundle.getId().getPrefix(), bundle.getId().getLocalPart(), pFactory));
            pFactory.addAttribute(temp, pFactory.newOther("HASH_URI", "sha256", "hash", resource.getSha256(), null));
            pFactory.addAttribute(temp, pFactory.newOther("HASH_URI", "md5", "hash", resource.getMd5(), null));
            statements.put(bundleTemp, temp);
        }
        for (Statement statement : statements.keySet()) {
            Identifiable bundleTemp = (Identifiable) statement;
            Identifiable temp = (Identifiable) statements.get(statement);
            specialization.add(pFactory.newSpecializationOf(temp.getId(), bundleTemp.getId()));
        }

        document.setNamespace(bundleNamespace);
        Bundle metaBundle = pFactory.newNamedBundle(bundleNamespace.qualifiedName("meta", "meta_provenance.provn", pFactory), namespace, null);
        document.getStatementOrBundle().add(metaBundle);
        metaBundle.getStatement().addAll(statements.keySet());
        metaBundle.getStatement().addAll(statements.values());
        metaBundle.getStatement().addAll(specialization);
        return document;
    }
}
