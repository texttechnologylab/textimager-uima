package org.hucompute.textimager.uima.julie.helper;

import de.julielab.jcore.types.Lemma;
import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XCASDeserializer;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.json.JSONML;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Converter
 *
 * @date 27.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provides functions which convert julie-types into dkpro-types
 *
 * Input: jCas
 * Output: jCas*/
public class Converter {
    public Converter(){};

    /**
     * Convert sentences from julie to dkpro and remove julie sentences.
     * @param aJCas
     */
    public void ConvertAndRemoveSentence(JCas aJCas) throws UIMAException, IOException, SAXException {
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence dsentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(aJCas, sentence.getBegin(), sentence.getEnd());
            dsentence.addToIndexes();
            //output loeschen
            sentence.removeFromIndexes(aJCas);
        }
    }

    /**
     * Convert, create tokens from julie to dkpro and remove julie tokens.
     * @param aJCas
     */
    public void ConvertCreateRemoveTokens(JCas aJCas) throws UIMAException, IOException, SAXException {
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token dtoken = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(aJCas, token.getBegin(), token.getEnd());
            dtoken.addToIndexes();
            token.removeFromIndexes(aJCas);
        }
    }

    /**
     * Remove julie sentences.
     * @param aJCas
     */
    public void RemoveSentence(JCas aJCas){
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
            sentence.removeFromIndexes(aJCas);
        }
    }
    /**
     * Remove julie POStag.
     * @param aJCas
     */
    public void RemovePOStag(JCas aJCas){
        for (POSTag posTag : JCasUtil.select(aJCas, POSTag.class)) {
            posTag.removeFromIndexes(aJCas);
        }
    }
    /**
     * Remove julie Lemma.
     * @param aJCas
     */
    public void RemoveLemma(JCas aJCas){
        for (Lemma lemma : JCasUtil.select(aJCas, Lemma.class)) {
            lemma.removeFromIndexes(aJCas);
        }
    }
    /**
     * Convert POS to dkpro and remove julie token
     * @param aJCas
     */
    public void ConvertPOSRemoveToken(JCas aJCas){
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            POS postag = new POS(aJCas, token.getPosTag(0).getBegin(), token.getPosTag(0).getEnd());
            postag.setPosValue(token.getPosTag(0).getValue());
            JCasUtil.selectAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, token.getBegin(), token.getEnd()).get(0).setPos(postag);
            postag.addToIndexes();
            token.removeFromIndexes();
        }
    }
    /**
     * Convert Lemma to dkpro and remove julie token
     * @param aJCas
     */
    public void ConvertLemmaSRemoveToken(JCas aJCas){
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma lemma = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma(aJCas, token.getLemma().getBegin(),  token.getLemma().getEnd());
            lemma.setValue(token.getLemma().getValue());
            JCasUtil.selectAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, token.getBegin(), token.getEnd()).get(0).setLemma(lemma);
            lemma.addToIndexes();
            token.removeFromIndexes();
        }
    }
}
