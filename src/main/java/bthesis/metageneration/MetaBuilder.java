package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MetaBuilder {
    private final ProvFactory pFactory;
    private final Namespace namespace;

    public MetaBuilder() {
        this.pFactory = InteropFramework.getDefaultFactory();
        this.namespace = new Namespace();
        this.namespace.addKnownNamespaces();
    }

    public Document makeDocument(List<DocInfoExtender> resources) {
        Map<Statement, Statement> statements = new LinkedHashMap<>();
        List<Statement> specialization = new ArrayList<>();

        Document document = pFactory.newDocument();
        Namespace bundlens = new Namespace();
        bundlens.register("meta", "META_URI");
        namespace.register("meta", "META_URI");
        namespace.register("hash", "HASH_URI");

        for (DocInfoExtender resource : resources) {
            Bundle bundle = (Bundle) resource.getDocument().getStatementOrBundle().get(0);
            namespace.register(bundle.getId().getPrefix(), bundle.getId().getNamespaceURI());
            Entity bundletemp = pFactory.newEntity(namespace.qualifiedName(bundle.getId().getPrefix(),"abstact_" + bundle.getId().getLocalPart(),pFactory));
            Entity temp = pFactory.newEntity(namespace.qualifiedName(bundle.getId().getPrefix(),bundle.getId().getLocalPart(),pFactory));
            pFactory.addAttribute(temp, pFactory.newOther("HASH_URI", "sha256", "hash", resource.getSha256(), null));
            pFactory.addAttribute(temp, pFactory.newOther("HASH_URI", "md5", "hash", resource.getMd5(), null));
            statements.put(bundletemp, temp);
        }
        for (Statement statement : statements.keySet()) {
            Identifiable bundletemp = (Identifiable) statement;
            Identifiable temp = (Identifiable) statements.get(statement);
            specialization.add(pFactory.newSpecializationOf(temp.getId(), bundletemp.getId()));
        }

        //document.setNamespace(namespace);
        document.setNamespace(bundlens);
        Bundle metabundle = pFactory.newNamedBundle(bundlens.qualifiedName("meta", "metabundle", pFactory),namespace,null);
        document.getStatementOrBundle().add(metabundle);
        metabundle.getStatement().addAll(statements.keySet());
        metabundle.getStatement().addAll(statements.values());
        metabundle.getStatement().addAll(specialization);
        return document;
    }
}
