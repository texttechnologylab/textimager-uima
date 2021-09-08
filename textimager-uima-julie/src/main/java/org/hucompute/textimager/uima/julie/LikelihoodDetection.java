package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.julie.helper.Converter;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

public class LikelihoodDetection extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/likelihoodDetection";
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

            //remove input: Token, Lemma
            conv.RemoveSentence(aJCas);
            conv.RemoveToken(aJCas);
            conv.RemoveLemma(aJCas);

        /*for (Token token: JCasUtil.select(aJCas, Token.class))
        {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token dtoken = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(aJCas, token.getBegin(), token.getEnd());
            POS postag = new POS(aJCas, token.getPosTag(0).getBegin(), token.getPosTag(0).getEnd());
            postag.setPosValue(token.getPosTag(0).getValue());
            dtoken.setPos(postag);
            dtoken.addToIndexes();
        }*/
        } catch (UIMAException | IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
}
