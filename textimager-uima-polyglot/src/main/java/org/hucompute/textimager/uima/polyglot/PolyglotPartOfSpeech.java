package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

/**
* PolyglotPartOfSpeech
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide POS for 16 languages. (http://polyglot.readthedocs.io/en/latest/POS.html) 
* UIMA-Token is needed as input to create POS.
* UIMA-Standard is used to represent the final POS.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"},
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"})
public class PolyglotPartOfSpeech  extends SegmenterBase {
	
	/**
     * Load the PythonPATH
     */
    public static final String PARAM_PYTHON_PATH = "PythonPathPolyglot";
    @ConfigurationParameter(name = PARAM_PYTHON_PATH, mandatory = false)
    protected String PythonPATH;
    
	/**
     * Use this language instead of the document language to resolve the model.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    /**
     * Override the default variant used to locate the model.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    /**
     * Load the model from this location instead of locating the model automatically.
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;

    /**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
    protected String posMappingLocation;
    
    public static final String PARAM_POLYGLOT_PATH = "PolyglotPath";
    @ConfigurationParameter(name = PARAM_POLYGLOT_PATH, mandatory = false)
    protected String POLYGLOT_LOCATION;
    
    /**
     * Log the tag set(s) when a model is loaded.
     *
     * Default: {@code false}
     */
    public static final String PARAM_PRINT_TAGSET = ComponentParameters.PARAM_PRINT_TAGSET;
    @ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue = "false")
    protected boolean printTagSet;

    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider posMappingProvider;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>()
        {
            {
                setContextObject(PolyglotPartOfSpeech.this);

                setDefault(ARTIFACT_ID, "${groupId}.Polyglot-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/polyglot/lib/"
                        + "tagger-${variant}.model");
                setDefault(VARIANT, "default");

                setOverride(LOCATION, modelLocation);
                setOverride(LANGUAGE, language);
                setOverride(VARIANT, variant);
            }

            @Override
            protected File produceResource(URL aUrl)
                throws IOException
            {
                return ResourceUtils.getUrlAsFile(aUrl, true);
            }
        };

        posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation, language, modelProvider);
    }
	
	/**
	 * Analyze the text and create tokens for every word. After successfully creation, add tokens to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}
		
		// Variables for mapping
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		posMappingProvider.configure(cas);
		
		// Create an ArrayList of all token, because POS-library doesn't output begin/end of POS. Calculate it manually.
		ArrayList<Token> T = new ArrayList<Token>();
		for (Token token : select(aJCas, Token.class)) {
			T.add(token);
		}
		
		int offsetToken = 0;
		
		for (Sentence sentence : select(aJCas, Sentence.class)) {	
			// Define ProcessBuilder
	        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "pos", sentence.getCoveredText());
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

				int currentOffset = 0;
				for(int i = 0; i < T.size(); i = i + 1) {
					if(i + offsetToken >= T.size()) {
						break;						
					}
					
					if(T.get(i + offsetToken).getEnd() > sentence.getEnd() || T.get(i + offsetToken).getBegin() > sentence.getEnd()) {
//						System.out.println("ABBRUCH");
						break;
					}
					
					if(T.get(i + offsetToken).getBegin() < sentence.getBegin()) {
//						System.out.println("WEITER");
						continue;
					}
					
					if(resultInParts[i * 2].equals(T.get(i + offsetToken).getCoveredText())) {
						// Create POS-Tag
						String tag = resultInParts[i * 2 + 1];
		    			Type posTag = posMappingProvider.getTagType(tag);
						POS posElement = (POS) cas.createAnnotation(posTag, T.get(i + offsetToken).getBegin(), T.get(i + offsetToken).getEnd());
						posElement.setPosValue(tag);
						posElement.addToIndexes();
						currentOffset = currentOffset + 1;
					}
				}		

				offsetToken = offsetToken + currentOffset;
				
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
