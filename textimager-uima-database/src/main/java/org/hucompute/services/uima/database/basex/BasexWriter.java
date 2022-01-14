package org.hucompute.services.uima.database.basex;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.uima.database.AbstractWriter;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BasexWriter extends AbstractWriter {

/*
    - BASEX_HOST=basex
    - BASEX_PORT=1984
    - BASEX_USER=admin
    - BASEX_PASS=admin
    - BASEX_DBNAME=uimadatabase
    */

	final String dbName = "uimadatabase";
	final String host = "127.0.0.1";
	final int port = 1984;
	final String user = "admin";
	final String pass = "admin";

	BaseXClient session = null;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		try {
			session = new BaseXClient(host, port, user, pass);

			// Create DB if not exists
			session.execute("CHECK " + dbName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		resumeWatch();
		String path = "cas/" + DocumentMetaData.get(jCas).getDocumentId();
		ByteArrayOutputStream docOS = new ByteArrayOutputStream();
		try {
			XmiCasSerializer.serialize(jCas.getCas(), docOS);
		} catch (SAXException e1) {
			e1.printStackTrace();
		}
		try {
			session.replace(path, new ByteArrayInputStream(docOS.toByteArray()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		suspendWatch();
		log();
	}
}
