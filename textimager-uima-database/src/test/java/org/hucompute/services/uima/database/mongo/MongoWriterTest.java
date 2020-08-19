package org.hucompute.services.uima.database.mongo;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class MongoWriterTest {

	public static void main(String[] args) throws UIMAException {
		AnalysisEngine writer = AnalysisEngineFactory.createEngine(MongoWriter.class, 
				MongoWriter.PARAM_DB_HOST,"localhost",
				MongoWriter.PARAM_DB_USER,"root",
				MongoWriter.PARAM_DB_PW,"rootpassword",
				MongoWriter.PARAM_DB_DBNAME,"lab",
				MongoWriter.PARAM_DB_COLLECTIONNAME,"asdf"
				);
		JCas cas = JCasFactory.createText("das ist ein test.", "de");
		SimplePipeline.runPipeline(cas, writer);
	}

}
