//package org.hucompute.textimager.uima.transformers;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//import static org.junit.Assert.assertArrayEquals;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.fit.util.JCasUtil;
//import org.apache.uima.jcas.JCas;
//import org.junit.Test;
//
//import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
//
//import org.hucompute.textimager.uima.transformers.SentAnaTransformers;
//
//public class SentAnaTransformersTest {
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Bill Gates is the head of Microsoft.");
//		cas.setDocumentLanguage("en");
//		
//		AnalysisEngineDescription transNer = createEngineDescription(SentAnaTransformers.class
//				,SentAnaTransformers.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy");
//		
//		SimplePipeline.runPipeline(cas, transNer);
//		
//		String[] ents = new String[] { "POSITIVE" };
//
//		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
//		
//		assertArrayEquals(ents, casEnts);
//	}
//}