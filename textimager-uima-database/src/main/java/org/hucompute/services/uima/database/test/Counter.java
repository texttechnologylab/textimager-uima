package org.hucompute.services.uima.database.test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * DKPro Annotator for the MateToolsMorphTagger.
 */
@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma"
		},
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme"
		}
		)
public class Counter
extends JCasConsumer_ImplBase
{

	int processed;
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println(processed++);
		for (Token token : JCasUtil.select(aJCas, Token.class)) {
			System.out.println(token.getCoveredText());
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
	}

}
