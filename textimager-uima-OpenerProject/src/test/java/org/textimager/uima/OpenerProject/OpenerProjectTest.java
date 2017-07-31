package org.textimager.uima.OpenerProject;

import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;

import org.hucompute.services.util.XmlFormatter;
import org.hucompute.textimager.uima.OpenerProject.*;


public class OpenerProjectTest {

	
	@Test
	public void TokenizerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void TokenizerDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "de");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		AssertAnnotations.assertToken(new String[] {"Das", "ist", "ein", "guter", "Test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void LanguageTestDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertEquals("Language", "de" , cas.getDocumentLanguage());
		
	}
	
	@Test
	public void LanguageTestFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertEquals("Language", "fr" , cas.getDocumentLanguage());
		
	}
	@Test
	public void simpleExample() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		new Token(cas,0,3).addToIndexes();
		new Token(cas,4,7).addToIndexes();
		new Token(cas,8,11).addToIndexes();
		new Token(cas,12,17).addToIndexes();
		new Token(cas,18,22).addToIndexes();
		new Token(cas,22,23).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertPOS(
				new String[] { "PR", "V", "ART", "ADJ", "NN","O"},
				new String[] { "R", "V", "D", "A", "G","O"}, 
				JCasUtil.select(cas, POS.class));	
	}
	@Test
	public void NER() throws UIMAException, IOException{
		JCas cas = JCasFactory.createText("Angela Dorothea Merkel (API : /ˈaŋɡela doʀoˈteːa ˈmɛʁkl̩/), née Kasner le 17 juillet 1954 à Hambourg, est une femme d'État allemande, membre de l'Union chrétienne-démocrate (CDU) et chancelière fédérale depuis le 22 novembre 2005.\n" + 
				"\n" + 
				"Physicienne de formation, elle est élue sans discontinuer au Bundestag depuis 1991. Elle est ministre fédérale des Femmes et de la Jeunesse au sein du cabinet Kohl IV, de 1991 à 1994, avant de se voir confier le ministère fédéral de l'Environnement, de la Protection de la Nature et de la Sécurité nucléaire du cabinet Kohl V, jusqu'en 1998. Elle devient, en 2000, la première femme présidente de la CDU.\n" + 
				"\n" + 
				"Après la victoire relative de la droite aux élections fédérales de 2005, elle est élue chancelière de la République fédérale d'Allemagne, formant une grande coalition alliant la CDU et le Parti social-démocrate (SPD). Elle est reconduite dans ses fonctions à la tête d'un gouvernement CDU-FDP en 2009, puis d'un nouveau gouvernement de coalition CDU-SPD en 2013. Lors de la crise migratoire en Europe, à partir de 2015, elle doit faire face à des critiques de dirigeants États européens et de son camp en raison de sa politique d'ouverture des frontières.\n" + 
				"\n" + 
				"Elle est désignée à dix reprises femme la plus puissante du monde par le magazine Forbes et personnalité de l'année 2015 par le magazine Time. Elle est aussi largement perçue comme la personnalité politique la plus importante et la plus puissante de l'Union européenne.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class
				));
		builder.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	

		System.out.println(cas.getCas());
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
	}

	
	

}
