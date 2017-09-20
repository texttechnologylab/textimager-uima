package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import tansliterationAnnotation.type.TransliterationAnnotation;

/**
* PolyglotTransliteration
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide Transliteration for 69 languages. (http://polyglot.readthedocs.io/en/latest/Transliteration.html) 
* UIMA-Token are needed as input to create Transliteration.
* UIMA-Standard is used to represent the final Transliteration.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"tansliterationAnnotation.type.TransliterationAnnotation"})
public class PolyglotTransliteration  extends SegmenterBase {
	
	/**
     * Load the PythonPATH
     */
    public static final String PARAM_PYTHON_PATH = "PythonPathPolyglot";
    @ConfigurationParameter(name = PARAM_PYTHON_PATH, mandatory = false)
    protected String PythonPATH;
	
	/**
     * Load the toLanguage-Tag
     */
    public static final String PARAM_TO_LANGUAGE_CODE = "ToLanguageCode";
    @ConfigurationParameter(name = PARAM_TO_LANGUAGE_CODE, mandatory = false)
    protected String toLanguageCode;
	
    public static final String PARAM_POLYGLOT_PATH = "PolyglotPath";
    @ConfigurationParameter(name = PARAM_POLYGLOT_PATH, mandatory = false)
    protected String POLYGLOT_LOCATION;
    
	/**
	 * Analyze the text and create Transliteration-Tag. After successfully creation, add Transliteration to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		String inputText = aJCas.getDocumentText();
		        
    	// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "transliteration", inputText, toLanguageCode);
        pb.redirectError(Redirect.INHERIT);
        
        boolean success = false;
        Process proc = null;
        
        try {
	    	// Start Process
	        proc = pb.start();
	
	        // IN, ERROR Streams
	        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
	      
	        StringBuilder builder = new StringBuilder();
					String line = null;
					while ( (line = in.readLine()) != null) {
					   builder.append(line);
					   builder.append(System.getProperty("line.separator"));
					}
			String result = builder.toString();
			String[] resultInParts = result.split("\n");
			
			// Create an ArrayList of all token, because Transliteration-library doesn't output begin/end of token. Calculate it manually.
			ArrayList<Token> T = new ArrayList<Token>();
			for (Token token : select(aJCas, Token.class)) {
				T.add(token);
			}

			// Only process sentence if Transliteration is found.
			if(result.length() != 0 && resultInParts.length > 0) {
				// Create transliteration for every token.
				for(int i = 0; i < resultInParts.length; i++) {
					TransliterationAnnotation transliteration = new TransliterationAnnotation(aJCas, T.get(i).getBegin(), T.get(i).getEnd());
					transliteration.setValue(resultInParts[i]);
					transliteration.addToIndexes();
				}
			}
				
	        // Get Errors
             String errorString = "";
			 line = "";
			 try {
				while ((line = error.readLine()) != null) {
					errorString += line+"\n";
				}
			 } catch (IOException e) {
				e.printStackTrace();
			 }

			 // Log Error
			 if(errorString != "")
			 getLogger().error(errorString);
			 
             success = true;
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
        
        finally {
            if (!success) {

            }
            
            if (proc != null) {
                proc.destroy();
            }
        }
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
			
	}

}
