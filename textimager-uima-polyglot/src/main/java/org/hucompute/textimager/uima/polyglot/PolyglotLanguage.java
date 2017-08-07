package org.hucompute.textimager.uima.polyglot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;

/**
* PolyglotLanguage
*
* @date 04.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide language recognition for different languages. 
* Language code is used for JCas document language.
*/
public class PolyglotLanguage  extends SegmenterBase {
	
	/**
	 * Analyze the text and recognize language. After successfully recognition, add language code to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		String inputText = aJCas.getDocumentText();
		
		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", POLYGLOT_LOCATION + "language.py", "language", inputText);
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
			String[] resultInParts = result.split("code: ");
			resultInParts = resultInParts[1].split("       ");   
								
			aJCas.setDocumentLanguage(resultInParts[0]);				
					
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
