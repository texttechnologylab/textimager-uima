package org.hucompute.textimager.uima.marmot;

public class MarMoTLemmaTest {

//	@Test
//	public void simpleExampleDE() throws UIMAException{
//		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "de");
//		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();
//
//		new Token(cas,0,3).addToIndexes();
//		new Token(cas,4,7).addToIndexes();
//		new Token(cas,8,11).addToIndexes();
//		new Token(cas,12,17).addToIndexes();
//		new Token(cas,18,22).addToIndexes();
//		new Token(cas,22,23).addToIndexes();
//
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(
//				MarMoTLemma.class,
//				MarMoTLemma.PARAM_MODEL_LOCATION,"http://cistern.cis.lmu.de/marmot/models/CURRENT/spmrl/de.marmot"
//				));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//
//		assertLemma(new String[]{"der","sein","ein","gut","Test","--"}, JCasUtil.select(cas, Lemma.class));
//	}

//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Hoc senatusconsulti genus in usu fuit a tempore Gracchorum usque ad secundum triumviratum (43 a.C.n.).");
////		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();
////
////		new Token(cas,0,6).addToIndexes();
////		new Token(cas,7,14).addToIndexes();
////		new Token(cas,15,18).addToIndexes();
////		new Token(cas,18,19).addToIndexes();
//
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(
//				BreakIteratorSegmenter.class
//				));
//		builder.add(createEngineDescription(
//				MarMoTLemma.class
//				));
//
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//
//		assertLemma(new String[]{"magnus","stultus","sum","."}, JCasUtil.select(cas, Lemma.class));
//
//	}

}
