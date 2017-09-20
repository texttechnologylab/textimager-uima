package org.hucompute.textimager.uima.polyglot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

/**
* PolyglotSentenceBoundary
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide sentence detection for different languages. 
* UIMA-Standard is used to represent the final sentence.
*/
@TypeCapability(outputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class PolyglotSentenceBoundary  extends SegmenterBase {
	
	/**
     * Load the PythonPATH
     */
    public static final String PARAM_PYTHON_PATH = "PythonPathPolyglot";
    @ConfigurationParameter(name = PARAM_PYTHON_PATH, mandatory = false)
    protected String PythonPATH;
    
    public static final String PARAM_POLYGLOT_PATH = "PolyglotPath";
    @ConfigurationParameter(name = PARAM_POLYGLOT_PATH, mandatory = false)
    protected String POLYGLOT_LOCATION;
    
	/**
	 * Analyze the text and create sentences. After successfully creation, add sentences to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		String inputText = aJCas.getDocumentText();
		
		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "sentence", inputText);
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
			
			String documentText = aJCas.getDocumentText();
			int startPosition = 0;
			int endPosition = 0;		
			
			for (String currentSentence : resultInParts) {
				while(documentText.startsWith("\n")) {
					// Multiple line breaks.
					startPosition = startPosition + 1;
					documentText = documentText.substring(1);
				}
				
				if(documentText.length() > currentSentence.length() + 1) {
					// Remove text to calculate next start-Position
					documentText = documentText.substring(currentSentence.length() + 1);
				} else {
					// Last sentence
					documentText = documentText.substring(currentSentence.length());
				}
									
				// Create end-Tag
				endPosition = startPosition + currentSentence.length();
				// Create sentence
	            Sentence sentence = new Sentence(aJCas, startPosition, endPosition);
	            // Create next start-Tag
	            startPosition = endPosition + 1;
	            // Add to JCas-Index
	            sentence.addToIndexes(aJCas);
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
