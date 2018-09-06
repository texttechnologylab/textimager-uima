package org.hucompute.textimager.uima.io.embeddings.writer;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class MikolovWriter extends BaseEmbeddingsWriter{

	public static final String PARAM_NORMALIZE_POS = "NORMALIZE_POS";
	@ConfigurationParameter(name=PARAM_NORMALIZE_POS, mandatory=true, defaultValue="true")
	private boolean normalizePos;

	int processed = 0;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				try {
					switch (exportmodus) {
					case TOKEN:
						writer.write(token.getCoveredText() + " ");
						break;
					case LEMMA:
						writer.write(token.getLemma().getValue()+ " ");
						break;
					case LEMMA_POS:
						writer.write(token.getLemma().getValue()+ "_" + (normalizePos?normalizePos(token.getPos().getPosValue()):token.getPos().getPosValue())+ " ");
						break;
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
