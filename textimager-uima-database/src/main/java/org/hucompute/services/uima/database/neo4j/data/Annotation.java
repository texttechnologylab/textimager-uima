package org.hucompute.services.uima.database.neo4j.data;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public interface Annotation{
	Node getNode();
	MDB getDB();
	void setProperty(String pProperty, Object pObject);
	Object getProperty(String pProperty);

    Relationship createRelationshipTo(Node pNode, Const.RelationType pType);
    Iterable<Relationship> getRelationship(Const.RelationType... pType);

}
