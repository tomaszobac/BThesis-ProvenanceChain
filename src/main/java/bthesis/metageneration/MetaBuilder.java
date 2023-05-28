package bthesis.metageneration;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;

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
        Bundle bundle = (Bundle) resources.get(0).getDocument().getStatementOrBundle().get(0);
        setPrefix(bundle.getId().getPrefix());
        setNsuri(bundle.getId().getNamespaceURI());

        Entity quote = pFactory.newEntity(namespace.qualifiedName(getPrefix(),"LittleSemanticsWeb.html",pFactory));
        quote.setValue(pFactory.newValue("A little provenance goes a long way", pFactory.getName().XSD_STRING));
        Entity original = pFactory.newEntity(namespace.qualifiedName(getPrefix(),"LittleSemanticsWeb.html",pFactory));

        WasAttributedTo attr1 = pFactory.newWasAttributedTo(null,quote.getId(), original.getId());
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(quote.getId(), original.getId());

        Document document = pFactory.newDocument();
        document.getStatementOrBundle().addAll(Arrays.asList(quote, attr1, original, wdf));
        document.setNamespace(namespace);
        return document;
    }
}
