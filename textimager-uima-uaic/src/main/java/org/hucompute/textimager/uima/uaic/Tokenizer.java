package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.uaic.core.DictionaryProvider;
import ro.uaic.info.nlptools.corpus.INlpSentence;
import ro.uaic.info.nlptools.corpus.InmemoryCorpus;


/**
 * This class provides the tokenization functionality for the romanian language
 *
 * @author Dinu Ganea
 */
@TypeCapability(outputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"
})
public class Tokenizer extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        ro.uaic.info.nlptools.tools.UaicTokenizer uaicTokenizer = new ro.uaic.info.nlptools.tools.UaicTokenizer(DictionaryProvider.getMorphologicalDictionary());

        try {
            InmemoryCorpus results = uaicTokenizer.splitAndTokenize(jCas.getDocumentText());

            // Keep track of the current position relative to the complete text by adding the length of every
            // sentence to the total length. This corresponds the cursor movement over the text
            // Must be done because token positions are relative to the sentence, not to the entire text
            int textRelativePos = 0;
            // Iterate every sentence
            for (int sentenceIdx = 0; sentenceIdx < results.getSentenceCount(); sentenceIdx++) {
                INlpSentence sentence = results.getSentence(sentenceIdx);
                // Check every token in the sentence
                for (int i = 0; i < sentence.getTokenCount(); i++) {
                    ro.uaic.info.nlptools.corpus.Token tokenUaic = sentence.getToken(i);

                    // Token begin position is added to the sentence begin position in the text to get the idx relative
                    // to the actual text. Same for the end idx
                    Token tokenUima = new Token(jCas, textRelativePos + tokenUaic.getCharStartIndexInSentence(),
                            textRelativePos + tokenUaic.getCharEndIndexInSentence());
                    tokenUima.addToIndexes(jCas);
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
