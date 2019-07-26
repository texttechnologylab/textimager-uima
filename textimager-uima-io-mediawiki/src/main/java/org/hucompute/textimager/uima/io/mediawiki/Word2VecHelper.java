package org.hucompute.textimager.uima.io.mediawiki;

import java.util.Collection;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

class Word2VecHelper {

	Word2Vec word2Vec = null;

	public Word2VecHelper(String filepath) {
		try {
			word2Vec = WordVectorSerializer.readWord2VecModel(filepath);
			System.out.println(" INFO | Word2Vec succesfully read " + filepath);
		} catch(Exception e) {
			System.out.println(" ERR  | Word2Vec could not read " + filepath + ": " + e.toString());
		}
	}

	public INDArray getVectorMatrix(String word) {
		return word2Vec != null ? word2Vec.getWordVectorMatrix(word) : null;
	}

	public double getSimilarity(String word1, String word2) {
		return word2Vec != null ? word2Vec.similarity(word1, word2) : null;
	}

/*	public Iterator<INDArray> getVectors() {
		return weightLookupTable.vectors();
	}*/

	public WeightLookupTable getWeightLookupTable() {
		return word2Vec != null ? word2Vec.lookupTable() : null;
	}

	public Collection<String> getWordsNearest(String word, int count) {
		return word2Vec != null ? word2Vec.wordsNearest(word, count) : null;
	}

	public double[] getWordVector(String word) {
        return word2Vec != null ? word2Vec.getWordVector(word) : null;
	}

}
