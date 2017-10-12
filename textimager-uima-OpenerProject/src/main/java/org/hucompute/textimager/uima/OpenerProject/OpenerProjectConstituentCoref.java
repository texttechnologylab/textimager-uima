package org.hucompute.textimager.uima.OpenerProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent_Type;
import ixa.kaflib.Coref;
import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Span;
import ixa.kaflib.Target;
import ixa.kaflib.Term;
import ixa.kaflib.Terminal;
import ixa.kaflib.Tree;
import ixa.kaflib.TreeNode;
import ixa.kaflib.WF;


@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.Lemma",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme"},
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain",
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink",
				"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent"
				})
public class OpenerProjectConstituentCoref extends JCasAnnotator_ImplBase {
	
    /**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_JRUBY_LOCATION = "PARAM_JRUBY_LOCATION";
    @ConfigurationParameter(name = PARAM_JRUBY_LOCATION, mandatory = false)
    protected String jRubyLocation;
	

    /**
     * Use this language instead of the document language to resolve the model and tag set mapping.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    /**
     * Variant of a model the model. Used to address a specific model if here are multiple models
     * for one language.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    /**
     * Location from which the model is read.
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;
    /**
     * Location of the mapping file for constituent tags to UIMA types.
     */
    public static final String PARAM_CONSTITUENT_MAPPING_LOCATION = ComponentParameters.PARAM_CONSTITUENT_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_CONSTITUENT_MAPPING_LOCATION, mandatory = false)
    protected String constituentMappingLocation;
    
    
    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider constituentMappingProvider;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>()
        {
            {
                setContextObject(OpenerProjectConstituentCoref.this);

                setDefault(ARTIFACT_ID, "${groupId}.OpenerProject-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/OpenerProject/lib/"
                        + "constituent-${variant}.model");
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

        if(constituentMappingLocation == null) 
        	constituentMappingLocation="classpath:/org/hucompute/textimager/uima/OpenerProject/lib/constituent-default.map";
        constituentMappingProvider = MappingProviderFactory.createConstituentMappingProvider(constituentMappingLocation,
                language, modelProvider);
    }

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Needed for Mapping
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		constituentMappingProvider.configure(cas);
		
		//Generate KAF File
		JCastoKaf jkaf = new JCastoKaf(aJCas);
		jkaf.add_POS_Lemma();
		KAFDocument kaf = jkaf.getKaf();
		String KAF_LOCATION = jkaf.KAF_LOCATION;
		kaf.save(KAF_LOCATION);
		
		String pathToJruby = "~/jruby/bin/";
		if(jRubyLocation != null) pathToJruby=jRubyLocation;
		
		// command for the Process
		List<String> cmd = new ArrayList<String>();
		cmd.add("/bin/sh");
		cmd.add("-c");
		cmd.add("export PATH=/usr/bin:$PATH && cat" + " \"" + KAF_LOCATION + "\"" + 
				"| "+pathToJruby+"jruby -S constituent-parser | "+pathToJruby+"jruby -S coreference");

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
	        
	     // Get Errors
            String line = "";
            String errorString = "";
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
			       
	        // Cut Debug Message
			 if(aJCas.getDocumentLanguage() == "de") {
				String inLine = "";
	            String inString = "";
				 try {
					while ((inLine = in.readLine()) != null) {
						inString += inLine+"\n";
					}
				 } catch (IOException e) {
					e.printStackTrace();
				 }
				 inString = inString.split("</KAF>")[0] + "</KAF>";
				 InputStream inStream = new ByteArrayInputStream(inString.getBytes());
				 in = new BufferedReader(new InputStreamReader( inStream));
				 
			 }
			 
			// InputSteam to KAF 
	        KAFDocument inputkaf = KAFDocument.createFromStream(in);        

	        List<Tree> TreeList = inputkaf.getConstituents();
	        
	        
	        for(Tree tree : TreeList) {
	        	contituentToCas(aJCas, tree.getRoot());

	        }
	        List<Coref> corefList = inputkaf.getCorefs();
	        
	        for(Coref coref : corefList) {
	        	List<CoreferenceLink> jCasCorefList = new ArrayList<CoreferenceLink>();
	        	for(List<Target> ref :coref.getReferences()) {
	        		Target firsttarget = ref.get(0);
	        		Target lasttarget = ref.get(ref.size()-1);
	        		int size = lasttarget.getTerm().getWFs().size();
	        		
		    		int begin = firsttarget.getTerm().getWFs().get(0).getOffset();
		    		int end = lasttarget.getTerm().getWFs().get(size-1).getOffset() +
		    				lasttarget.getTerm().getWFs().get(size-1).getLength();

	    			CoreferenceLink co = new CoreferenceLink(aJCas, begin, end);
	    			jCasCorefList.add(co);
	    			co.addToIndexes();
	    			if(jCasCorefList.size() > 2)
	    				jCasCorefList.get(jCasCorefList.size()-2).setNext(co);
	        		
	        	}
        		CoreferenceChain jCasCoreference = new CoreferenceChain(aJCas);
        		jCasCoreference.setFirst(jCasCorefList.get(0));
        		jCasCoreference.addToIndexes();
	        	
	        }

			 
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
			e.printStackTrace();
		}

		
	}

	private Annotation contituentToCas(JCas aJCas, TreeNode treeNode) {
	
		//Leaf Nodes
		if(treeNode.isTerminal()) {
			//get the Term
			Terminal termNode = (Terminal) treeNode;
			Span<Term> term = termNode.getSpan();
			//Get Begin and End from the word
			int begin = term.getFirstTarget().getWFs().get(0).getOffset();
			int end = begin +term.getFirstTarget().getWFs().get(0).getLength(); 
			//Return the Token
			return JCasUtil.selectSingleAt(aJCas, Token.class, begin, end);
			
		}
		// Other Nodes
		else {
			CAS cas = aJCas.getCas();
			//Get the Tag
			NonTerminal nterm = (NonTerminal) treeNode;
			String tag = nterm.getLabel();

			//Map the Tag
			Type posTag = constituentMappingProvider.getTagType(tag);
			Constituent con = (Constituent) cas.createAnnotation(posTag, 0, 0);
			con.setConstituentType(tag);			
			
			//Get all Children Nodes and add the to the List
			List<Annotation> annoList = new ArrayList<Annotation>();
			for(TreeNode childNodes : treeNode.getChildren()) {
				Annotation a = contituentToCas(aJCas, childNodes);
				annoList.add(a);
			}
			//Convert The List to FSArray
			Annotation[] array =  annoList.toArray(new Annotation[] {});
			int size = annoList.size();
			FSArray fa = new FSArray(aJCas, size);
			fa.copyFromArray(array, 0, 0, size);
			
			//Add Childeren, Begin and End to the Annotation
			con.setChildren(fa);
			con.setBegin(con.getChildren(0).getBegin());
			con.setEnd(con.getChildren(size-1).getEnd());
			con.addToIndexes();

			return con;
		}
		
		
		
	}

}
