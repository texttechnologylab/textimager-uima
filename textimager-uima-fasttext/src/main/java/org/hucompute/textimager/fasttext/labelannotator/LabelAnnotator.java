package org.hucompute.textimager.fasttext.labelannotator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.fasttext.BaseAnnotator;
import org.hucompute.textimager.fasttext.FastTextResult;
import org.hucompute.textimager.fasttext.ProbabilityLabel;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

public class LabelAnnotator extends BaseAnnotator {
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
     * Append DDC to the input text, set to tag to search for
     */
    public static final String PARAM_APPEND_DDC = "appendDDC";
    @ConfigurationParameter(name = PARAM_APPEND_DDC, mandatory = false, defaultValue = "")
    protected String appendDDC;

    /**
     * Append DDC variant
     */
    public static final String PARAM_APPEND_DDC_VARIANT = "appendDDCVariant";
    @ConfigurationParameter(name = PARAM_APPEND_DDC_VARIANT, mandatory = false, defaultValue = "")
    protected String appendDDCVariant;
    
    /**
     * DDC class names mapping file
     */
    public static final String PARAM_DDC_CLASS_NAMES_FILENAME = "ddcClassNamesFilename";
    @ConfigurationParameter(name = PARAM_DDC_CLASS_NAMES_FILENAME, mandatory = false, defaultValue = "")
    protected String ddcClassNamesFilename;

    private HashMap<String, HashMap<String, String>> ddcNames;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        ddcNames = new HashMap<>();
        if (!appendDDC.isEmpty()) {
        	String[] ddcClassNamesFilenames = ddcClassNamesFilename.split(",", -1);
        	for (String entry : ddcClassNamesFilenames) {
        		String[] entryFields = entry.split(":", 2);
        		String lang = entryFields[0].trim();
        		String filename = entryFields[1].trim();
        		
	            System.out.println("loading ddc class names for language " + lang + " from file " + filename + "...");
				try {
					ddcNames.put(lang, new HashMap<>());
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8")));
		            String line;
		            while ((line = reader.readLine()) != null) {
		            	line = line.trim();
		            	if (!line.isEmpty()) {
		            		String[] fields = line.split("\t");
		            		if (fields.length == 2) {
		            			String id = fields[0];
		            			String name = fields[1];
		            			ddcNames.get(lang).put("__label_ddc__" + id.toString(), name);
		            		}
		            	}
		            }
		            reader.close();
				} catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
				System.out.println("loaded " + ddcNames.get(lang).size() + " ddc class names.");
	        }

        	System.out.println("loaded ddc class names for " + ddcNames.size() + " languages.");
        }
    }
    
    // __label_ddc__480 -> __ddc__480
    private String ddcLabelToFeature(String ddc) {
    	return "__ddc__" + ddc.substring(13);
    }

    @Override
    protected void processCoveredWithFastText(JCas jCas, Annotation ref) throws AnalysisEngineProcessException {
        String documentText = getTextWithDisambig(jCas, ref, useLemma, addPOS, removePunct, removeFunctionwords, disambigTag, disambigLabelReplace, disambigLabelReplaceWith, ignoreMissingLemmaPOS);
        System.out.println(documentText);
        if (documentText.isEmpty()) {
            return;
        }

        if (!appendDDC.isEmpty()) {
            // Add DDC Predictions to the Text...
            StringBuilder ddcsSB = new StringBuilder();
            Collection<CategoryCoveredTagged> ddcCats = JCasUtil.select(jCas, CategoryCoveredTagged.class);
            ArrayList<CategoryCoveredTagged> ddcCatsSorted = new ArrayList<>();
            for (CategoryCoveredTagged ddcCat : ddcCats) {
                if (ddcCat.getTags().equals(appendDDC)) {
                    ddcCatsSorted.add(ddcCat);
                }
            }

            if (!ddcCatsSorted.isEmpty()) {
                Collections.sort(ddcCatsSorted, (r1, r2) -> ((r1.getScore() > r2.getScore()) ? -1 : ((r1.getScore() < r2.getScore()) ? 1 : 0)));

                System.out.println("ddc variant: " + appendDDCVariant);

                if (appendDDCVariant.equals("top_10x")) {
                    CategoryCoveredTagged topCat = ddcCatsSorted.get(0);

                    System.out.println("top ddc: " + topCat.getValue());
                    System.out.println("top score: " + topCat.getScore());

                    for (int i = 0; i < 10; ++i) {
                        ddcsSB.append(" ").append(ddcLabelToFeature(topCat.getValue()));
                        if (ddcNames.containsKey(jCas.getDocumentLanguage())) {
	                        if (ddcNames.get(jCas.getDocumentLanguage()).containsKey(topCat.getValue())) {
	                            ddcsSB.append(" ").append(ddcNames.get(jCas.getDocumentLanguage()).get(topCat.getValue()));
	                        }
                        }
                    }
                } else if (appendDDCVariant.equals("top_scorex")) {
                    CategoryCoveredTagged topCat = ddcCatsSorted.get(0);

                    System.out.println("top ddc: " + topCat.getValue());
                    System.out.println("top score: " + topCat.getScore());

                    int reps = Math.max(1, (int)(topCat.getScore()*10));

                    System.out.println("-> reps: " + reps);

                    for (int i = 0; i < reps; ++i) {
                        ddcsSB.append(" ").append(ddcLabelToFeature(topCat.getValue()));
                        if (ddcNames.containsKey(jCas.getDocumentLanguage())) {
	                        if (ddcNames.get(jCas.getDocumentLanguage()).containsKey(topCat.getValue())) {
	                            ddcsSB.append(" ").append(ddcNames.get(jCas.getDocumentLanguage()).get(topCat.getValue()));
	                        }
                        }
                    }
                } else if (appendDDCVariant.equals("top_text_length_x")) {
                    CategoryCoveredTagged topCat = ddcCatsSorted.get(0);

                    System.out.println("top ddc: " + topCat.getValue());
                    System.out.println("top score: " + topCat.getScore());
                    
                    int reps = 10;
                    int textLen = documentText.length();
                    if (textLen < 1000) {
                    	reps = Math.max(1, textLen / 100);
                    }

                    System.out.println("-> reps: " + reps);

                    for (int i = 0; i < reps; ++i) {
                        ddcsSB.append(" ").append(ddcLabelToFeature(topCat.getValue()));
                        if (ddcNames.containsKey(jCas.getDocumentLanguage())) {
	                        if (ddcNames.get(jCas.getDocumentLanguage()).containsKey(topCat.getValue())) {
	                            ddcsSB.append(" ").append(ddcNames.get(jCas.getDocumentLanguage()).get(topCat.getValue()));
	                        }
                        }
                    }
                }
            }

            String ddcs = ddcsSB.toString();
            System.out.println("Found DDC Predictions: " + ddcs);

            documentText += ddcs;
        }

        // Begin und End setzen, entweder passend zu Ref oder kompletter Text
        int begin = (ref != null ? ref.getBegin() : 0);
        int end = (ref != null ? ref.getEnd() : jCas.getDocumentText().length());

        try {
            // result is a list, there can be more than one model loaded
            ArrayList<FastTextResult> results = fasttext.input(jCas.getDocumentLanguage(), documentText);

            for (FastTextResult ftResult : results) {
                ArrayList<ProbabilityLabel> labels = ftResult.getSortedResults(cutoff);

                // Insgesamt nur "fasttextK" Tags ausgeben
                // TODO Pro Model?
                int num = 0;
                for (ProbabilityLabel result : labels) {
                    if (num >= fasttextK) {
                        break;
                    }
                    num++;

                    CategoryCoveredTagged cat = new CategoryCoveredTagged(jCas, begin, end);
                    cat.setValue(result.getLabel());
                    cat.setScore(result.getLogProb());
                    cat.setTags(tags);
                    cat.setRef(ref);
                    cat.addToIndexes();
                }
            }
        } catch (Exception ex) {
            throw new AnalysisEngineProcessException("error processing: " + ex.getMessage(), null, ex);
        }
    }
}
