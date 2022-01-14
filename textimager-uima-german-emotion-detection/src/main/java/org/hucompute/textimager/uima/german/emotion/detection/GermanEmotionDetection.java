package org.hucompute.textimager.uima.german.emotion.detection;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bitbucket.rkilinger.ged.Emotion;
import org.hucompute.textimager.uima.base.TextImagerBaseAnnotator;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

public class GermanEmotionDetection extends TextImagerBaseAnnotator {

    public HashSet<String> disgustSet;
    public HashSet<String> contemptSet;
    public HashSet<String> surpriseSet;
    public HashSet<String> fearSet;
    public HashSet<String> mourningSet;
    public HashSet<String> angerSet;
    public HashSet<String> joySet;

    @Override
    protected String getAnnotatorVersion() {
        return "0.2";
    }

    @Override
    protected String getModelVersion() {
        return "0.1";
    }

    @Override
    protected String getModelName() {
        return "GermanEmotionDetection";
    }

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        // load lists
        disgustSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Ekel.txt"));
        contemptSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Verachtung.txt"));
        surpriseSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Ueberraschung.txt"));
        fearSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Furcht.txt"));
        mourningSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Trauer.txt"));
        angerSet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Wut.txt"));
        joySet = readDictFromStream(GermanEmotionDetection.class.getResourceAsStream("/org/hucompute/textimager/uima/german/emotion/dictionaries/Freude.txt"));
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        // For each Token check whether it is contained in any set
        for (Token tk : JCasUtil.select(aJCas, Token.class)) {
            String tkText = tk.getLemma() != null ? tk.getLemma().getValue() : tk.getCoveredText();

            Emotion em = new Emotion(aJCas, tk.getBegin(), tk.getEnd());
            boolean bFound = false;
            if(disgustSet.contains(tkText)){
                em.setDisgust(1);
                bFound = true;
            }
            if(contemptSet.contains(tkText)){
                em.setContempt(1);
                bFound = true;
            }
            if(surpriseSet.contains(tkText)){
                em.setSurprise(1);
                bFound = true;
            }
            if(fearSet.contains(tkText)){
                em.setFear(1);
                bFound = true;
            }
            if(mourningSet.contains(tkText)){
                em.setMourning(1);
                bFound = true;
            }
            if(angerSet.contains(tkText)){
                em.setAnger(1);
                bFound = true;
            }
            if(joySet.contains(tkText)){
                em.setJoy(1);
                bFound = true;
            }
            if (bFound) {
                em.addToIndexes();
                addAnnotatorComment(aJCas, em);
            }
        }

    }

    private HashSet<String> readDictFromStream(InputStream inputStream) {
        HashSet<String> returnDict = new HashSet<>();
        Scanner textFile = new Scanner(inputStream);
        while (textFile.hasNext()) {
            returnDict.add(textFile.next().trim());
        }
        textFile.close();

        return returnDict;
    }
}
