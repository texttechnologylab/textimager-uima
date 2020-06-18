package org.hucompute.textimager.uima.io.embeddings.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;



public abstract class BaseEmbeddingsWriter extends JCasFileWriter_ImplBase{

	public static final String PARAM_NORMALIZE_POS = "NORMALIZE_POS";
	@ConfigurationParameter(name=PARAM_NORMALIZE_POS, mandatory=true, defaultValue="true")
	public boolean normalizePos;
	
	public static final String PARAM_MODUS = "exportModus";
	@ConfigurationParameter(name=PARAM_MODUS, mandatory=true)
	public Modus exportmodus;
	
	public enum Modus{
		TOKEN,
		LEMMA,
		LEMMA_POS,
		LEMMA_POS_DISAMBIG
	}
	
	public BufferedWriter writer;
	
	private HashMap<String, String>posMap = new HashMap<>();
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
//			new File(getTargetLocation()).getParentFile().mkdirs();
			writer = new BufferedWriter(new FileWriter(getTargetLocation()));
			String text = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("am_posmap.txt"), Charset.defaultCharset());
			for (String string : text.split("\n")) {
				posMap.put(string.split("\t")[0], string.split("\t")[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String normalizePos(String pos){
		return posMap.get(pos);
	}
	
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
