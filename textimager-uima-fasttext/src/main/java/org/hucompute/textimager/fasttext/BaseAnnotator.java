package org.hucompute.textimager.fasttext;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Basis Klasse für fastText Annotatoren
 */
public abstract class BaseAnnotator extends JCasAnnotator_ImplBase {
    /**
     * Comma separated list of Language and Location and Num Labels of the model.
     * EX: de,model_de.bin,100,en,en_model1.bin,93...
     */
    public static final String PARAM_LANGUAGE_MODELS_LABELS = "language_models_labels";
    @ConfigurationParameter(name = PARAM_LANGUAGE_MODELS_LABELS, mandatory = true)
    protected String language_models_labels;

    /**
     * Location from which the model is read.
     */
    public static final String PARAM_FASTTEXT_LOCATION = "fasttextLocation";
    @ConfigurationParameter(name = PARAM_FASTTEXT_LOCATION, mandatory = true)
    protected String fasttextLocation;

    /**
     * fastText k parameter == Max Anzahl an Labeln.
     */
    public static final String PARAM_FASTTEXT_K = "fasttextK";
    @ConfigurationParameter(name = PARAM_FASTTEXT_K, mandatory = true)
    protected int fasttextK;

    /**
     * Cutoff all labels with lowest score
     */
    public static final String PARAM_CUTOFF = "cutoff";
    @ConfigurationParameter(name = PARAM_CUTOFF, mandatory = false, defaultValue = "false")
    protected boolean cutoff;

    /**
     * Comma separated list of selection to process in order: text (default), paragraph, sentence, token, line (== Div)
     * Add the div type for "line" with a ";", ex: "sentence,line;newline,token"
     */
    public static final String PARAM_SELECTION = "selection";
    @ConfigurationParameter(name = PARAM_SELECTION, mandatory = true)
    protected String selection;

    /**
     * Comma separated list of Tags to add
     */
    public static final String PARAM_TAGS = "tags";
    @ConfigurationParameter(name = PARAM_TAGS, mandatory = false)
    protected String tags;

    /**
     * Use Lemma instead of Token
     */
    public static final String PARAM_USE_LEMMA = "useLemma";
    @ConfigurationParameter(name = PARAM_USE_LEMMA, mandatory = false, defaultValue = "false")
    protected boolean useLemma;

    /**
     * Add POS Info to words
     */
    public static final String PARAM_ADD_POS = "addPOS";
    @ConfigurationParameter(name = PARAM_ADD_POS, mandatory = false, defaultValue = "false")
    protected boolean addPOS;

    /**
     * Location for POS Mapping file
     */
    public static final String PARAM_POSMAP_LOCATION = "posmapLocation";
    @ConfigurationParameter(name = PARAM_POSMAP_LOCATION, mandatory = false, defaultValue = "")
    protected String posmapLocation;

    /**
     * Remove Punctuation from text
     */
    public static final String PARAM_REMOVE_PUNCT = "removePunct";
    @ConfigurationParameter(name = PARAM_REMOVE_PUNCT, mandatory = false, defaultValue = "false")
    protected boolean removePunct;

    /**
     * Remove Functionwords from text
     */
    public static final String PARAM_REMOVE_FUNCTIONWORDS = "removeFunctionwords";
    @ConfigurationParameter(name = PARAM_REMOVE_FUNCTIONWORDS, mandatory = false, defaultValue = "false")
    protected boolean removeFunctionwords;

    /**
     * Lazy Load Models
     */
    public static final String PARAM_LAZY_LOAD = "lazyLoad";
    @ConfigurationParameter(name = PARAM_LAZY_LOAD, mandatory = false, defaultValue = "false")
    protected boolean lazyLoad;

    /**
     * Max loaded Models
     */
    public static final String PARAM_LAZY_LOAD_MAX = "lazyLoadMax";
    @ConfigurationParameter(name = PARAM_LAZY_LOAD_MAX, mandatory = false, defaultValue = "1")
    protected int lazyLoadMax;

    /**
     * Ignore missing Lemma/POS
     */
    public static final String PARAM_IGNORE_MISSING_LEMMA_POS = "ignoreMissingLemmaPOS";
    @ConfigurationParameter(name = PARAM_IGNORE_MISSING_LEMMA_POS, mandatory = false, defaultValue = "true")
    protected boolean ignoreMissingLemmaPOS;


    // POS Mapping
    protected static HashMap<String, String> posMapping = new HashMap<>();

    // fastText Prozesse über StdIn/Out
    protected FastTextBridge fasttext;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        System.out.println("initializing...");
        System.out.println("- useLemma: " + useLemma);
        System.out.println("- addPOS: " + addPOS);
        System.out.println("- removePunct: " + removePunct);
        System.out.println("- removeFunctionwords: " + removeFunctionwords);
        System.out.println("- ignoreMissingLemmaPOS: " + ignoreMissingLemmaPOS);

        // TODO weitere Vorbedingungen prüfen!

        if (removeFunctionwords && posmapLocation.isEmpty()) {
            throw new ResourceInitializationException("removeFunctionwords = true but no posmap location specified", null);
        }

        if (!posmapLocation.isEmpty()) {
            try {
                readPOSMappingFile();
            } catch (IOException e) {
                throw new ResourceInitializationException("error loading posmapping file", null, e);
            }
        }

        fasttext = new FastTextBridge(fasttextLocation, language_models_labels, lazyLoad, lazyLoadMax);

        System.out.println("initializing done.");
    }

    @Override
    public void destroy() {
    	System.out.println("destroying...");

        fasttext.exit();

        super.destroy();
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {

        // TODO let user give Class Names instead of "special" names and use class automatically
        //try {
        //    Class.forName(Paragraph.class.toString());
        //} catch (ClassNotFoundException e) {
        //    e.printStackTrace();
        //}

        String[] selections = selection.split(",", -1);
        for (String sel : selections) {
            if (sel.equals("paragraph")) {
                for (Paragraph paragraph : JCasUtil.select(jCas, Paragraph.class)) {
                    processCoveredWithFastText(jCas, paragraph);
                }
            } else if (sel.equals("sentence")) {
                for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
                    processCoveredWithFastText(jCas, sentence);
                }
            } else if (sel.equals("token")) {
                for (Token token : JCasUtil.select(jCas, Token.class)) {
                    processCoveredWithFastText(jCas, token);
                }
            } else if (sel.startsWith("line")) {
                String divType = null;
                if (sel.contains(";")) {
                    divType = sel.substring(5);
                }
                for (Div div : JCasUtil.select(jCas, Div.class)) {
                    if (divType == null || (div.getDivType() != null && div.getDivType().equals(divType))) {
                        processCoveredWithFastText(jCas, div);
                    }
                }
            } else /*if (sel.equals("text")) */ {
                // text (= alles als Standard)
                processCoveredWithFastText(jCas, null);
            }
        }
    }

    // Funktion die abgeleitete Klassen implementieren müssen
    protected abstract void processCoveredWithFastText(JCas jCas, Annotation ref) throws AnalysisEngineProcessException;

    // Holt den Text aus CAS oder ref mit Option Lemma, POS, Filter...
    protected static String getText(JCas jCas, Annotation ref, boolean useLemma, boolean addPOS, boolean removePunct, boolean removeFunctionwords, boolean ignoreMissingLemmaPOS) {
        StringBuilder sb = new StringBuilder();

        Collection<Token> tokens;
        if (ref != null) {
            tokens = JCasUtil.selectCovered(Token.class, ref);
        } else {
            tokens = JCasUtil.select(jCas, Token.class);
        }

        for (Token token : tokens) {

            String pos = mapPOS(token);

            if (removeFunctionwords) {
                if (isPOSFunctionWord(pos)) {
                    continue;
                }
            }

            String text = getTextFromTokenOrLemma(token, useLemma, ignoreMissingLemmaPOS);

            if (removePunct) {
                if (isPunctuation(text)) {
                    continue;
                }
            }

            sb.append(text);

            if (addPOS) {
                sb.append("_").append(pos);
            }

            sb.append(" ");
        }

        return sb.toString();
    }

    protected static String getTextWithDisambig(JCas jCas, Annotation ref, boolean useLemma, boolean addPOS, boolean removePunct, boolean removeFunctionwords, String disambigTag, String disambigLabelReplace, String disambigLabelReplaceWith, boolean ignoreMissingLemmaPOS) {
        StringBuilder sb = new StringBuilder();

        Collection<Token> tokens;
        if (ref != null) {
            tokens = JCasUtil.selectCovered(Token.class, ref);
        } else {
            tokens = JCasUtil.select(jCas, Token.class);
        }

        for (Token token : tokens) {

            String pos = mapPOS(token);

            if (removeFunctionwords) {
                if (isPOSFunctionWord(pos)) {
                    continue;
                }
            }

            String text = getTextFromTokenOrLemma(token, useLemma, ignoreMissingLemmaPOS);

            if (removePunct) {
                if (isPunctuation(text)) {
                    continue;
                }
            }

            sb.append(text);

            if (addPOS) {
                sb.append("_").append(pos);
            }

            sb.append(" ");

            if (!disambigTag.isEmpty()) {
                ArrayList<CategoryCoveredTagged> disambigs = new ArrayList<>();
                disambigs.addAll(JCasUtil.selectCovered(CategoryCoveredTagged.class, token));
                if (!disambigs.isEmpty()) {
                    // Alle entfernen mit falschem Tag
                    disambigs.removeIf(dis -> {
                        String[] tags = dis.getTags().split(",", -1);
                        for (int ind = 0; ind < tags.length; ++ind) {
                            if (tags[ind].equals(disambigTag)) {
                                return false;
                            }
                        }
                        return true;
                    });

                    // Sortieren
                    Collections.sort(disambigs, (r1, r2) -> ((r1.getScore() > r2.getScore()) ? -1 : ((r1.getScore() < r2.getScore()) ? 1 : 0)));

                    // Label anpassen für weitere Schritte
                    String bestDisambig = disambigs.get(0).getValue();
                    if (!disambigLabelReplace.isEmpty() || !disambigLabelReplaceWith.isEmpty()) {
                        bestDisambig = bestDisambig.replaceFirst(disambigLabelReplace, disambigLabelReplaceWith);
                    }

                    // Bestes hinzufügen
                    sb.append(bestDisambig).append(" ");
                }
            }
        }

        return sb.toString();
    }

    private static String getTextFromTokenOrLemma(Token token, boolean useLemma, boolean ignoreMissingLemmaPOS) {
        String text;
        if (useLemma) {
            Lemma lemma = token.getLemma();
            // Wenn kein Lemma existiert und wir diesen Fehler ignorieren wollen...
            if (lemma == null && ignoreMissingLemmaPOS) {
                text = "";
            } else {
                text = lemma.getValue();
            }
        } else {
            text = token.getCoveredText();
        }
        return text;
    }

    protected void readPOSMappingFile() throws IOException, ResourceInitializationException {
    	System.out.println("loading pos map file: " + posmapLocation);

        posMapping = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(posmapLocation)), Charset.forName("UTF-8")));
        String line;
        int lineCounter = 0;
        while ((line = reader.readLine()) != null) {
            lineCounter++;
            String[] lineData = line.split("\t", 2);

            if (lineData.length != 2) {
                throw new ResourceInitializationException("error loading posmapping file: line " + lineCounter + " is invalid.", null);
            } else {
                posMapping.put(lineData[0], lineData[1]);
            }
        }
        reader.close();
    }

    protected static String mapPOS(Token token) {
        if (token.getPos() == null) {
            return "";
        }

        String posStrOrig = token.getPos().getPosValue();

        if (posMapping.containsKey(posStrOrig)) {
            return posMapping.get(posStrOrig);
        }

        return posStrOrig;
    }

    protected static boolean isPOSFunctionWord(String posStr) {
        return posStr.equals("ART")
                || posStr.startsWith("K")
                || posStr.startsWith("PTK")
                || posStr.startsWith("AP")
                || posStr.startsWith("VM")
                || posStr.startsWith("VA");
    }

    protected static boolean isPunctuation(String input) {
        String temp = input.replaceAll("\\p{P}", " ").replaceAll("\\|", " ").trim();
        return temp.isEmpty();
    }
}
