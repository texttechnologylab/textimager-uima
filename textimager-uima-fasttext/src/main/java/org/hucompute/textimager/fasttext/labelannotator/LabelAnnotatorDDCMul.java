package org.hucompute.textimager.fasttext.labelannotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.fasttext.BaseAnnotator;
import org.hucompute.textimager.fasttext.FastTextBridge;
import org.hucompute.textimager.fasttext.FastTextResult;
import org.hucompute.textimager.fasttext.ProbabilityLabel;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/*
 * Führt DDC2 und DDC3 durch, multipliziert dann die Ergebnisse
 */
public class LabelAnnotatorDDCMul extends BaseAnnotator {
    /**
     * Comma separated list of Language and Location and Num Labels of the model.
     * EX: de,model_de.bin,100,en,en_model1.bin,93...
     */
    public static final String PARAM_LANGUAGE_MODELS_LABELS_DDC3 = "language_models_labels_ddc3";
    @ConfigurationParameter(name = PARAM_LANGUAGE_MODELS_LABELS_DDC3, mandatory = true)
    protected String language_models_labels_ddc3;

    /**
     * Remove DDC2/DDC3 Scores
     */
    public static final String PARAM_REMOVE_OLD_SCORES = "remove_old_scores";
    @ConfigurationParameter(name = PARAM_REMOVE_OLD_SCORES, mandatory = false, defaultValue = "true")
    protected boolean removeOldScores;

    /**
     * Tag of Disambig Results
     */
    public static final String PARAM_DISAMBIG_TAG = "disambigTag";
    @ConfigurationParameter(name = PARAM_DISAMBIG_TAG, mandatory = false, defaultValue = "")
    protected String disambigTag;

    /**
     * Disambig Label Replace: Ersetzt erstes Vorkommen
     * BSP:
     * Label für Disambig  =   __label__1234_Wort
     * Label für DDC       =   __disambig_word__1234_Wort
     */
    public static final String PARAM_DISAMBIG_LABEL_REPLACE = "disambigLabelReplace";
    @ConfigurationParameter(name = PARAM_DISAMBIG_LABEL_REPLACE, mandatory = false, defaultValue = "__label__")
    protected String disambigLabelReplace;
    public static final String PARAM_DISAMBIG_LABEL_REPLACE_WITH = "disambigLabelReplaceWith";
    @ConfigurationParameter(name = PARAM_DISAMBIG_LABEL_REPLACE_WITH, mandatory = false, defaultValue = "__disambig_word__")
    protected String disambigLabelReplaceWith;

    /**
     * Comma separated list of Tags to add
     */
    public static final String PARAM_TAGS_DDC2 = "tagsDDC2";
    @ConfigurationParameter(name = PARAM_TAGS_DDC2, mandatory = false)
    protected String tagsDDC2;

    /**
     * Comma separated list of Tags to add
     */
    public static final String PARAM_TAGS_DDC3 = "tagsDDC3";
    @ConfigurationParameter(name = PARAM_TAGS_DDC3, mandatory = false)
    protected String tagsDDC3;

    // 2. fastText Prozess für DDC3
    private FastTextBridge fasttext_ddc3;

    // DDC2 / DDC3 Ergebnisse speichern
    private class CategoryCoveredTaggedSimple {
        public int begin;
        public int end;
        public String value;
        public double score;
        public Annotation ref;
    }

    // Speichern der Labels der 2. / 3. Ebene
    protected HashMap<String, ArrayList<CategoryCoveredTaggedSimple>> categoriesLevel2 = new HashMap<>();
    protected HashMap<String, ArrayList<CategoryCoveredTaggedSimple>> categoriesLevel3 = new HashMap<>();

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {

        super.initialize(aContext);

        System.out.println("initializing additional fastText for DDC3");

        fasttext_ddc3 = new FastTextBridge(fasttextLocation, language_models_labels_ddc3, lazyLoad, lazyLoadMax);

        categoriesLevel2 = new HashMap<>();
        categoriesLevel3 = new HashMap<>();

    }

    @Override
    public void destroy() {
    	System.out.println("destroying...");

        fasttext_ddc3.exit();

        super.destroy();
    }

    // Prüft ob 1/2 Stelle von DDC2 und DDC3 Label übereinstimmen
    private boolean levelsMatch(CategoryCoveredTaggedSimple catLevel2, CategoryCoveredTaggedSimple catLevel3) {

        int l3 = catLevel3.value.length();
        int l2 = catLevel2.value.length();

        char l3_1 = catLevel3.value.charAt(l3 - 2);
        char l3_2 = catLevel3.value.charAt(l3 - 3);
        char l2_1 = catLevel2.value.charAt(l2 - 2);
        char l2_2 = catLevel2.value.charAt(l2 - 3);

        return (l3_1 == l2_1 && l3_2 == l2_2);

    }

    @Override
    protected void processCoveredWithFastText(JCas jCas, Annotation ref) throws AnalysisEngineProcessException {

        String documentText = getTextWithDisambig(jCas, ref, useLemma, addPOS, removePunct, removeFunctionwords, disambigTag, disambigLabelReplace, disambigLabelReplaceWith, ignoreMissingLemmaPOS);
        System.out.println(documentText);

        // Begin und End setzen, entweder passend zu Ref oder kompletter Text
        int begin = (ref != null ? ref.getBegin() : 0);
        int end = (ref != null ? ref.getEnd() : jCas.getDocumentText().length());

        try {
            // FastText für DDC2 und DDC3 aufrufen
            addDDC(jCas, ref, begin, end, documentText, fasttext_ddc3, categoriesLevel3, tagsDDC3);
            addDDC(jCas, ref, begin, end, documentText, fasttext, categoriesLevel2, tagsDDC2);
        } catch (Exception ex) {
            throw new AnalysisEngineProcessException("error processing: " + ex.getMessage(), null, ex);
        }

        ArrayList<CategoryCoveredTaggedSimple> finalList = new ArrayList<>();

        // Alle mit passedem begin/end durchgehen und multiplizieren
        for (HashMap.Entry<String, ArrayList<CategoryCoveredTaggedSimple>> level2 : categoriesLevel2.entrySet()) {
            for (CategoryCoveredTaggedSimple catLevel2 : level2.getValue()) {
                // Alle zu diesem Begin/End passenden Level3 holen
                ArrayList<CategoryCoveredTaggedSimple> level3List = categoriesLevel3.get(beginEndKey(catLevel2.begin, catLevel2.end));
                for (CategoryCoveredTaggedSimple catLevel3 : level3List) {
                    // Passen die Label?
                    // Wenn in Ebene 2 DDC520 rauskommt, dann alle aus Ebene 2 mit DDC52_ multiplizieren
                    if (levelsMatch(catLevel2, catLevel3)) {

                        // Label von DDC3 nehmen (ohne "_old")
                        String label = catLevel3.value;

                        // Neuer Score ist Produkt der beiden Ebenen
                        double score = catLevel2.score * catLevel3.score;

                        // Ref von Category3
                        Annotation newRef = catLevel3.ref;

                        CategoryCoveredTaggedSimple c = new CategoryCoveredTaggedSimple();
                        c.begin = catLevel3.begin;
                        c.end = catLevel3.end;
                        c.value = label;
                        c.score = score;
                        c.ref = newRef;
                        finalList.add(c);
                    }
                }
            }
        }

        //System.out.println("ddcMul matching done");
        // Sortieren
        Collections.sort(finalList, (r1, r2) -> ((r1.score > r2.score) ? -1 : ((r1.score < r2.score) ? 1 : 0)));
        if (cutoff) {
            CategoryCoveredTaggedSimple lowest = finalList.get(finalList.size() - 1);
            finalList.removeIf(v -> v.score <= lowest.score);
            // Letztes wieder hinzufügen um niedrigsten Wert zu behalten
            finalList.add(lowest);
        }

        //System.out.println("ddcMul sorting done");
        int num = 0;
        for (CategoryCoveredTaggedSimple c : finalList) {
            if (num >= fasttextK) {
                break;
            }
            num++;

            CategoryCoveredTagged cat = new CategoryCoveredTagged(jCas, c.begin, c.end);
            cat.setValue(c.value);
            cat.setScore(c.score);
            cat.setTags(tags);
            cat.setRef(c.ref);
            cat.addToIndexes();
        }

        categoriesLevel2 = new HashMap<>();
        categoriesLevel3 = new HashMap<>();

    }

    private String beginEndKey(int begin, int end) {
        return String.valueOf(begin + "_" + end);
    }

    private void addDDC(JCas jCas, Annotation ref, int begin, int end, String documentText, FastTextBridge ft, HashMap<String, ArrayList<CategoryCoveredTaggedSimple>> list, String tagsDDC) throws AnnotatorProcessException {

        if (documentText.isEmpty()) {
            return;
        }

        // Only supports 1 model!
        FastTextResult ftResult = ft.input(jCas.getDocumentLanguage(), documentText).get(0);
        ArrayList<ProbabilityLabel> labels = ftResult.getSortedResults(false);

        int num = 0;
        for (ProbabilityLabel result : labels) {
            num++;

            CategoryCoveredTaggedSimple c = new CategoryCoveredTaggedSimple();
            c.begin = begin;
            c.end = end;
            c.value = result.getLabel();
            c.score = result.getLogProb();
            c.ref = ref;

            String key = beginEndKey(begin, end);
            if (!list.containsKey(key)) {
                list.put(key, new ArrayList<>());
            }
            list.get(key).add(c);

            // Ergebnisse dem CAS hinzufügen
            if (!removeOldScores && num <= fasttextK) {
                CategoryCoveredTagged cat = new CategoryCoveredTagged(jCas, begin, end);
                cat.setValue(result.getLabel());
                cat.setScore(result.getLogProb());
                cat.setTags(tagsDDC);
                cat.setRef(ref);
                cat.addToIndexes(jCas);
            }
        }

    }
}
