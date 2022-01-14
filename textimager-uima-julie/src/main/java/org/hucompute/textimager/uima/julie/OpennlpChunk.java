package org.hucompute.textimager.uima.julie;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.julie.helper.Converter;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

public class OpennlpChunk extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpChunk";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
    /**
     * Read Json and update jCas.
     * @param aJCas
     */
    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        try {
            JsonReader reader = new JsonReader();
            reader.UpdateJsonToCas(jsonResult, aJCas);

            Converter conv = new Converter();
            conv.ConvertChunk(aJCas);

            //remove input: POStag, Sentence, Token
            conv.RemovePOStag(aJCas);
            conv.RemoveSentence(aJCas);
            conv.RemoveToken(aJCas);
            //remove output: Chunk
            conv.RemoveChunk(aJCas);

//            for (Token token : JCasUtil.select(aJCas, Token.class)) {
//                de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token dtoken = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(aJCas, token.getBegin(), token.getEnd());
//                Lemma lemma = new Lemma(aJCas, token.getLemma().getBegin(), token.getLemma().getEnd());
//                lemma.setValue(token.getLemma().getValue());
//                dtoken.setLemma(lemma);
//                dtoken.addToIndexes();
//            }
        } catch (UIMAException | IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
}

