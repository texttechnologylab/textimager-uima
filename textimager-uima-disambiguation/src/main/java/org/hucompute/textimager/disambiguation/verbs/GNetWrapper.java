package org.hucompute.textimager.disambiguation.verbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import de.tuebingen.uni.sfs.germanet.api.GermaNet;

public final class GNetWrapper implements SharedResourceObject {
	public static final String PARAM_GERMANET_PATH = "germanetPath";
	@ConfigurationParameter(name=PARAM_GERMANET_PATH, mandatory= true)
	private String germanetPath;
	private GermaNet gnet;

	public void load(DataResource aData) throws ResourceInitializationException {
		
		ConfigurationParameterInitializer.initialize(this, aData);
		
		try {
			gnet = new GermaNet(new File(germanetPath));
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GermaNet getGnet() {
		return gnet;
	}

	public String getPath() {
		return germanetPath;
	}

}
