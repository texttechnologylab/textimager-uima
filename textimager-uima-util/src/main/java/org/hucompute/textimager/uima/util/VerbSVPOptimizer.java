package org.hucompute.textimager.uima.util;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.ArrayList;
import java.util.List;


public class VerbSVPOptimizer extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {

			List<Dependency>svps = new ArrayList<>();
			for (Dependency dependency : JCasUtil.selectCovered(Dependency.class, sentence)) {
				if(dependency.getDependencyType().equals("SVP"))
					svps.add(dependency);
			}
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				String lemma = token.getLemma().getValue();
				for (Dependency dependency : svps) {
					if(dependency.getGovernor().equals(token)){
						if(!lemma.startsWith(dependency.getDependent().getLemma().getValue())){
							lemma = dependency.getDependent().getLemma().getValue()+lemma;
							token.getLemma().setValue(lemma);
						}
					}
				}
			}
		}
	}

}
