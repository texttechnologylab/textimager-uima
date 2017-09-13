package org.hucompute.textimager.uima.talismane;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import com.joliciel.talismane.AnnotatedText;
import com.joliciel.talismane.TalismaneException;
import com.joliciel.talismane.TalismaneSession;
import com.joliciel.talismane.rawText.RawText;
import com.joliciel.talismane.rawText.Sentence;
import com.joliciel.talismane.sentenceAnnotators.SentenceAnnotatorLoadException;
import com.joliciel.talismane.sentenceDetector.SentenceDetector;
import com.joliciel.talismane.tokeniser.Token;
import com.joliciel.talismane.tokeniser.TokenSequence;
import com.joliciel.talismane.tokeniser.Tokeniser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;


@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class TalismaneSegmenter extends SegmenterBase{
	
	/**
     * Path to Config File
     */
    public static final String PARAM_CONFIG_LOCATION = "PARAM_CONFIG_LOCATION";
    @ConfigurationParameter(name = PARAM_CONFIG_LOCATION, mandatory = false)
    protected String configLocation;

	@Override
	protected void process(JCas aJCas, String text, int arg2) throws AnalysisEngineProcessException {
		
		// load the Talismane configuration
	    Config conf = ConfigFactory.load(configLocation);
	    
	    //create session ID
  		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
  		String sessionId = timestamp.toString();
	    TalismaneSession session = null;
		try {
			session = new TalismaneSession(conf, sessionId);
			} 
		catch (ClassNotFoundException e) {e.printStackTrace();} 
		catch (SentenceAnnotatorLoadException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();} 
		catch (TalismaneException e) {e.printStackTrace();}

	    RawText rawText = new RawText(text, true, session);

	    // retrieve the processed text after filters have been applied
	    AnnotatedText processedText = rawText.getProcessedText();

	    // detect sentences
	    SentenceDetector sentenceDetector = null;
		try {
			sentenceDetector = SentenceDetector.getInstance(session);
			} 
		catch (ClassNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}
		
	    try {
			sentenceDetector.detectSentences(processedText);
			} 
	    catch (TalismaneException e) {e.printStackTrace();}

	    //Get Sentences
	    List<Sentence> sentences = rawText.getDetectedSentences();

	    //Add sentences to JCas
	    for (Sentence sentence : sentences) {
	    	de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sen = 
	    			new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(aJCas);
	    	
	    	int sStart = sentence.getOriginalIndex(0);
	    	int sEnd = sStart + sentence.getText().length();
	    	
	    	//ignore empty sentences
	    	if(sStart != sEnd) {
	    		sen.setBegin(sStart);
	    		sen.setEnd(sEnd);
	    		sen.addToIndexes();
	    	
	    	
		    	 Tokeniser tokeniser = null;
		 		try {
		 			tokeniser = Tokeniser.getInstance(session);
		 			} 
		 		catch (ClassNotFoundException e) {e.printStackTrace();} 
		 		catch (IOException e) {e.printStackTrace();}
		    	
		    	TokenSequence tokenSequence = null;
				try {
					tokenSequence = tokeniser.tokeniseSentence(sentence);
					} 
				catch (TalismaneException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
				
				// write token to JCas
			    for (Token token : tokenSequence) {
			    	int tStart = token.getStartIndex() + sStart;
			    	int tEnd = token.getEndIndex() + sStart;    	
			    	createToken(aJCas, tStart, tEnd);
				}
	    	}
	    }
		
	}

}
