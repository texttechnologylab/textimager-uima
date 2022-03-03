package org.hucompute.textimager.uima.OpenerProject;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.Lemma",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme"},
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity"
				})
public class OpenerProjectNER extends JCasAnnotator_ImplBase {
    /**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_JRUBY_LOCATION = "PARAM_JRUBY_LOCATION";
    @ConfigurationParameter(name = PARAM_JRUBY_LOCATION, mandatory = false)
    protected String jRubyLocation;

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
    public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION = ComponentParameters.PARAM_NAMED_ENTITY_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION, mandatory = false)
    protected String namedEntityMappingLocation;

    /**
     * Use the {@link String#intern()} method on tags. This is usually a good idea to avoid spaming
     * the heap with thousands of strings representing only a few different tags.
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
    @ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue = "false")
    protected boolean printTagSet;

    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider mappingProvider;




    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>()
        {
            {
                setContextObject(OpenerProjectNER.this);

                setDefault(ARTIFACT_ID, "${groupId}.OpenerProject-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/OpenerProject/lib/"
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

        mappingProvider = new MappingProvider();
        mappingProvider
                .setDefaultVariantsLocation("classpath:/org/hucompute/textimager/uima/OpenerProject/lib/ner-default.map");
        mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/hucompute/textimager/uima/"
                + "OpenerProject/lib/ner-default.map");
        mappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());

        mappingProvider.setOverride(MappingProvider.LOCATION, namedEntityMappingLocation);
        mappingProvider.setOverride(MappingProvider.LANGUAGE, language);
        mappingProvider.setOverride(MappingProvider.VARIANT, variant);
//        mappingProvider).addTagMappingImport("ner", modelProvider);


    }

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		// Needed for Mapping
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		mappingProvider.configure(cas);

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
				"| "+pathToJruby+"jruby -S ner");
		//| jruby --2.0 -S ned

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


	        List<Entity> entityList = inputkaf.getEntities();

	        for(Entity entity : entityList) {



	        	List<Term> kafTerms = entity.getTerms();
	        	int t = kafTerms.size();
	        	int w = kafTerms.get(t-1).getWFs().size();

	        	int begin = kafTerms.get(0).getWFs().get(0).getOffset();
	        	int end = kafTerms.get(t-1).getWFs().get(w-1).getOffset()
	        			+ kafTerms.get(t-1).getWFs().get(w-1).getLength();

	        	String tag = entity.getType();
	        	Type nameTag = mappingProvider.getTagType(tag);
	        	NamedEntity nameAnno = (NamedEntity) cas.createAnnotation(nameTag, begin, end);
	        	nameAnno.setValue(tag);
	        	nameAnno.addToIndexes();

	        }



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
