package org.hucompute.services.uima.database.neo4j.impl;

import org.hucompute.services.uima.database.neo4j.data.Annotation;
import org.hucompute.services.uima.database.neo4j.data.Const;
import org.hucompute.services.uima.database.neo4j.data.MDB;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Created by abrami on 17.02.17.
 */
public class Annotation_Neo4J_Impl implements Annotation {

	private Node n = null;
	private MDB pMDB = null;

	public static String TYPE = "type";


	public Annotation_Neo4J_Impl(MDB_Neo4J_Impl pMDB, Node pNode){
		this.n=pNode;
		this.pMDB = pMDB;
	}

	@Override
	public Node getNode() {
		return this.n;
	}

	@Override
	public MDB getDB() {
		return this.pMDB;
	}
	
	@Override
	public void setProperty(String pProperty, Object pObject) {
//		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			this.n.setProperty(pProperty, pObject);
//			tx.success();
//		}
	}

	@Override
	public Object getProperty(String pProperty) {
		Object rObject = null;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {

			if(this.n.hasProperty(pProperty)){
				rObject = this.n.getProperty(pProperty);
			}
			tx.success();
		}
		return rObject;
	}
	
	@Override
	public Relationship createRelationshipTo(Node pNode, Const.RelationType pType) {
		Relationship ret ;
//		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			ret =  getNode().createRelationshipTo(pNode, pType);
//			tx.success();
//		}
		return ret;
	}
	
	@Override
	public Iterable<Relationship> getRelationship(Const.RelationType ... pType) {
		Iterable<Relationship> ret ;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			ret =  getNode().getRelationships(pType);
			tx.success();
		}
		return ret;
	}
}
