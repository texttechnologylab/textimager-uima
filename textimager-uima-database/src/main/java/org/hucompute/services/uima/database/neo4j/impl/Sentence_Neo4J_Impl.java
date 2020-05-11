package org.hucompute.services.uima.database.neo4j.impl;

import org.hucompute.services.uima.database.neo4j.data.Const;
import org.hucompute.services.uima.database.neo4j.data.Sentence;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author Manuel Stoeckel
 * Created on 13.07.2017
 */
public class Sentence_Neo4J_Impl extends Annotation_Neo4J_Impl implements Sentence {

	public Sentence_Neo4J_Impl(MDB_Neo4J_Impl pMDB, Node pNode) {
		super(pMDB, pNode);
	}
	
	public static Label getLabel() {
		return Label.label(Const.TYPE.SENTENCE.toString());
	}

	public static synchronized Sentence_Neo4J_Impl create(MDB_Neo4J_Impl pMDB){
		// check if avialable
		// if not
		Sentence_Neo4J_Impl lResult = null;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			lResult = new Sentence_Neo4J_Impl(pMDB, MDB_Neo4J_Impl.gdbs.createNode(getLabel()));
			tx.success();
		}
		return lResult;
	}

}
