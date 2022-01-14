package org.hucompute.services.uima.database.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.json.JsonCasSerializerModified;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.uima.database.AbstractWriter;

import java.io.IOException;
import java.io.StringWriter;

public class MongoWriter extends AbstractWriter {

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

	public static final String PARAM_DB_AUTH_SOURCE = "mongo_connection_auth_source";
	@ConfigurationParameter(name = PARAM_DB_AUTH_SOURCE, mandatory = false, defaultValue = "admin")
	protected String db_connection_auth_source;

	private DBCollection coll;

	private JsonCasSerializerModified xcs;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		xcs = new JsonCasSerializerModified();
		xcs.setOmit0Values(true);
		xcs.setJsonContext(org.apache.uima.json.JsonCasSerializerModified.JsonContextFormat.omitContext);
//		try {
//			MongoClient mongoClient = new MongoClient( "localhost" );
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		try {
			MongoConnection conn = new MongoConnection(db_connection_host,db_connection_dbname,db_connection_collectionname,db_connection_user,db_connection_pw, safeMode, db_connection_auth_source);
			coll = conn.coll;
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		resumeWatch();
		try {

			StringWriter sw = new StringWriter();
			xcs.serialize(jCas.getCas(), sw);

			DBObject doc = (DBObject) JSON.parse(
					sw.toString()
					.replaceAll("\"begin\"", "\"b\"")
					.replaceAll("\"end\"", "\"e\"")
					.replaceAll("\"xmi:id\"", "\"xid\"")
					.replaceAll("\"sofa\"", "\"s\"")
					);
//			try{
			coll.insert(doc);
//			}
//			catch(BsonSerializationException e){
//				e.printStackTrace();
//			}
		} catch (Throwable t) {
			throw new AnalysisEngineProcessException(t);
		}
		suspendWatch();
//		log();
	}
}
