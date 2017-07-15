package org.hucompute.textimager.uima.OpenerProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import static org.apache.uima.util.Level.INFO;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import ixa.kaflib.WF;


@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma"})
public class OpenerProjectPOSTagger  extends JCasAnnotator_ImplBase {
	// end::capabilities[]
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
	     * The character encoding used by the model.
	     */
	    public static final String PARAM_MODEL_ENCODING = ComponentParameters.PARAM_MODEL_ENCODING;
	    @ConfigurationParameter(name = PARAM_MODEL_ENCODING, mandatory = false)
	    private String modelEncoding;

		/**
		 * Load the part-of-speech tag to UIMA type mapping from this location instead of locating
		 * the mapping automatically.
		 */
		public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
		@ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
		protected String posMappingLocation;

		/**
		 * Use the {@link String#intern()} method on tags. This is usually a good idea to avoid
		 * spaming the heap with thousands of strings representing only a few different tags.
		 *
		 * Default: {@code true}
		 */
		public static final String PARAM_INTERN_TAGS = ComponentParameters.PARAM_INTERN_TAGS;
		@ConfigurationParameter(name = PARAM_INTERN_TAGS, mandatory = false, defaultValue = "true")
		private boolean internTags;

		/**
		 * Log the tag set(s) when a model is loaded.
		 *
		 * Default: {@code false}
		 */
		public static final String PARAM_PRINT_TAGSET = ComponentParameters.PARAM_PRINT_TAGSET;
		@ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue="false")
		protected boolean printTagSet;
		

//		private CasConfigurableProviderBase<POSTaggerME> modelProvider;
		private MappingProvider mappingProvider;
	    private Charset encoding;

//	    @Override
//		public void initialize(UimaContext aContext)
//			throws ResourceInitializationException
//		{
//			super.initialize(aContext);
//
//	        encoding = modelEncoding != null ? Charset.forName(modelEncoding) : null;
//			
//	// tag::model-provider-decl[]
//			// Use ModelProviderBase convenience constructor to set up a model provider that
//			// auto-detects most of its settings and is configured to use default variants.
//			// Auto-detection inspects the configuration parameter fields (@ConfigurationParameter)
//			// of the analysis engine class and looks for default parameters such as PARAM_LANGUAGE,
//			// PARAM_VARIANT, and PARAM_MODEL_LOCATION.
//	        modelProvider = new ModelProviderBase<POSTaggerME>(this, "tagger", "pos")
//	        {
//	            @Override
//	            protected POSTaggerME produceResource(InputStream aStream)
//	                throws Exception
//	            {
//	                // Load the POS tagger model from the location the model provider offers
//	                POSModel model = new POSModel(aStream);
//	                // Create a new POS tagger instance from the loaded model
//	                return new POSTaggerME(model);
//	            }
//	        };
//		}

	
	

	
	

	
	
	
	
	
	
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {		
	
			//CAS cas = aJCas.getCas();
			//modelProvider.configure(aJCas.getCas());
			//mappingProvider.configure(aJCas.getCas());
			
			String text = aJCas.getDocumentText();
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String KAF_LOCATION = "/tmp/tokenizer" + timestamp.getTime() + ".kaf";
		
		
			String version   = KAFDocument.class.getPackage().getImplementationVersion();
			KAFDocument kaf = new KAFDocument(aJCas.getDocumentLanguage(), "1.0");
			kaf.setRawText(text);
			
			// get token from JCas
			for(Token jCasToken : 
					org.apache.uima.fit.util.JCasUtil.select(aJCas,Token.class)) {
				kaf.newWF(text.substring(jCasToken.getBegin(), jCasToken.getEnd()), jCasToken.getBegin());
			}
			
			
			
			System.out.println(kaf.toString());
			kaf.save(KAF_LOCATION);	
			
		
			
			// command for the Process
			List<String> cmd = new ArrayList<String>();
			cmd.add("/bin/sh");
			cmd.add("-c");
			cmd.add("cat" + " \"" + KAF_LOCATION + "\"" + 
					" | jruby --2.0 -S pos-tagger");

			// Define ProcessBuilder
	        ProcessBuilder pb = new ProcessBuilder(cmd);
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
				       
		        // InputSteam to KAF 
		        KAFDocument inputkaf = KAFDocument.createFromStream(in);
		        
		        
		        System.out.println("POS");
		        System.out.println(inputkaf.toString());
		        
		        List<Term> termList = inputkaf.getTerms();
		        // Create Lemma
		        for(Term term : termList) {
		        	List<WF> kafToken = term.getWFs();
		        	int begin = kafToken.get(0).getOffset();
		        	int end = begin + kafToken.get(0).getLength();
		        	
		        	Lemma lemma = new Lemma(aJCas, begin, end);
		        	lemma.setValue(term.getLemma());
		        	lemma.addToIndexes();
		        	
		        	// ersetzen durch mapping
		        	POS pos = new POS(aJCas, begin, end);
		        	pos.setPosValue(term.getPos());
		        	pos.addToIndexes();
		        	
//		        	Morpheme morph = new Morpheme(aJCas, begin, end);
//		        	morph.setMorphTag(term.getMorphofeat());
//		        	morph.addToIndexes();

		        }
		        
		        
		        
		        
		        
		        //Mapping - error
		        /*
		        for(Term term : termList) {
		        	List<WF> kafToken = term.getWFs();
		        	int Begin = kafToken.get(0).getOffset();
		        	int End = Begin + kafToken.get(0).getLength();
		        	String tag = term.getPos();
		        	
		        	Type posTag = mappingProvider.getTagType(tag);
	                POS posAnno = (POS) cas.createAnnotation(posTag, Begin, End);
	                // To save memory, we typically intern() tag strings
	                posAnno.setPosValue(internTags ? tag.intern() : tag);
	                posAnno.setPosValue(posAnno.getClass().equals(POS.class) ? null
	                        : posAnno.getType().getShortName().intern());
	                posAnno.addToIndexes();

		        }
		        */
		        
	            
	             // Get Errors
	             String line = "";
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
	        
	        try {
				Files.delete(Paths.get(KAF_LOCATION));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
