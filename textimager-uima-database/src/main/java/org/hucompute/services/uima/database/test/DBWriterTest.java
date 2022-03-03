package org.hucompute.services.uima.database.test;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.uima.database.basex.BasexWriter;
import org.hucompute.services.uima.database.cassandra.CassandraWriter;
import org.hucompute.services.uima.database.mongo.MongoWriter;
import org.hucompute.services.uima.database.neo4j.Neo4jWriter;
import org.hucompute.services.uima.database.xmi.XmiReaderModified;
import org.hucompute.services.uima.database.xmi.XmiWriterModified;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

public class DBWriterTest {
	public static void runTest(String writer) throws UIMAException, IOException {

		CollectionReader reader = CollectionReaderFactory.createReader(
				XmiReaderModified.class,
				XmiReaderModified.PARAM_PATTERNS, "[+]**/*.xmi.gz",
				XmiReaderModified.PARAM_SOURCE_LOCATION, "testdata/biologie_sample",
				XmiReaderModified.PARAM_LANGUAGE, "de");

		runPipeline(reader, getWriter(writer));

	}

	public static AnalysisEngine getWriter(String writer) throws ResourceInitializationException {
		switch (writer) {
		case "XMI": return getXMIWriter();
		//case "Mysql": return getMysqlWriter();
		case "Basex": return getBasexWriter();
		case "Cassandra": return getCassandraWriter();
		case "Mongo": return getMongoWriter();
		case "Neo4j": return getNeo4JWriter();
		}
		return null;
	}

	/*public static AnalysisEngine getMysqlWriter() throws ResourceInitializationException{
		return createEngine(MysqlWriter.class);
	}*/

	public static AnalysisEngine getBasexWriter() throws ResourceInitializationException{
		return createEngine(BasexWriter.class, BasexWriter.PARAM_LOG_FILE_LOCATION,new File("dbtest/writer/basex.log"));
	}

	public static AnalysisEngine getNeo4JWriter() throws ResourceInitializationException{
		return createEngine(Neo4jWriter.class,
				Neo4jWriter.PARAM_LOG_FILE_LOCATION,new File("dbtest/writer/neo4j.log"));
	}

	public static AnalysisEngine getCassandraWriter() throws ResourceInitializationException{
		return createEngine(CassandraWriter.class, CassandraWriter.PARAM_LOG_FILE_LOCATION,new File("dbtest/writer/cassandra.log"));
	}

	public static AnalysisEngine getMongoWriter() throws ResourceInitializationException{
		return createEngine(
				MongoWriter.class,
				MongoWriter.PARAM_DB_HOST, "127.0.0.1:27017",
				MongoWriter.PARAM_DB_DBNAME, "test_with_indexes",
				MongoWriter.PARAM_DB_COLLECTIONNAME, "wikipedia",
				MongoWriter.PARAM_DB_USER, "",
				MongoWriter.PARAM_DB_PW, "",
				MongoWriter.PARAM_LOG_FILE_LOCATION,new File("dbtest/mongo_with_index.log"));
	}

	public static AnalysisEngine getXMIWriter() throws ResourceInitializationException{
		return createEngine(
				XmiWriterModified.class,
				XmiWriterModified.PARAM_TARGET_LOCATION,"testdata/output",
				XmiWriterModified.PARAM_USE_DOCUMENT_ID,true,
				XmiWriterModified.PARAM_OVERWRITE,true,
				XmiWriterModified.PARAM_LOG_FILE_LOCATION,new File("dbtest/writer/xmi.log")
				);
	}

}
