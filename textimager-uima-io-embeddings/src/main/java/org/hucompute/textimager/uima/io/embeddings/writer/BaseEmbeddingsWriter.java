package org.hucompute.textimager.uima.io.embeddings.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;

public abstract class BaseEmbeddingsWriter extends JCasFileWriter_ImplBase{
	
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
			new File(getTargetLocation()).getParentFile().mkdirs();
			writer = new BufferedWriter(new FileWriter(getTargetLocation()));
			String text = Resources.toString(Resources.getResource("am_posmap.txt"), Charsets.UTF_8);
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
