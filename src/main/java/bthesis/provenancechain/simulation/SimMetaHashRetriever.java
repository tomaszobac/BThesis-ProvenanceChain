package bthesis.provenancechain.simulation;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bthesis.provenancechain.tools.retrieving.IMetaHashRetriever;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.QualifiedName;

public class SimMetaHashRetriever implements IMetaHashRetriever {
    @Override
    public Map<String, String> retrieveHash(Document metaDocument, QualifiedName bundleId) {
        Bundle metaBundle = (Bundle) metaDocument.getStatementOrBundle().get(0);
        for (Statement statement : metaBundle.getStatement()) {
            if (statement instanceof Entity entity) {
                if (entity.getId().equals(bundleId)) {
                    String metaSha256 = entity.getOther().get(0).getValue().toString();
                    String metaMd5 = entity.getOther().get(1).getValue().toString();

                    Pattern pattern = Pattern.compile("value=(.*?),");
                    Matcher sha256Matcher = pattern.matcher(metaSha256);
                    Matcher md5Matcher = pattern.matcher(metaMd5);

                    Map<String, String> hashes = new HashMap<>();

                    if (sha256Matcher.find()) {
                        hashes.put("sha256", sha256Matcher.group(1));
                    } else hashes.put("sha256", null);

                    if (md5Matcher.find()) {
                        hashes.put("md5", md5Matcher.group(1));
                    } else hashes.put("md5", null);

                    return hashes;
                }
            }
        }
        return null;
    }
}
