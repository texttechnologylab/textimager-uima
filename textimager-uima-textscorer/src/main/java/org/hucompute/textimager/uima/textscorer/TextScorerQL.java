package org.hucompute.textimager.uima.textscorer;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import jep.JepException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.texttechnologylab.annotation.score.TextScore;
import org.texttechnologylab.annotation.score.TextScoreEntry;


import java.util.ArrayList;
import java.util.HashMap;

public class TextScorerQL extends TextScorerBase {

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		// TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later

		try {
			System.out.println("initializing scorer...");
			interpreter.exec("scorers = list([cls() for name, cls in scorer.__dict__.items() if isinstance(cls, type) "
					+ "and issubclass(cls, scorer.TextScore) and name != 'TextScore'])");
			interpreter.exec("sc = scorer.Scorer(scorers=scorers)");
			System.out.println("done initializing scorer.");

		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processScores(JCas aJCas, String documentName, ArrayList<Double> scores, ArrayList<String> names)
			throws JepException {
		TextScore textScore = new TextScore(aJCas);
		FSArray scs = new FSArray(aJCas, scores.size());

		for (int i=0; i<scores.size(); i++){
			double score = scores.get(i);
			String name = names.get(i);
			TextScoreEntry textScoreEntry = new TextScoreEntry(aJCas);
			textScoreEntry.setKey(name);
			textScoreEntry.setValue(score);
			textScoreEntry.setLabel(name);
			scs.set(i, textScoreEntry);
		}
		textScore.setElements(scs);
		textScore.setDocumentName(documentName);
		textScore.addToIndexes();
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		long textLength = text.length();
		System.out.println("text length: " + textLength);
		// abort on empty
		if (textLength < 1) {
			System.out.println("skipping due to text length < 1");
			return;
		}
		try {
			// text to python interpreter
			interpreter.set("lang", (Object)aJCas.getDocumentLanguage());
			interpreter.set("text", (Object)text);
	//				interpreter.set("label", (Object)text);
			interpreter.exec("scores, names, text_hash = sc.run(lang, 'dummy', text)");

			@SuppressWarnings("unchecked")
			ArrayList<Double> scores = (ArrayList<Double>) interpreter.getValue("scores");
			@SuppressWarnings("unchecked")
			ArrayList<String> names = (ArrayList<String>) interpreter.getValue("names");
			String text_hash = (String) interpreter.getValue("text_hash");

			System.out.println("----------");
			System.out.println(scores);
			System.out.println(names);
			System.out.println(text_hash);

			processScores(aJCas, text_hash, scores, names);

		} catch (JepException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
