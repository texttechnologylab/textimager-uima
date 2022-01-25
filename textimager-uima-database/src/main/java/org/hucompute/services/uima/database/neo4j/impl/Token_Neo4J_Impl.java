package org.hucompute.services.uima.database.neo4j.impl;

import org.hucompute.services.uima.database.neo4j.data.Const;
import org.hucompute.services.uima.database.neo4j.data.Lemma;
import org.hucompute.services.uima.database.neo4j.data.Pos;
import org.hucompute.services.uima.database.neo4j.data.Token;
import org.neo4j.graphdb.*;

/**
 * Created by abrami on 17.02.17.
 */
public class Token_Neo4J_Impl extends Annotation_Neo4J_Impl implements Token {

	public Token_Neo4J_Impl(MDB_Neo4J_Impl pMDB, Node pNode) {
		super(pMDB, pNode);
	}
	public static String TYPE = "type";

	public static Label getLabel() {
		return Label.label(Const.TYPE.TOKEN.toString());
	}

	public static synchronized Token_Neo4J_Impl create(MDB_Neo4J_Impl pMDB){
		// check if avialable
		// if not
		Token_Neo4J_Impl lResult = null;
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			lResult = new Token_Neo4J_Impl(pMDB, MDB_Neo4J_Impl.gdbs.createNode(getLabel()));
			tx.success();
		}
		return lResult;
	}

	@Override
	public Pos getPos() {
		Relationship r = getNode().getSingleRelationship(Const.RelationType.pos, Direction.OUTGOING);
		return new Pos_Neo4J_Impl((MDB_Neo4J_Impl)getDB(), r.getEndNode());
	}

	@Override
	public Lemma getLemma() {
		Relationship r = getNode().getSingleRelationship(Const.RelationType.lemma, Direction.OUTGOING);
		return new Lemma_Neo4J_Impl((MDB_Neo4J_Impl)getDB(), r.getEndNode());
	}

	@Override
	public void setPos(Pos pPos) {
		this.createRelationshipTo(pPos.getNode(), Const.RelationType.pos);
	}

	@Override
	public void setLemma(Lemma pLemma) {
		this.createRelationshipTo(pLemma.getNode(), Const.RelationType.lemma);
	}
}
