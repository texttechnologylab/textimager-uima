package org.hucompute.textimager.uima.biofid.gazetteer.util;

import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XmiCasSerializerRunnable implements Runnable {
	
	JCas jCas;
	File outFile;
	
	public XmiCasSerializerRunnable(JCas pJcas, File pOutFile) {
		jCas = pJcas;
		outFile = pOutFile;
	}
	
	@Override
	public void run() {
		try (FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
			XmiCasSerializer.serialize(jCas.getCas(), fileOutputStream);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}
