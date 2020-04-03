package org.hucompute.services.uima.database.cassandra;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

import com.datastax.driver.core.*;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.hucompute.services.uima.database.AbstractCollectionReader;


public class CassandraCollectionReader extends AbstractCollectionReader {

	private Cluster cluster;
	private Session session;
	private Set<String> tables;

	public static final String PARAM_DB_CONNECTION = "cassandra_connection";
	@ConfigurationParameter(name = PARAM_DB_CONNECTION, //
			description = "host, keyspace, user, pw", mandatory = false)
	protected String[] db_connection;

	public static final String PARAM_QUERY = "cassandra query";
	@ConfigurationParameter(name = PARAM_QUERY, mandatory = false, //
			description = "some cassandra query SELECT  COLUMNNAME FROM TABLE WHERE CONDITION;")
	private String query = null;

	ResultSet rs = null;


	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		//initialize jdbc connection


		cluster = null;

		try {
			//			String id = "0_";
			cluster = Cluster.builder()
					.addContactPoint("127.0.0.1")
					.withPort(
							9042
					)
					.withCredentials(
							"cassandra",
							"cassandra"
					)
					.build();
			session = cluster.connect("textimager");
			session.execute("use textimager;");
			tables = new HashSet<>(Arrays.asList("WikiDataHyponym", "pos", "Lemma", "tokens", "Wikify", "morph", "Sentence", "TagsetDescription", "Paragraph"));
			System.out.println("Grabing xmis...");
			rs = session.execute("SELECT * FROM xmi;");
			System.out.println("query complete.");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//bearbeiten
	public boolean hasNext() throws IOException, CollectionException {
		if(!rs.isExhausted())
		{
			return true;
		}
		else
		{	
			session.close();
			cluster.close();
			return false;
		}
	}

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		resumeWatch();

		Row row = rs.one();

		reecreateMetaData(aCAS, row);

		String xmi = row.getString("xmi");;
		StringBuilder sb = new StringBuilder();
		for(String s : tables){
			sb.setLength(0);
			sb.append("SELECT * FROM ");
			sb.append(s);
			sb.append(" WHERE xmi ='");
			sb.append(xmi);
			sb.append("' ;");

			//  reihen bzgl der xmi, werden nun geparsed
			ResultSet xmiRs = session.execute(sb.toString());
			recreateFromRows(xmiRs, s, aCAS);
		}

		//		//readCAS()
		suspendWatch();
		log();
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	private void recreateFromRows(ResultSet resultSet, String table, CAS aCAS){
		while (!resultSet.isExhausted()) {
			Row row = resultSet.one();

			String begin = row.getString("start");
			String end = row.getString("end");
			//prelim work

			switch (table){
//			case "WikiDataHyponym":
//				try {
//					WikiDataHyponym wdh = new WikiDataHyponym(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
//					wdh.setDepth(Integer.valueOf(row.getString("depth")));
//					wdh.addToIndexes(aCAS.getJCas());
//				} catch (CASException e) {
//					e.printStackTrace();
//				}
//
//				break;
			case "pos":
				try {
					POS pos = new POS(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
					pos.setPosValue(row.getString("value"));
					pos.addToIndexes(aCAS.getJCas());
				} catch (CASException e) {
					e.printStackTrace();
				}
				break;
			case "Lemma":
				try {
					Lemma lemma = new Lemma(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
					lemma.setValue(row.getString("value"));
					lemma.addToIndexes(aCAS.getJCas());
				} catch (CASException e) {
					e.printStackTrace();
				}
				break;
			case "tokens":
				try {
					Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
					token.addToIndexes(aCAS.getJCas());
				} catch (CASException e) {
					e.printStackTrace();
				}
				break;
//			case "Wikify":
//				try {
//					Wikify wikify = new Wikify(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
//					wikify.setLink(row.getString("link"));
//					wikify.setTitle(row.getString("title"));
//					wikify.addToIndexes(aCAS.getJCas());
//				} catch (CASException e) {
//					e.printStackTrace();
//				}
			case "Sentence":
				try {
					Sentence sentence = new Sentence(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
					sentence.addToIndexes(aCAS.getJCas());
				} catch (CASException e) {
					e.printStackTrace();
				}
				break;
			case "Paragraph":
				try {
					Paragraph paragraph = new Paragraph(aCAS.getJCas(),Integer.valueOf(begin), Integer.valueOf(end));
					paragraph.addToIndexes(aCAS.getJCas());
				} catch (CASException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

		}
	}

	private void reecreateMetaData(CAS aCAS, Row row) {
		try {
			DocumentMetaData meta = new DocumentMetaData(aCAS.getJCas(), Integer.valueOf(row.getString("start")), Integer.valueOf(row.getString("end")));
			meta.setCollectionId(row.getString("collectionId"));
			meta.setDocumentBaseUri(row.getString("baseUri"));
			meta.setDocumentId(row.getString("xmi"));
			meta.setDocumentTitle(row.getString("title"));
			meta.setDocumentUri(row.getString("fileUri"));
			meta.addToIndexes(aCAS.getJCas());
		} catch (CASException e) {
			e.printStackTrace();
		}
	}
	
	
}
