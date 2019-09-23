package org.hucompute.textimager.uima.io.mediawiki;

import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

/** A wrapper around a Word2Vec object. */
class Word2VecHelper {

	Word2Vec word2Vec = null;

	/** Load Word2Vec embeddings from a file. */
	public Word2VecHelper(String filepath) {
		try {
			word2Vec = WordVectorSerializer.readWord2VecModel(filepath);
			System.out.println(" INFO | Word2Vec succesfully read " + filepath);
		} catch(Exception e) {
			System.out.println(" ERR  | Word2Vec could not read " + filepath + ": " + e.toString());
		}
	}

	public double getSimilarity(LemmaInfos.LemmaPos lp1, LemmaInfos.LemmaPos lp2) {
		return getSimilarity(lp1.toString(), lp2.toString());
	}

	public double getSimilarity(String word1, String word2) {
		return word2Vec != null ? word2Vec.similarity(word1, word2) : null;
	}

	public WeightLookupTable getWeightLookupTable() {
		return word2Vec != null ? word2Vec.lookupTable() : null;
	}

	public Collection<String> getWordsNearest(String word, int count) {
		return word2Vec != null ? word2Vec.wordsNearest(word, count) : null;
	}

	public Collection<String> getWordsNearest(LemmaInfos.LemmaPos lemmapos, int count) {
		return getWordsNearest(lemmapos.toString(), count);
	}

	public String[] getWordsNearestAsArray(String word, int count) {
		Collection<String> col = getWordsNearest(word, count);
		return col != null ? col.toArray(new String[0]) : null;
	}

	public String[] getWordsNearestAsArray(LemmaInfos.LemmaPos lemmapos, int count) {
		return getWordsNearestAsArray(lemmapos.toString(), count);
	}

	public double[] getWordVector(String word) {
        return word2Vec != null ? word2Vec.getWordVector(word) : null;
	}

}
