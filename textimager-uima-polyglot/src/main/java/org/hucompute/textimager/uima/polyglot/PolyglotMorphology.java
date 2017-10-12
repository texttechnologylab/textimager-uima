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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import morphemeAnnotation.type.MorphemeAnnotation;

/**
* PolyglotMorphology
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide morphemes for 135 languages. (http://polyglot.readthedocs.io/en/latest/MorphologicalAnalysis.html) 
* UIMA-Token is needed as input to create POS.
* UIMA-Standard is used to represent the final MorphologyAnnotation.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"},
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity"})
public class PolyglotMorphology  extends SegmenterBase {
	
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
	 * Analyze the text and create NE-Tag for every word. After successfully creation, add NE to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		
		for (Token token : select(aJCas, Token.class)) {		
			// Define ProcessBuilder
	        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "morphology", token.getCoveredText(), aJCas.getDocumentLanguage());
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
				String value = "";
				
				// Build value-String
				for(int i = 0; i < resultInParts.length; i = i + 1) {
					value = value + "[" + resultInParts[i] + "]";
				}
				
				// Create MorphemeAnnotation		
				MorphologicalFeatures morpheme = new MorphologicalFeatures(aJCas, token.getBegin(), token.getEnd());
				morpheme.setValue(value);
				morpheme.addToIndexes();	
				
				
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
