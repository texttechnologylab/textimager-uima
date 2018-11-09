package org.hucompute.textimager.uima.cltk;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class CLTKSentenceSegmenterTest {
	@Test
	public void simpleExampleLa() throws UIMAException{
		JCas cas = JCasFactory.createText("Itaque cum M. Aurelio et P. Minidio et Cn. Cornelio ad apparationem balistarum et scorpionem reliquorumque tormentorum refectionem fui praesto et cum eis commoda accepi, quae cum primo mihi tribuisiti recognitionem, per sorosis commendationem servasti. Cum ergo eo beneficio essem obligatus, ut ad exitum vitae non haberem inopiae timorem, haec tibi scribere coepi, quod animadverti multa te aedificavisse et nunc aedificare, reliquo quoque tempore et publicorum et privatorum aedificiorum, pro amplitudine rerum gestarum ut posteris memoriae traderentur curam habiturum.");
		cas.setDocumentLanguage("la");
		
		AnalysisEngineDescription cltkSentenceSegmenter = createEngineDescription(CLTKSentenceSegmenter.class,
				CLTKSentenceSegmenter.PARAM_DOCKER_IMAGE,"textimager-cltk");
		
		SimplePipeline.runPipeline(cas, cltkSentenceSegmenter);
		
		String[] sentences = new String[] {
				"Itaque cum M. Aurelio et P. Minidio et Cn. Cornelio ad apparationem balistarum et scorpionem reliquorumque tormentorum refectionem fui praesto et cum eis commoda accepi, quae cum primo mihi tribuisiti recognitionem, per sorosis commendationem servasti.",
				"Cum ergo eo beneficio essem obligatus, ut ad exitum vitae non haberem inopiae timorem, haec tibi scribere coepi, quod animadverti multa te aedificavisse et nunc aedificare, reliquo quoque tempore et publicorum et privatorum aedificiorum, pro amplitudine rerum gestarum ut posteris memoriae traderentur curam habiturum."
		};

		String[] casSentences = (String[]) JCasUtil.select(cas, Sentence.class).stream().map(s -> s.getCoveredText()).toArray(String[]::new);
		
		assertArrayEquals(sentences, casSentences);
	}
}
