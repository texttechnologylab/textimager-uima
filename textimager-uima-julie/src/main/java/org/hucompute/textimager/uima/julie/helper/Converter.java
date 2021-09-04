package org.hucompute.textimager.uima.julie.helper;

import de.julielab.jcore.types.*;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ABBREV;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ABBREV_Type;
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
     * Remove julie tokens.
     * @param aJCas
     */
    public void RemoveToken(JCas aJCas){
        for (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token : JCasUtil.select(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class)) {
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
     * Remove julie StemForm.
     * @param aJCas
     */
    public void RemoveStem(JCas aJCas){
        for (StemmedForm stem : JCasUtil.select(aJCas, StemmedForm.class)) {
            stem.removeFromIndexes(aJCas);
        }
    }
    /**
     * Remove julie Chunk.
     * @param aJCas
     */
    public void RemoveChunk(JCas aJCas){
        for (de.julielab.jcore.types.Chunk chunk : JCasUtil.select(aJCas, de.julielab.jcore.types.Chunk.class)) {
            chunk.removeFromIndexes(aJCas);
        }
    }
    /**
     * Remove julie Abbreviation.
     * @param aJCas
     */
    public void RemoveAbbreviation(JCas aJCas){
        for (Abbreviation abbreviation : JCasUtil.select(aJCas, Abbreviation.class)) {
            abbreviation.removeFromIndexes(aJCas);
        }
    }
    /**
     * Remove julie Constituent.
     * @param aJCas
     */
    public void RemoveConstituent(JCas aJCas){
        for (Constituent constituent : JCasUtil.select(aJCas, Constituent.class)) {
            constituent.removeFromIndexes(aJCas);
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
    public void ConvertLemmaRemoveToken(JCas aJCas){
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma lemma = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma(aJCas, token.getLemma().getBegin(),  token.getLemma().getEnd());
            lemma.setValue(token.getLemma().getValue());
            JCasUtil.selectAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, token.getBegin(), token.getEnd()).get(0).setLemma(lemma);
            lemma.addToIndexes();
            token.removeFromIndexes();
        }
    }
    /**
     * Convert Stem to dkpro and remove julie token
     * @param aJCas
     */
    public void ConvertStemRemoveToken(JCas aJCas){
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            Stem stem = new Stem(aJCas, token.getStemmedForm().getBegin(),  token.getStemmedForm().getEnd());
            stem.setValue(token.getStemmedForm().getValue());
            JCasUtil.selectAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, token.getBegin(), token.getEnd()).get(0).setStem(stem);
            stem.addToIndexes();
            token.removeFromIndexes();
        }
    }
    /**
     * Convert Chunk to dkpro
     * @param aJCas
     */
    public void ConvertChunk(JCas aJCas){
        for (de.julielab.jcore.types.Chunk chunk : JCasUtil.select(aJCas, de.julielab.jcore.types.Chunk.class)) {
            Chunk chunk_dkpro = new Chunk(aJCas, chunk.getBegin(),  chunk.getEnd());
            chunk_dkpro.setChunkValue(chunk.getType().getShortName());
            chunk_dkpro.addToIndexes();
        }
    }
    /**
     * Convert ABBREV to dkpro
     * @param aJCas
     */
    public void ConvertABBREV(JCas aJCas){
        for (Abbreviation abbreviation : JCasUtil.select(aJCas, Abbreviation.class)) {
            ABBREV abbrev = new ABBREV(aJCas, abbreviation.getBegin(), abbreviation.getEnd());
            abbrev.setDependencyType(abbreviation.getCoveredText());
            abbrev.addToIndexes();
        }
    }
    /**
     * Convert Constituent to dkpro
     * @param aJCas
     */
    public void ConvertConstituent(JCas aJCas){
        for (Constituent constituent : JCasUtil.select(aJCas, Constituent.class)) {
            de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent constituent_dkpro = new de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent(aJCas, constituent.getBegin(), constituent.getEnd());
            constituent_dkpro.setConstituentType(constituent.getCat());
            constituent_dkpro.addToIndexes();
        }
    }
}
