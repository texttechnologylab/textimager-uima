package org.hucompute.textimager.uima.io.mediawiki;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/** Infos about multiple lemmas. */
public class LemmaInfos {

	private HashMap<LemmaPos, LemmaInfo> map = new HashMap<LemmaPos, LemmaInfo>();

	/** Different features of a lemma. */
	public class LemmaInfo {
		public TreeSet<String> containingDocuments;
		public int frequency;
		public Set<EnhancedMorphologicalFeatures> morphologicalFeatures;
		public List<LemmaInText> occurances;

		public LemmaInfo() {
			containingDocuments = new TreeSet<String>();
			frequency = 0;
			morphologicalFeatures = new TreeSet<EnhancedMorphologicalFeatures>();
			occurances = new ArrayList<LemmaInText>();
		}

		public EnhancedMorphologicalFeatures addMorphologicalFeatures(String token, MorphologicalFeatures features) {
			EnhancedMorphologicalFeatures morph = new EnhancedMorphologicalFeatures(token, features);
			morphologicalFeatures.add(morph);
//			int i = 1;
//			for (EnhancedMorphologicalFeatures feat : morphologicalFeatures) {
//				System.out.println(token + " " + (i++) + ": " + feat); // FIXME delete
//			}
			return morph;
		}

		public void addOccurance(String pageTitle, int sentence, String leftContext, String keyword, String rightContext) {
			occurances.add(new LemmaInText(pageTitle, sentence, leftContext, keyword, rightContext));
		}

		public int getDocumentFrequency() {
			return containingDocuments.size();
		}

		public int getFrequencyClass() {
			return 0; // TODO calculate frequency class
		}

		public double getInverseDocumentFrequency(int docCount) {
			return Math.log(docCount / (1 + containingDocuments.size())); // TODO is this right?
		}

		public String getInverseDocumentFrequencyAsString(int docCount) {
			return getInverseDocumentFrequencyAsString(docCount, "0.0");
		}

		public String getInverseDocumentFrequencyAsString(int docCount, String format) {
			return (new DecimalFormat(format)).format(getInverseDocumentFrequency(docCount));
		}
	}

	/** Occurance of a lemma in a sentence. */
	public class LemmaInText {
		public String text;
		public String leftContext;
		public String rightContext;
		public String keyword;
		public int sentence;

		public LemmaInText(String text, int sentence, String leftContext, String keyword, String rightContext) {
			this.text = text;
			this.sentence = sentence;
			this.leftContext = leftContext;
			this.rightContext = rightContext;
			this.keyword = keyword;
		}
	}

	/** A combination of lemma and POS. */
	public class LemmaPos {
		public String lemma;
		public String pos;

		public LemmaPos(Token token) {
			lemma = token.getLemma().getValue();
			pos = token.getPos().getPosValue();
		}

		public boolean equals(Object obj) {
			return obj instanceof LemmaPos && toString().equals(((LemmaPos) obj).toString());
		}

		public int hashCode() {
			return toString().hashCode();
		}

		public String toString() {
			return lemma + "_" + pos;
		}
	}

	public LemmaPos createLemmaPos(Token token) {
		return new LemmaPos(token);
	}

	public Collection<HashMap.Entry<LemmaPos, LemmaInfo>> entrySet() {
		return map.entrySet();
	}
	
	public LemmaInfo get(LemmaPos lemmapos) {
		if (lemmapos == null) {
			throw new IllegalArgumentException("lemmapos can not be null");
		}
		LemmaInfo info = map.get(lemmapos);
		if (info == null) {
			info = new LemmaInfo();
			map.put(lemmapos, info);
		}
		return info;
	}
}
