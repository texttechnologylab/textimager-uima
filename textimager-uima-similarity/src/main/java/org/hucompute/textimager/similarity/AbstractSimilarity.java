package org.hucompute.textimager.similarity;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.type.Similarity;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

@TypeCapability(
		outputs={
		"org.hucompute.services.similarity.type.Similarity"})
public abstract class AbstractSimilarity extends JCasAnnotator_ImplBase{

	private class AnnotationBeginComparator implements Comparator<Annotation> {
		@Override
		public int compare(Annotation a, Annotation b) {
			return a.getBegin() < b.getBegin() ? -1 : a.getBegin() == b.getBegin() ? 0 : 1;
		}
	}

	private double round(final double value, final int frac) { 
		return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac); 
	}

	/**
	 * Gets the Lemmas from a given sentence.
	 * @param sentence The input Sentence
	 * @return The lemmas represented as List of String.
	 */
	public List<String> getLemmas(Annotation sentence){
		List<String> lemmas = new ArrayList<>();
		for (Token token : selectCovered(Token.class, sentence)) {
			lemmas.add(selectCovered(Lemma.class, token).get(0).getValue());
		}
		return lemmas;
	}

	/**
	 * Gets the Tokens from a given sentence.
	 * @param sentence The input Sentence
	 * @return The tokens represented as List of String.
	 */
	public List<String> getTokens(Annotation sentence){
		List<String> tokens = new ArrayList<>();

		for (Token token : selectCovered(Token.class, sentence)) {
			tokens.add(token.getCoveredText());
		}
		return tokens;
	}

	/**
	 * Gets the Tokens from a given sentence.
	 * @param sentence The input Sentence
	 * @return The tokens represented as List of String.
	 */
	public List<String> getTokens(Annotation sentence, Class<? extends Annotation> filter){
		List<String> tokens = new ArrayList<>();


		for (Token token : selectCovered(Token.class, sentence)) {
			if(selectCovered(filter, token).size()>0)
				tokens.add(token.getCoveredText());
		}
		return tokens;
	}

	/**
	 * Gets the similarity of two sentences.
	 * @param sentence1
	 * @param sentence2
	 * @return
	 * @throws InterruptedException 
	 */
	public abstract double getSimilarityValue(JCas jCas,Annotation sentence1, Annotation sentence2);
//		throw new NotImplementedException();
//	}




	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {		
		List<Sentence> sentences = IteratorUtils.toList(select(aJCas, Sentence.class).iterator());
		int docSize = 1;

		Collections.sort(sentences,new AnnotationBeginComparator());
		StringBuilder sb = new StringBuilder();
		StringBuilder indexes = new StringBuilder();

		boolean documentSentenceBipartite = false;

		for (int i = 0; i<sentences.size(); i++) {
			Sentence sentence1 = sentences.get(i);				
			indexes.append(sentence1.getBegin()).append("-").append(sentence1.getEnd()).append(",");

			for(int j = i+1; j<sentences.size();j++){
				if(documentSentenceBipartite){
					if(j%(sentences.size()/docSize)==i%(sentences.size()/docSize)){
						Sentence sentence2 = sentences.get(j);
						sb.append(round(getSimilarityValue(aJCas,sentence1, sentence2),6)).append(",");
					}
					else
						sb.append(0).append(",");
				}
				else{
					Sentence sentence2 = sentences.get(j);
					sb.append(round(getSimilarityValue(aJCas,sentence1, sentence2),6)).append(",");
				}
			}
			sb.deleteCharAt(sb.length()-1).append(";");
		}
		sb.insert(0, indexes.toString().substring(0, indexes.length()-1)+";");
		Similarity value = new Similarity(aJCas);
		value.setValue(sb.toString());
		aJCas.addFsToIndexes(value);	
		System.out.println("similarity finished");
	}

}
