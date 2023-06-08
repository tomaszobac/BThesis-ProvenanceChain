package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Identifiable;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetaBuilder {
    private final ProvFactory pFactory;
    private final Namespace namespace;
    private String prefix;
    private String nsuri;

    public MetaBuilder() {
        this.pFactory = InteropFramework.getDefaultFactory();
        namespace = new Namespace();
        namespace.addKnownNamespaces();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNsuri() {
        return nsuri;
    }

    public void setNsuri(String nsuri) {
        this.nsuri = nsuri;
    }

    public Document makeDocument(List<TooManyDocuments> resources) {
        List<StatementOrBundle> statements = new ArrayList<>();
        List<StatementOrBundle> specialization = new ArrayList<>();

        Document document = pFactory.newDocument();
        Bundle bundle = (Bundle) resources.get(0).getDocument().getStatementOrBundle().get(0);

        setPrefix(bundle.getId().getPrefix());
        setNsuri(bundle.getId().getNamespaceURI());

        namespace.register(getPrefix(), getNsuri());

        Entity master = pFactory.newEntity(namespace.qualifiedName(getPrefix(),bundle.getId().getLocalPart(),pFactory));
        resources.remove(0);

        for (TooManyDocuments resource : resources) {
            statements.add(pFactory.newEntity(namespace.qualifiedName(getPrefix(),resource.getFile().getName(),pFactory)));
        }
        for (StatementOrBundle statement : statements) {
            Identifiable temp = (Identifiable) statement;
            specialization.add(pFactory.newSpecializationOf(temp.getId(), master.getId()));
        }

        document.getStatementOrBundle().add(master);
        document.getStatementOrBundle().addAll(statements);
        document.getStatementOrBundle().addAll(specialization);
        document.setNamespace(namespace);
        return document;
    }
}
