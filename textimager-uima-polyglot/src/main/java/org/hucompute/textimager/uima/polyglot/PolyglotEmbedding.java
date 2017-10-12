package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import embeddingAnnotation.type.EmbeddingAnnotation;

/**
* PolyglotEmbedding
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide Embedding for 137 languages. (http://polyglot.readthedocs.io/en/latest/Embeddings.html) 
* UIMA-Token are needed as input to create Embedding.
* UIMA-Standard is used to represent the final Embedding.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"sentimentAnnotation.type.EmbeddingAnnotation"})
public class PolyglotEmbedding  extends SegmenterBase {
	
	/**
     * Load the PythonPATH
     */
    public static final String PARAM_PYTHON_PATH = "PythonPathPolyglot";
    @ConfigurationParameter(name = PARAM_PYTHON_PATH, mandatory = false)
    protected String PythonPATH;
    
    public static final String PARAM_POLYGLOT_PATH = "PolyglotPath";
    @ConfigurationParameter(name = PARAM_POLYGLOT_PATH, mandatory = false)
    protected String POLYGLOT_LOCATION;
    
    public static final String PARAM_EMBEDDING_PATH = "EmbeddingPath";
    @ConfigurationParameter(name = PARAM_EMBEDDING_PATH, mandatory = false)
    protected String EMBEDDING_LOCATION;
    
	/**
	 * Analyze the text and create Sentiment-Tag for every word. After successfully creation, add Polarity to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		
		if(EMBEDDING_LOCATION == null) {
			EMBEDDING_LOCATION = "/home/alex/polyglot_data/embeddings2/" + aJCas.getDocumentLanguage() + "/embeddings_pkl.tar.bz2";
		} else {
			EMBEDDING_LOCATION = EMBEDDING_LOCATION + aJCas.getDocumentLanguage() + "/embeddings_pkl.tar.bz2"; 
		}
		
		for (Token token : select(aJCas, Token.class)) {				
			// Define ProcessBuilder
		    ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "embedding", token.getCoveredText(), EMBEDDING_LOCATION);
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
								
				// Only process sentence if Embedding-TAG is found.
				if(result.length() != 0 && resultInParts.length > 1) {
					for(int i = 0; i < resultInParts.length; i = i + 2) {
						// Create EmbeddingAnnotation		
						EmbeddingAnnotation embeddingText = new EmbeddingAnnotation(aJCas, token.getBegin(), token.getEnd());
						embeddingText.setValue(resultInParts[i]);
						embeddingText.setDistance(resultInParts[i + 1]);
						embeddingText.addToIndexes();	
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
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
			
	}
}
