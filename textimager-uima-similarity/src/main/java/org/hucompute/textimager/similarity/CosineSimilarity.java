package org.hucompute.textimager.similarity;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.dkpro.similarity.algorithms.api.SimilarityException;


public class CosineSimilarity extends AbstractSimilarity {

	@Override
	public double getSimilarityValue(JCas jCas,Annotation sentence1, Annotation sentence2) {
		System.out.println("sim");
		try {
			return new org.dkpro.similarity.algorithms.lexical.string.CosineSimilarity().getSimilarity(getTokens(sentence1), getTokens(sentence2));
		} catch (SimilarityException e) {
			e.printStackTrace();
		} 
		return 0;
	}

}
