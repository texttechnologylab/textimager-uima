package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.uaic.core.DictionaryProvider;
import ro.uaic.info.nlptools.corpus.INlpSentence;
import ro.uaic.info.nlptools.corpus.InmemoryCorpus;
import ro.uaic.info.nlptools.corpus.Token;
import ro.uaic.info.nlptools.tools.UaicMorphologicalAnnotation;
import ro.uaic.info.nlptools.tools.UaicTokenizer;

import java.util.Set;

/**
 * This class provides the tokenization functionality for the romanian language
 *
 * @author Dinu Ganea
 */
@TypeCapability(outputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma"
})
public class Lemmatizer extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        UaicTokenizer uaicTokenizer = new UaicTokenizer(DictionaryProvider.getMorphologicalDictionary());

        try {
            InmemoryCorpus results = uaicTokenizer.splitAndTokenize(jCas.getDocumentText());

            // Keep track of the current position relative to the complete text by adding the length of every
            // sentence to the total length. This corresponds the cursor movement over the text
            // Must be done because token positions are relative to the sentence, not to the entire text
            int textRelativePos = 0;
            // Iterate every sentence
            for (int sentenceIdx = 0; sentenceIdx < results.getSentenceCount(); sentenceIdx++) {
                INlpSentence sentence = results.getSentence(sentenceIdx);
                // Check every token of the sentence
                for (int i = 0; i < sentence.getTokenCount(); i++) {
                    Token tokenUaic = sentence.getToken(i);

                    // Lemma begin position is added to the sentence begin position in the text to get the idx relative
                    // to the actual text. Same for the end idx
                    Lemma lemma = new Lemma(jCas, textRelativePos + tokenUaic.getCharStartIndexInSentence(), textRelativePos + tokenUaic.getCharEndIndexInSentence());

                    // Set the token word form as fallback value
                    String lemmaValue = tokenUaic.getWordForm();

                    // Find the lemma corresponding to the current token
                    Set<UaicMorphologicalAnnotation> annotations = DictionaryProvider.getMorphologicalDictionary().get(tokenUaic.getWordForm());
                    if (annotations != null) {
                        UaicMorphologicalAnnotation annotation = annotations.iterator().next();
                        if (annotation != null) {
                            // Overwrite the value
                            lemmaValue = annotation.getLemma();
                        }
                    }

                    lemma.setValue(lemmaValue);
                    lemma.addToIndexes(jCas);

                }
                // Make sure to add the length of the sentence to the text relative index
                textRelativePos += sentence.getToken(sentence.getTokenCount() - 1).getCharEndIndexInSentence() + 1;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            // Wrap the exception
            throw new AnalysisEngineProcessException(e);
        }

    }
}