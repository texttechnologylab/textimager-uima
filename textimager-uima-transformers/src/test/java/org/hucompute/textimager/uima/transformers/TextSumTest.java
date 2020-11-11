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
//import org.hucompute.textimager.uima.transformers.TextSumTransformers;
//
//public class TextSumTransformersTest {
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("William Henry Gates III (born October 28, 1955) is an American business magnate, software developer, investor, and philanthropist. He is best known as the co-founder of Microsoft Corporation.[2][3] During his career at Microsoft, Gates held the positions of chairman, chief executive officer (CEO), president and chief software architect, while also being the largest individual shareholder until May 2014. He is one of the best-known entrepreneurs and pioneers of the microcomputer revolution of the 1970s and 1980s. ");
//		cas.setDocumentLanguage("en");
//		
//		AnalysisEngineDescription transSum = createEngineDescription(TextSumTransformers.class
//				,TextSumTransformers.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy");
//		
//		SimplePipeline.runPipeline(cas, transSum);
//		
//		String[] ents = new String[] { "William Henry Gates III (born October 28, 1955) is an american business magnate, software developer, investor and philanthropist . he is best known as the co-founder of Microsoft Corporation ." };
//
//		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
//		
//		assertArrayEquals(ents, casEnts);
//	}
//}
