package org.hucompute.textimager.uima.OpenerProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.resources.RuntimeProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import ixa.kaflib.KAFDocument;

public class OpenerProjectLanguageIdentifier extends SegmenterBase {
	
    /**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_JRUBY_LOCATION = "PARAM_JRUBY_LOCATION";
    @ConfigurationParameter(name = PARAM_JRUBY_LOCATION, mandatory = false)
    protected String jRubyLocation;

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
	
		
		String pathToJruby = "~/jruby/bin/";
		if(jRubyLocation != null) pathToJruby=jRubyLocation;
		
		// command for the Process
		List<String> cmd = new ArrayList<String>();
		cmd.add("/bin/sh");
		cmd.add("-c");
		cmd.add("export PATH=/usr/bin:$PATH && echo" + " \"" + text + "\"" + 
				" | "+pathToJruby+"jruby -S language-identifier");

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
	        KAFDocument kaf = KAFDocument.createFromStream(in);
	        
	        // Get Document Language
            String lang = kaf.getLang();
           
            // Set Document Language
            if(lang != "")
        	   aJCas.setDocumentLanguage(kaf.getLang());
           
            
            
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
			
			
			
			
			
	}

}
