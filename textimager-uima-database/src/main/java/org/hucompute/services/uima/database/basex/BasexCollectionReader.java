package org.hucompute.services.uima.database.basex;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.hucompute.services.uima.database.AbstractCollectionReader;
import org.hucompute.services.uima.database.basex.BaseXClient.Query;
import org.xml.sax.SAXException;


public class BasexCollectionReader extends AbstractCollectionReader {
	
	final String dbName = "uimadatabase";
	final String host = "127.0.0.1";
	final int port = 1984;
	final String user = "admin";
	final String pass = "admin";

	BaseXClient session = null;
	Query query = null;
	int queryStart = 1;
	
	boolean lastQueryEmpty = false;
	
	final String input = "declare namespace xmi=\"http://www.omg.org/XMI\"; declare variable $start external; declare variable $limit external; let $xmis := for $xmi at $count in subsequence(/xmi:XMI, $start, $limit) return $xmi return $xmis";
		
    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException {
        super.initialize(context);
        //initialize jdbc connection
        //get curser of data.       
		try {
			session = new BaseXClient(host, port, user, pass);
			// Create DB if not exists
			session.execute("CHECK " + dbName);
			query = session.query(input);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    //bearbeiten
    public boolean hasNext() throws IOException, CollectionException {
    	return !lastQueryEmpty;
    }

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		resumeWatch();

        query.bind("$limit", "1", "xs:integer");
        query.bind("$start", new Integer(queryStart++).toString(), "xs:integer");
		String next = query.execute();
		
		try {
			XmiCasDeserializer.deserialize(new ByteArrayInputStream(next.getBytes()), aCAS);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		lastQueryEmpty = next.isEmpty();
		suspendWatch();
		log();
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}
}
