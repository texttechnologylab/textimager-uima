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
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.resources.RuntimeProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import ixa.kaflib.KAFDocument;

public class OpenerProjectLanguageIdentifier extends SegmenterBase {
	
	

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
	
		
		String pathToJruby = "~/jruby/bin/";
		try {
			pathToJruby = new String(Files.readAllBytes(Paths.get("/src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/jruby_path")));
		} catch (IOException e1) {
			pathToJruby = "~/jruby/bin/";
		}		
		// command for the Process
		List<String> cmd = new ArrayList<String>();
		cmd.add("/bin/sh");
		cmd.add("-c");
		cmd.add("echo" + " \"" + text + "\"" + 
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
