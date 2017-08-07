package org.hucompute.textimager.uima.polyglot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

/**
* PolyglotSentenceBoundary
*
* @date 07.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide sentence detection for different languages. 
* UIMA-Standard is used to represent the final sentence.
*/
@TypeCapability(outputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class PolyglotSentenceBoundary  extends SegmenterBase {
	
	/**
	 * Analyze the text and create sentences. After successfully creation, add sentences to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		String inputText = aJCas.getDocumentText();
		
		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", POLYGLOT_LOCATION + "language.py", "sentence", inputText);
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
					
			int startPosition = 0;
			int endPosition = 0;		
					
			for (String currentSentence : resultInParts) {
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
