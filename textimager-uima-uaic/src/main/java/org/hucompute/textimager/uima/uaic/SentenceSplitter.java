package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.uaic.core.DictionaryProvider;
import ro.uaic.info.nlptools.corpus.INlpSentence;
import ro.uaic.info.nlptools.corpus.InmemoryCorpus;

/**
 * This class provides the sentence splitting functionality for the romanian language
 *
 * @author Dinu Ganea
 */
@TypeCapability(outputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
})
public class SentenceSplitter extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        // The tokenizer also splits the text into sentences
        ro.uaic.info.nlptools.tools.UaicTokenizer uaicTokenizer = new ro.uaic.info.nlptools.tools.UaicTokenizer(DictionaryProvider.getMorphologicalDictionary());

        try {
            InmemoryCorpus results = uaicTokenizer.splitAndTokenize(jCas.getDocumentText());

            // Keep track of the current position relative to the complete text by adding the length of every
            // sentence to the total length. This corresponds the cursor movement over the text
            int totalLength = 0;

            int sentenceLength = 0;

            for (int i = 0; i < results.getSentenceCount(); i++) {
                INlpSentence uaicSentence = results.getSentence(i);

                // To calculate the sentence length, we just query the last token and get its end position.
                // We also keep track of the begin position of the sentence by keeping the total length
                sentenceLength = uaicSentence.getToken(uaicSentence.getTokenCount() - 1).getCharEndIndexInSentence();

                if (i == results.getSentenceCount() - 1) {
                    // Add the last character for the last sentence
                    sentenceLength += 1;
                }

                Sentence sentence = new Sentence(jCas, totalLength, totalLength + sentenceLength);
                sentence.setId(jCas.getDocumentText().substring(totalLength, totalLength + sentenceLength));
                sentence.addToIndexes(jCas);

                // "Move the cursor"
                totalLength = +sentenceLength;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            // Wrap th exception
            throw new AnalysisEngineProcessException(e);
        }
    }
}
