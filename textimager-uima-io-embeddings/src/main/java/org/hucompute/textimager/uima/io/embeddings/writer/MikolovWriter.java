package org.hucompute.textimager.uima.io.embeddings.writer;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_PUNCT;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


public class MikolovWriter extends BaseEmbeddingsWriter{

	// Output fastSense Disambiguations for a word if available
	public static final String PARAM_OUTPUT_DISAMBIG = "DISAMBIG_OUTPUT";
	@ConfigurationParameter(name=PARAM_OUTPUT_DISAMBIG, mandatory=true, defaultValue="false")
	private boolean disambigOutput;
	
	int processed = 0;
	
	private String getDisambig(Annotation anno, String defaultStr) {
		Collection<CategoryCoveredTagged> disambigs = JCasUtil.selectCovered(CategoryCoveredTagged.class, anno);
		if (!disambigs.isEmpty()) {
			try {
				CategoryCoveredTagged disambigBest = disambigs.stream().max(Comparator.comparingDouble(d -> d.getScore())).orElseThrow(NoSuchElementException::new);
				return disambigBest.getValue();
			} catch (NoSuchElementException ex) {
				// ignore
			}
		}
		
		return defaultStr;
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				try {
					switch (exportmodus) {
					case TOKEN: {
						String tokenStr = token.getCoveredText();
						if (disambigOutput) {
							tokenStr = getDisambig(token, tokenStr);
						}
						writer.write(tokenStr + " ");
						break;
					}
					case LEMMA: {
						String lemmaStr = token.getLemma().getValue();
						if (disambigOutput) {
							lemmaStr = getDisambig(token, lemmaStr);
						}
						writer.write(lemmaStr + " ");
						break;
					}
					case LEMMA_POS: {
						String lemmaStr = token.getLemma().getValue();
						if (disambigOutput) {
							lemmaStr = getDisambig(token, lemmaStr);
						}
						
						if(normalizePos(token.getPos().getPosValue()).equals("PUN"))
							lemmaStr = token.getCoveredText();
						
						writer.write(lemmaStr + "_" + (normalizePos?normalizePos(token.getPos().getPosValue()):token.getPos().getPosValue())+ " ");
						break;
					}
					default:
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				writer.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(processed++ % 100 == 0)
			System.out.println(processed);
	}
}
