package bthesis.provenancechain.tools.retrieving;

import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.util.Map;

public interface IMetaHashRetriever {
    Map<String,String> retrieveHash(Document metaDocument, QualifiedName bundleId);
}
