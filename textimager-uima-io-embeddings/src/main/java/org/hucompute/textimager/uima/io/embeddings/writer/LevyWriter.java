package org.hucompute.textimager.uima.io.embeddings.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class LevyWriter extends BaseEmbeddingsWriter{

	public static final String PARAM_NORMALIZE_POS = "NORMALIZE_POS";
	@ConfigurationParameter(name=PARAM_NORMALIZE_POS, mandatory=true, defaultValue="true")
	private boolean normalizePos;

	int processed = 0;

//	@Override
//	public void process(JCas jCas) throws AnalysisEngineProcessException {
//		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
//			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
//				try {
//					switch (exportmodus) {
//					case TOKEN:
//						writer.write(token.getCoveredText() + " ");
//						break;
//					case LEMMA:
//						writer.write(token.getLemma().getValue()+ " ");
//						break;
//					case LEMMA_POS:
//						writer.write(token.getLemma().getValue()+ "_" + (normalizePos?normalizePos(token.getPos().getPosValue()):token.getPos().getPosValue())+ " ");
//						break;
//					default:
//						break;
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			try {
//				writer.write("\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if(processed++ % 100 == 0)
//			System.out.println(processed);
//	}
	

	@Override
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException
	{

		Map<Sentence, Collection<Dependency>> indexDependencies = JCasUtil.indexCovered(aJCas, Sentence.class, Dependency.class);

		Map<Sentence, Collection<Token>> indexToken = JCasUtil.indexCovered(aJCas, Sentence.class, Token.class);


		StringBuilder sbMikolov = new StringBuilder();
		
		HashSet<String>uniqueWords = new HashSet<>();

		for (Sentence sentence : JCasUtil.select(aJCas,Sentence.class)) {
			sbMikolov.setLength(0);
			Collection<Dependency> dependencies = indexDependencies.get(sentence);
			for (Token token: indexToken.get(sentence)) {
				String context = "";
				String lPOS = (normalizePos?normalizePos(token.getPos().getPosValue()):token.getPos().getPosValue());

				sbMikolov.append(token.getLemma().getValue()).append("_").append(lPOS).append(" ");


				uniqueWords.add(token.getLemma().getValue() +"_"+lPOS);

				for (Dependency dependency : getDependents(dependencies, token)) {
					if(dependency.getDependent().equals(token)){
						String deptyp = dependency.getDependencyType()+"-1";
						System.out.println(token.getCoveredText()+"\t"+deptyp+"_"+dependency.getGovernor().getCoveredText());
					}
					else if(dependency.getGovernor().equals(token)){
						String deptyp = dependency.getDependencyType();
						System.out.println(token.getCoveredText()+"\t"+deptyp+"_"+dependency.getDependent().getCoveredText());
					}
				}
			}
		}	
	}

	public List<Dependency>getDependents(Collection<Dependency> dependencies,Token token){
		List<Dependency>dependents = new ArrayList<>();
		for (Dependency dependency : dependencies) {
			if(dependency.getGovernor().equals(token) || dependency.getDependent().equals(token)){
				dependents.add(dependency);
			}
		}
		return dependents;
	}
}
