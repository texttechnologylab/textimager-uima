package org.hucompute.textimager.uima.german.emotion.detection;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.hucompute.textimager.uima.base.TextImagerBaseAnnotator;

import org.bitbucket.rkilinger.ged.Emotion;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class GermanEmotionDetection extends TextImagerBaseAnnotator {
    /**
     *
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION)
    protected String path;
    public HashSet<String> disgustSet;
    public HashSet<String> contemptSet;
    public HashSet<String> surpriseSet;
    public HashSet<String> fearSet;
    public HashSet<String> mourningSet;
    public HashSet<String> angerSet;
    public HashSet<String> joySet;

    @Override
    protected String getAnnotatorVersion() {
        return "0.1";
    }

    @Override
    protected String getModelVersion() {
        return "0.1";
    }

    @Override
    protected String getModelName() {
        return "0.1";
    }

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        // load lists
        disgustSet = readDictFromFile(path + "Ekel.txt");
		contemptSet = readDictFromFile(new String(path+ "Verachtung.txt"));
		surpriseSet =  readDictFromFile(new String(path+ "Ueberraschung.txt"));
		fearSet = readDictFromFile(new String(path + "Furcht.txt"));
		mourningSet = readDictFromFile(new String(path+ "Trauer.txt"));
		angerSet = readDictFromFile(new String(path + "Wut.txt"));
		joySet = readDictFromFile(new String(path + "Freude.txt"));
    }

    //@Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        // For each Token check whether it is contained in any set
        for (Token tk : JCasUtil.select(aJCas, Token.class)) {
            String tkText = tk.getCoveredText();
            Emotion em = new Emotion(aJCas, tk.getBegin(), tk.getEnd());
            if(disgustSet.contains(tkText)){
                em.setDisgust(1);
            }
            if(contemptSet.contains(tkText)){
                em.setContempt(1);
            }
            if(surpriseSet.contains(tkText)){
                em.setSurprise(1);
            }
            if(fearSet.contains(tkText)){
                em.setFear(1);
            }
            if(mourningSet.contains(tkText)){
                em.setMourning(1);
            }
            if(angerSet.contains(tkText)){
                em.setAnger(1);
            }
            if(joySet.contains(tkText)){
                em.setJoy(1);
            }
            em.addToIndexes();
            addAnnotatorComment(aJCas, em);
        }

    }
    private HashSet<String> readDictFromFile(String fileName) {
        HashSet<String> returnDict = new HashSet<String>();
        try {
            Scanner textFile = new Scanner(new File(fileName));
            while (textFile.hasNext()) {
                returnDict.add(textFile.next().trim());
            }
            textFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return returnDict;
    }
}
