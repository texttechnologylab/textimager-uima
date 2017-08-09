package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import morphemeAnnotation.type.MorphemeAnnotation;
import sentimentAnnotation.type.SentimentAnnotation;

/**
* PolyglotPolarity
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide NER for 40 languages. (http://polyglot.readthedocs.io/en/latest/NamedEntityRecognition.html) 
* UIMA-Token are needed as input to create POS.
* UIMA-Standard is used to represent the final Polarity.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"polarityAnnotation.type.PolarityAnnotation"})
public class PolyglotSentiment  extends SegmenterBase {
	
	/**
	 * Analyze the text and create Polarity-Tag for every word. After successfully creation, add Polarity to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		String inputText = aJCas.getDocumentText();
		
		// List of every Token.
		ArrayList<Token> T = new ArrayList<Token>();
		for (Token token : select(aJCas, Token.class)) {
			T.add(token);
		}
		
		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", POLYGLOT_LOCATION + "language.py", "polarity", inputText);
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
							
			// Only process sentence if Polarity-TAG is found.
			if(result.length() != 0 && resultInParts.length > 0) {
				for(int i = 0; i < resultInParts.length; i = i + 1) {
					String[] currentWord = resultInParts[i].split(" ");										
					SentimentAnnotation polarity = new SentimentAnnotation(aJCas, T.get(i).getBegin(), T.get(i).getEnd());
					polarity.setValue(currentWord[1]);
					polarity.addToIndexes();	
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
