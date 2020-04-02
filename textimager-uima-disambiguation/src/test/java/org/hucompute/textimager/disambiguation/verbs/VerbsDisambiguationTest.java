package org.hucompute.textimager.disambiguation.verbs;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import static org.junit.Assert.*;


public class VerbsDisambiguationTest {

	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Attentäter gesteht","de");
		
		DocumentMetaData.create(cas).setDocumentId("test");
		Sentence sent = new Sentence(cas, 0,cas.getDocumentText().length());
		sent.addToIndexes();
		
		String verbString = "gesteht";
		String verbLemma = "gestehen";
		
		POS_VERB verb = new POS_VERB (cas, cas.getDocumentText().indexOf(verbString), cas.getDocumentText().indexOf(verbString)+verbString.length());
		verb.addToIndexes();
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				VerbsDisambiguation.class,
				VerbsDisambiguation.PARAM_GERMANET_PATH,"/home/staff_homes/ahemati/projects/VerbsAnnotator/trunk/src/main/resources/GN_V140.zip",
				VerbsDisambiguation.PARAM_VERBLEMMAIDS_PATH,"/home/staff_homes/ahemati/projects/VerbsAnnotator/trunk/verbLemmaIds"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	}
}
