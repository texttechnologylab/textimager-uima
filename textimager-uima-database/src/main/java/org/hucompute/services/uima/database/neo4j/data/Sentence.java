package org.hucompute.services.uima.database.neo4j.data;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author Manuel Stoeckel
 * Created on 13.07.2017
 */
public interface Sentence {
	Node getNode();
	
	Object getProperty(String pProperty);
    void setProperty(String pProperty, Object pObject);

    Relationship createRelationshipTo(Node pNode, Const.RelationType pType);
}
