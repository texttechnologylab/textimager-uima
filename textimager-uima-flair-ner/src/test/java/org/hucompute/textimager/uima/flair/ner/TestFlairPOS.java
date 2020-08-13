package org.hucompute.textimager.uima.flair.ner;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hucompute.textimager.uima.flair.FlairPOS;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class TestFlairPOS {

	@BeforeClass
	public static void setUpClass() throws ResourceInitializationException {
	}

	@Test
	public void test() throws UIMAException, IOException {
		String text = "Si autem illis adhuc vita perdonata non fuit et revicti non sunt et res et mancipia vel mobile habent fiat de illis sicut in quarto libro capitulorum capitulo XXIX dicitur cum ad mallum comitis venerint\n"
				+ "9 Ut marca nostra secundum quod ordinatum vel scaritum habemus custodiant una cum missis nostris\n"
				+ "Iste quadragenarius numerus qui inchoatur a sequenti dominica post Quinquagesimam finitur quinta feria ante Pascha Domini quae vocatur Coena Domini\n"
				+ "48 Ut nemo presbiterorum baptizare praesumat nisi in vicis et ecclesiis baptismalibus atque temporibus constitutis nisi causa egritudinis vel certae necessitatis sicut sacra canonum docet auctoritas ; et vici auctoritatem et privilegia debita et antiqua retineant\n"
				+ "In antiquis libris Missalium et Lectionariorum reperitur scriptum Hebdomada quinta ante Natalem Domini\n";
		JCas jCas = JCasFactory.createText(text);

		int last_index = 0;
		int index = text.indexOf("\n");
		while (index > 0) {
			Sentence sentence = new Sentence(jCas, last_index, index);
			jCas.addFsToIndexes(sentence);

			last_index = index + 1;
			index = text.indexOf("\n", last_index);
		}

		String home = System.getenv("HOME");
		String model_location = home + "/.textimager/models/pos-la-flair.pt";
		if (!Paths.get(model_location).toFile().exists()) {
			Files.copy(Paths.get("/resources/nlp/models/pos/flair/pos-la-flair.pt").toFile(),
					Paths.get(model_location).toFile());
		}
		AnalysisEngine engine = AnalysisEngineFactory.createEngine(FlairPOS.class, FlairPOS.PARAM_LANGUAGE, "la",
				FlairPOS.PARAM_MODEL_LOCATION, model_location);

		SimplePipeline.runPipeline(jCas, engine);
		JCasUtil.select(jCas, POS.class).forEach(pos -> {
			System.out.println(pos.getCoveredText() + ": " + pos);
		});
		assert JCasUtil.select(jCas, POS.class).size() > 0;
	}
}
