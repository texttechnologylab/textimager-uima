package org.hucompute.textimager.uima.marmot;

public class MarMoTTaggerTest {

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
//				MarMoTTagger.class
//				//				,MarMoTTagger.PARAM_MODEL_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/test/de.marmot"
//				,MarMoTTagger.PARAM_MODEL_LOCATION,"http://cistern.cis.lmu.de/marmot/models/CURRENT/spmrl/de.marmot"
//				,MarMoTTagger.PARAM_POS_MAPPING_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/lib/pos-de-pretrained.map"
//				));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//
//
//		assertPOS(
//				new String[] {"POS","POS","POS","POS","POS","POS"},
////				new String[] { "PR", "V", "ART", "ADJ", "NN","PUNC"},
//								new String[] { "PDS|PDS", "VAFIN|VAFIN", "ART|ART", "ADJA|ADJA", "NN|NN","$.|$."},
//				JCasUtil.select(cas, POS.class));
//	}
//
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Magnus stultus est.", "la");
//		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();
//
//		new Token(cas,0,6).addToIndexes();
//		new Token(cas,7,14).addToIndexes();
//		new Token(cas,15,18).addToIndexes();
//		new Token(cas,18,19).addToIndexes();
//
//		AggregateBuilder builder = new AggregateBuilder();
//
//		builder.add(createEngineDescription(
//				MarMoTTagger.class
//				,MarMoTTagger.PARAM_POS_MAPPING_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/lib/pos-la-hucompute.map"
//				));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//
//		assertPOS(
////				new String[] { "NP", "NN", "V", "PUNC"},
//				new String[]{"POS","POS","POS","POS"},
//				new String[] { "NP","NN", "V", "$."},
//				JCasUtil.select(cas, POS.class));
//	}

}
