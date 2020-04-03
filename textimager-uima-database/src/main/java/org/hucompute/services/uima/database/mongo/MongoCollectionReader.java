package org.hucompute.services.uima.database.mongo;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.json.JsonCasDeserializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.hucompute.services.uima.database.AbstractCollectionReader;
import org.json.JSONObject;

import com.mongodb.Bytes;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Reads CASes from Mongo
 * 
 * @author renaud.richardet@epfl.ch
 */
public class MongoCollectionReader extends AbstractCollectionReader {

//    public static final String PARAM_DB_CONNECTION = "mongo_connection";
//    @ConfigurationParameter(name = PARAM_DB_CONNECTION, //
//    description = "host, dbname, collectionname, user, pw")
//    protected String[] db_connection;
	
	public static final String PARAM_DB_USER = "mongo_connection_user";
	@ConfigurationParameter(name = PARAM_DB_USER)
	protected String db_connection_user;
	

	public static final String PARAM_DB_PW = "mongo_connection_pw";
	@ConfigurationParameter(name = PARAM_DB_PW)
	protected String db_connection_pw;
	
	
	public static final String PARAM_DB_HOST = "mongo_connection_host";
	@ConfigurationParameter(name = PARAM_DB_HOST)
	protected String db_connection_host;
	
	public static final String PARAM_DB_DBNAME = "mongo_connection_dbname";
	@ConfigurationParameter(name = PARAM_DB_DBNAME)
	protected String db_connection_dbname;
	
	public static final String PARAM_DB_COLLECTIONNAME = "mongo_connection_collectionname";
	@ConfigurationParameter(name = PARAM_DB_COLLECTIONNAME)
	protected String db_connection_collectionname;
	
	public static final String PARAM_CONNECTION_SAFE_MODE = "safeMode";
	@ConfigurationParameter(name = PARAM_CONNECTION_SAFE_MODE, defaultValue = "true", //
			description = "Mongo's WriteConcern SAFE(true) or NORMAL(false)")
	private boolean safeMode;
	
    protected DBCursor cur;

    public static final String PARAM_QUERY = "mongo query";
    @ConfigurationParameter(name = PARAM_QUERY, mandatory = false, //
    description = "a mongo query, e.g. {my_db_field:{$exists:true}} or {ftr.ns:1} or {pmid: 17} "
            + "or {pmid:{$in:[12,17]}} or {pmid:{ $gt: 8, $lt: 11 }} ")
    private String query = null;
    
    public static final String PARAM_LIMIT = "mongo_limit";
    @ConfigurationParameter(name = PARAM_LIMIT, mandatory = false, defaultValue = "-1")
    private int limit;
    
    public static final String PARAM_SKIP = "mongo_SKIP";
    @ConfigurationParameter(name = PARAM_LIMIT, mandatory = false, defaultValue = "0")
    private int skip;
    
    int processed = 0;
    
    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException {
    	
        super.initialize(context);
        try {
			MongoConnection conn = new MongoConnection(db_connection_host,db_connection_dbname,db_connection_collectionname,db_connection_user,db_connection_pw, safeMode);
            initQuery(conn);
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    protected void initQuery(MongoConnection conn) throws IOException {
    	System.out.println("initQuery");
    	if (query != null){
            cur = conn.coll.find((DBObject) JSON.parse(query));
            System.out.println(JSON.parse(query));
        }
        else
            cur = conn.coll.find()
//            .limit(1000)
            ;
        if(limit>0)
        	cur.limit(limit);
        if(skip>0)
        	cur.skip(skip);
        cur.addOption(Bytes.QUERYOPTION_NOTIMEOUT).batchSize(1000);
        System.out.println("size: " + cur.size());
    }

    public boolean hasNext() throws IOException, CollectionException {
        return cur.hasNext();
    }

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		
		resumeWatch();
		try {
            DBObject doc = cur.next();
            String json = JSON.serialize(doc);
            new JsonCasDeserializer().deserialize(aCAS.getJCas(), new JSONObject(json));
        } catch (Exception e) {
            throw new CollectionException(e);
        }		
		suspendWatch();
		log();
		processed++;
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(processed,
				cur.count(), Progress.ENTITIES) };
	}
}

