package org.hucompute.services.uima.database.neo4j;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.hucompute.services.uima.database.AbstractCollectionReader;
import org.hucompute.services.uima.database.neo4j.data.Const.RelationType;
import org.hucompute.services.uima.database.neo4j.impl.Document_Neo4J_Impl;
import org.hucompute.services.uima.database.neo4j.impl.MDB_Neo4J_Impl;
import org.hucompute.services.uima.database.neo4j.impl.Token_Neo4J_Impl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.io.IOException;
import java.util.Iterator;


public class Neo4jCollectionReader extends AbstractCollectionReader {
	MDB_Neo4J_Impl pMDB;
	Iterator<Node> documents;
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		//initialize jdbc connection
		//get curser of data.
		pMDB = new MDB_Neo4J_Impl("src/main/resources/neo4j/conf.conf");
		documents = pMDB.getNodes(Document_Neo4J_Impl.getLabel()).iterator();
	}

	//bearbeiten
	public boolean hasNext() throws IOException, CollectionException {
		return documents.hasNext();
	}

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		resumeWatch();
		Document_Neo4J_Impl doc = new Document_Neo4J_Impl(pMDB, documents.next());
		try {
			DocumentMetaData meta = DocumentMetaData.create(aCAS);
			meta.setDocumentId(doc.getProperty("id").toString());
			aCAS.setDocumentLanguage(doc.getProperty("language").toString());
			aCAS.setDocumentText(doc.getProperty("text").toString());

			try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
				Iterator<Relationship>tokens = doc.getRelationship(RelationType.token).iterator();
				while (tokens.hasNext()) {
					Relationship type = (Relationship) tokens.next();
					Token_Neo4J_Impl token = new Token_Neo4J_Impl(pMDB, type.getEndNode());

					Token xmiToken = new Token(aCAS.getJCas(),(Integer)token.getProperty("begin"),(Integer)token.getProperty("end"));

					Iterator<Relationship>posLemma =  token.getRelationship(RelationType.lemma,RelationType.pos).iterator();
					while (posLemma.hasNext()) {
						Relationship type2 = posLemma.next();
						if(type2.isType(RelationType.pos)){
							POS pos = new POS(aCAS.getJCas(), xmiToken.getBegin(), xmiToken.getEnd());
							pos.setPosValue(type2.getEndNode().getProperty("value").toString());
							pos.addToIndexes();
							xmiToken.setPos(pos);
						}else
						{
							Lemma lemma = new Lemma(aCAS.getJCas(), xmiToken.getBegin(), xmiToken.getEnd());
							lemma.setValue(type2.getEndNode().getProperty("value").toString());
							lemma.addToIndexes();
							xmiToken.setLemma(lemma);
						}
					}
					xmiToken.addToIndexes();
				}
				tx.success();
			}

		} catch (CASException | IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//readCAS()
		suspendWatch();
		log();
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}
}
