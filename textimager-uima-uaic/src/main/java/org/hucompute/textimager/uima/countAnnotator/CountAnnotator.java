package org.hucompute.textimager.uima.countAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.countAnnotator.type.CountAnnotation;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static org.apache.uima.fit.util.JCasUtil.select;

@TypeCapability(
        inputs = {
                "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        },
        outputs = {
                "org.hucompute.textimager.uima.countAnnotator.type.CountAnnotation"

        }
)
public class CountAnnotator extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        HashMap<String, Integer> wordsForm = new HashMap<>();
        HashMap<String, Integer> lemmata = new HashMap<>();

        // Count lemmata and word form apparition first while storing/updating the counter into the specific HashMap
        select(jCas, Token.class).forEach(new Consumer<Token>() {
            @Override
            public void accept(Token token) {
                wordsForm.put(token.getCoveredText(), wordsForm.getOrDefault(token.getCoveredText(), 0) + 1);
                // We don't need to iterate over lemmata, we can extract them direct from the token
                lemmata.put(token.getLemma().getValue(), lemmata.getOrDefault(token.getLemma().getValue(), 0) + 1);
            }
        });

        // Parse the HashMap for each saved word form and create a CountAnnotation instance
        for (Entry<String, Integer> wordFormCount : wordsForm.entrySet()) {
            CountAnnotation countAnnotation = new CountAnnotation(jCas);
            countAnnotation.setValue(String.format("Word Form (%s)", wordFormCount.getKey()));
            countAnnotation.setCount(wordFormCount.getValue());
            countAnnotation.addToIndexes();
        }

        // Parse the HashMap for each saved lemma and create a CountAnnotation instance
        for (Entry<String, Integer> lemmaCount : lemmata.entrySet()) {
            CountAnnotation countAnnotation = new CountAnnotation(jCas);
            countAnnotation.setValue(String.format("Lemma (%s)", lemmaCount.getKey()));
            countAnnotation.setCount(lemmaCount.getValue());
            countAnnotation.addToIndexes();
        }
    }
}
