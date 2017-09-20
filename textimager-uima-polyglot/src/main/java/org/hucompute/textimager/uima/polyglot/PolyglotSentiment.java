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
import sentimentAnnotation.type.SentimentAnnotation;

/**
* PolyglotSentiment
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide Sentiment for 135 languages. (http://polyglot.readthedocs.io/en/latest/Sentiment.html) 
* UIMA-Token are needed as input to create Sentiment.
* UIMA-Standard is used to represent the final Sentiment.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"sentimentAnnotation.type.SentimentAnnotation"})
public class PolyglotSentiment  extends SegmenterBase {
	
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
	 * Analyze the text and create Sentiment-Tag for every word. After successfully creation, add Polarity to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		
		String inputText = aJCas.getDocumentText();
		
		// List of every Token.
		ArrayList<Token> T = new ArrayList<Token>();
		for (Token token : select(aJCas, Token.class)) {
			T.add(token);
		}
		
		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "sentiment", inputText);
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
			
			// Only process sentence if Sentiment-TAG is found.
			if(result.length() != 0 && resultInParts.length > 0) {
				for(int i = 0; i < resultInParts.length; i = i + 1) {
					String[] currentWord = resultInParts[i].split(" ");										
					SentimentAnnotation sentiment = new SentimentAnnotation(aJCas, T.get(i).getBegin(), T.get(i).getEnd());
					sentiment.setValue(currentWord[1]);
					sentiment.addToIndexes();	
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
