package org.hucompute.textimager.uima.talismane;

import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.services.util.XmlFormatter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;



public class TalismaneTest {

	
	@Test
	public void TokenizerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismaneSegmenter.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		System.out.println(cas.getCas());
		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));
		AssertAnnotations.assertSentence(new String[] {"Ceci est un bon test."}, JCasUtil.select(cas, Sentence.class));

	}
	
	@Test
	public void POSTaggerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");
		
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		new Token(cas,0,4).addToIndexes();
		new Token(cas,5,8).addToIndexes();
		new Token(cas,9,11).addToIndexes();
		new Token(cas,12,15).addToIndexes();
		new Token(cas,16,20).addToIndexes();
		new Token(cas,20,21).addToIndexes();
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismanePOS.class,
				TalismanePOS.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/pos-default.map"));
		SimplePipeline.runPipeline(cas,builder.createAggregate());

		AssertAnnotations.assertLemma(new String[] {"ceci", "être", "un", "bon","test","."}, JCasUtil.select(cas, Lemma.class));
		AssertAnnotations.assertPOS(new String[] {"N", "V", "ART", "ADJ","N","PUNC"}, new String[] {"NPP", "V", "DET", "ADJ","NC","PONCT"}, JCasUtil.select(cas, POS.class));

	}
	
	@Test
	public void TestOutputXML() throws UIMAException{
		JCas cas = JCasFactory.createText("Champagne est située sur la rivière Dezadeash, un affluent de la rivière Alsek, sur le Dalton Trail où un comptoir avait été établi en 1902. La Route de l'Alaska ne traverse plus le village, son tracé ayant été modifié à la fin de 2002, mais l'ancienne route le dessert toujours.", "fr");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismaneSegmenter.class));
		builder.add(createEngineDescription(
				TalismanePOS.class,
				TalismanePOS.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/pos-default.map"));
		builder.add(createEngineDescription(
				TalismaneDependencyParser.class,
				TalismaneDependencyParser.PARAM_DEPENDENCY_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/dependency-default.map"));
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		

		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
		//AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));
		//AssertAnnotations.assertSentence(new String[] {"Ceci est un bon test."}, JCasUtil.select(cas, Sentence.class));

	}
	
	@Test
	public void FullPipeRuntime() throws UIMAException, IOException{
		String text = new String(Files.readAllBytes(Paths.get("src/test/java/wiki_fr_text")));
		JCas cas = JCasFactory.createText(text,"fr");
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismaneSegmenter.class));
		builder.add(createEngineDescription(
				TalismanePOS.class,
				TalismanePOS.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/pos-default.map"));
		builder.add(createEngineDescription(
				TalismaneDependencyParser.class,
				TalismaneDependencyParser.PARAM_DEPENDENCY_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/dependency-default.map"));
		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));	
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
		
		
		
//		91 seconds
//		Token: 2153
//		Sentence: 76
//		Token per Second: 23
		
//		941 seconds
//		Token: 6082
//		Sentence: 213
//		Token per Second: 6
		Timestamp t3 = new Timestamp(timestamp2.getTime() - timestamp1.getTime());
		long Time_in_Seconds = (t3.getTime() / 1000);
		System.out.println(Time_in_Seconds +" seconds");
		System.out.println("Token: "+JCasUtil.select(cas, Token.class).size());
		System.out.println("Sentence: "+JCasUtil.select(cas, Sentence.class).size());
		System.out.println("Token per Second: "+JCasUtil.select(cas, Token.class).size()/Time_in_Seconds);

	}
	

}
