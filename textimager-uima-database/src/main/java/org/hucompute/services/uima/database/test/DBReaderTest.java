package org.hucompute.services.uima.database.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.xmi.XmiWriter;
import org.hucompute.services.uima.database.basex.BasexCollectionReader;
import org.hucompute.services.uima.database.cassandra.CassandraCollectionReader;
import org.hucompute.services.uima.database.mongo.MongoCollectionReader;
import org.hucompute.services.uima.database.neo4j.Neo4jCollectionReader;
import org.hucompute.services.uima.database.xmi.XmiReaderModified;


public class DBReaderTest {

	public static void runTest(String reader) throws IOException, UIMAException {
		runPipeline(
				getReader(reader),
				createEngine(Counter.class),
				createEngine(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION, "testdata/output/",
						XmiWriter.PARAM_USE_DOCUMENT_ID, true,
						XmiWriter.PARAM_OVERWRITE, true)
				);
	}
	
	public static CollectionReader getReader(String reader) throws ResourceInitializationException {
		switch (reader) {
		case "XMI": return getXMIReader();
		//case "Mysql": return getMysqlReader();
		case "Basex": return getBasexReader();
		case "Cassandra": return getCassandraReader();
		case "Mongo": return getMongoReader();
		case "Neo4j": return getNeo4jReader();
		}
		return null;
	}

	public static CollectionReader getNeo4jReader() throws ResourceInitializationException{
		return  CollectionReaderFactory.createReader(Neo4jCollectionReader.class,
				Neo4jCollectionReader.PARAM_LOG_FILE_LOCATION, new File("dbtest/read/neo4j.log")
				);
	}
	
	public static CollectionReader getBasexReader() throws ResourceInitializationException{
		return  CollectionReaderFactory.createReader(BasexCollectionReader.class,
				BasexCollectionReader.PARAM_LOG_FILE_LOCATION, new File("dbtest/read/basex.log")
				);
	}

	public static CollectionReader getCassandraReader() throws ResourceInitializationException{
		return  CollectionReaderFactory.createReader(CassandraCollectionReader.class,
				CassandraCollectionReader.PARAM_LOG_FILE_LOCATION, new File("dbtest/read/cassandra.log")
				);
	}

	public static CollectionReader getXMIReader() throws ResourceInitializationException {
		return CollectionReaderFactory.createReader(XmiReaderModified.class,
				XmiReaderModified.PARAM_SOURCE_LOCATION, "testdata/biologie_sample",
				XmiReaderModified.PARAM_PATTERNS, "[+]**/*.xmi.gz",
				XmiReaderModified.PARAM_LOG_FILE_LOCATION, new File("dbtest/read/xmi.log"),
				XmiReaderModified.PARAM_LANGUAGE, "de"
				);
	}

	public static CollectionReader getMongoReader() throws ResourceInitializationException{
		return  CollectionReaderFactory.createReader(MongoCollectionReader.class,
				MongoCollectionReader.PARAM_DB_HOST, "127.0.0.1:27017",
				MongoCollectionReader.PARAM_DB_DBNAME, "test_with_indexes",
				MongoCollectionReader.PARAM_DB_COLLECTIONNAME, "wikipedia",
				MongoCollectionReader.PARAM_DB_USER, "",
				MongoCollectionReader.PARAM_DB_PW, "",
				MongoCollectionReader.PARAM_LOG_FILE_LOCATION, new File("dbtest/read/mongo.log")
				);
			}
}
