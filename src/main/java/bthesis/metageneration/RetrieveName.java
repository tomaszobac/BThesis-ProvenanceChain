package bthesis.metageneration;

import org.antlr.runtime.RecognitionException;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.notation.PROV_NParser;
import org.openprovenance.prov.notation.Utility;

public class RetrieveName {
    private final ProvFactory pFactory;
    public RetrieveName(ProvFactory pFactory) {
        this.pFactory = pFactory;
    }

    
    public void doConversions(String filein) {
        InteropFramework intF=new InteropFramework();
        Document document=intF.readDocumentFromFile(filein);
        System.out.println(document.getNamespace());
        //Bundle bundle = new Bundle(document.getStatementOrBundle());
        //System.out.println(bundle.getId());
        ProvFactory provFactory = new org.openprovenance.prov.xml.ProvFactory();
        Utility utility = new Utility();
        PROV_NParser parser = utility.getParserForFile(filein);
        try {
            System.out.println(parser.bundle().getTree().getClass());
        } catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
    }

    
    public static void main(String [] args) {
        if (args.length!=1) throw new UnsupportedOperationException("main to be called with two filenames");
        String filein=args[0];
        
        RetrieveName tutorial=new RetrieveName(InteropFramework.getDefaultFactory());
        tutorial.doConversions(filein);
    }

}
