package org.hucompute.textimager.uima.OpenerProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.WF;

@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"})
public class OpenerProjectTokenizer  extends SegmenterBase {
	
	

	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {		
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String KAF_LOCATION = "/tmp/kaf" + timestamp.getTime() + ".kaf";

			String version   = KAFDocument.class.getPackage().getImplementationVersion();
			KAFDocument kaf = new KAFDocument(aJCas.getDocumentLanguage(), "1.0");
			
//			while(text.contains("\n\n")) {
//				text = text.replaceAll("\n\n", "\n");
//			}
			kaf.setRawText(text);
			kaf.save(KAF_LOCATION);	
			
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
			cmd.add("cat" + " \"" + KAF_LOCATION + "\"" + 
					" | "+pathToJruby+"jruby -S tokenizer");

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
		        
		        List<WF> wfList = inputkaf.getWFs();
		        List<List<WF>> sentList = inputkaf.getSentences();
		        
		        for(List<WF> kafSentence:sentList){
		        	int Begin = kafSentence.get(0).getOffset();
		        	int End = kafSentence.get(kafSentence.size()-1).getOffset() + kafSentence.get(kafSentence.size()-1).getLength();
		        	Sentence sent = new Sentence(aJCas, Begin, End);
		        	sent.addToIndexes();
		        }
		        // Create Token
		        for(WF kafToken:wfList){
		        	int Begin = kafToken.getOffset();
		        	int End = Begin + kafToken.getLength();
		        	
		        	Token token = new Token(aJCas);
		        	token.setBegin(Begin);
		        	token.setEnd(End);
		        	token.addToIndexes();
		        	
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
