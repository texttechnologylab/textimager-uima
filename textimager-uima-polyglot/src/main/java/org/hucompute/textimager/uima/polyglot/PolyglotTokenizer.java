package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* PolyglotTokenizer
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide tokenization for 165 languages. 
* UIMA-Standard is used to represent the final token.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" }
		)
public class PolyglotTokenizer  extends SegmenterBase {
	
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
	 * Analyze the text and create tokens for every word. After successfully creation, add tokens to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		
		for (Sentence sentence : select(aJCas, Sentence.class)) {		
			// Define ProcessBuilder
	        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "token", sentence.getCoveredText());
	        pb.redirectError(Redirect.INHERIT);
	        
	        boolean success = false;
	        Process proc = null;
	        
	        try {
		    	// Start Process
		        proc = pb.start();
		
		        // IN, OUT, ERROR Streams
		        PrintWriter out = new PrintWriter(new OutputStreamWriter(proc.getOutputStream()));
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
						
				String currentSentence = sentence.getCoveredText();
				int currentTextPosition = 0;
				int lastWordLength = 0;
				int wordOffset = 0;
				int startPosition = 0;
				int endPosition = 0;
				
				// Calculate word begin and end.
				for (String currentWord : resultInParts) {				
					currentSentence = currentSentence.substring(lastWordLength);
					wordOffset = currentSentence.indexOf(currentWord);
					currentSentence = currentSentence.substring(wordOffset);
					startPosition = sentence.getBegin() + wordOffset + currentTextPosition;
					endPosition = startPosition + currentWord.length();
					currentTextPosition = endPosition - sentence.getBegin();	
					lastWordLength = currentWord.length();
					
					// Create UIMA-Token: Offset next startPosition by 1 to fit UIMA-Standard.					
			        Token token = new Token(aJCas, startPosition, endPosition);		
		        	token.addToIndexes(aJCas);
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
	}

	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
			
	}

}
