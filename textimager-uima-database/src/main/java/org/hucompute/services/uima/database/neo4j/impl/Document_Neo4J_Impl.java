package org.hucompute.services.uima.database.neo4j.impl;

import org.hucompute.services.uima.database.neo4j.data.Annotation;
import org.hucompute.services.uima.database.neo4j.data.Const;
import org.hucompute.services.uima.database.neo4j.data.Document;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Created by abrami on 17.02.17.
 */
public class Document_Neo4J_Impl extends Annotation_Neo4J_Impl implements Document {

	public Document_Neo4J_Impl(MDB_Neo4J_Impl pMDB, Node pNode) {
		super(pMDB, pNode);
	}

	public static Label getLabel() {
		return Label.label(Const.TYPE.DOCUMENT.toString());
	}


	public static Document_Neo4J_Impl getOrCreate(MDB_Neo4J_Impl pMDB, String id){
		// check if avialable
		// if not
		Document_Neo4J_Impl lResult = null;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			Node node = MDB_Neo4J_Impl.gdbs.findNode(getLabel(), "id", id);
			if(node == null){
				lResult = new Document_Neo4J_Impl(pMDB, MDB_Neo4J_Impl.gdbs.createNode(getLabel()));
				lResult.setProperty("id", id);
			}else
				lResult = new Document_Neo4J_Impl(pMDB, node);
			tx.success();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return lResult;
	}

	public void addAnnotation(Annotation pAnnotation, Const.RelationType type) {
		this.createRelationshipTo(pAnnotation.getNode(), type);
	}
}
