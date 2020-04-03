package org.hucompute.services.uima.database.neo4j.impl;

import org.hucompute.services.uima.database.neo4j.data.Const;
import org.hucompute.services.uima.database.neo4j.data.Pos;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Created by abrami on 17.02.17.
 */
public class Pos_Neo4J_Impl extends Annotation_Neo4J_Impl implements Pos {

	public Pos_Neo4J_Impl(MDB_Neo4J_Impl pMDB, Node pNode) {
		super(pMDB, pNode);
	}

	public static Label getLabel() {
		return Label.label(Const.TYPE.POS.toString());
	}

	public static Pos_Neo4J_Impl getOrCreate(MDB_Neo4J_Impl pMDB, String pName){
		// check if avialable
		// if not
		Pos_Neo4J_Impl lResult = null;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			Node node = MDB_Neo4J_Impl.gdbs.findNode(getLabel(), "value", pName);
			
			if(node == null){
				lResult = new Pos_Neo4J_Impl(pMDB, MDB_Neo4J_Impl.gdbs.createNode(getLabel()));
				lResult.setProperty("value", pName);
			}else
				lResult = new Pos_Neo4J_Impl(pMDB, node);


			tx.success();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return lResult;
	}
}