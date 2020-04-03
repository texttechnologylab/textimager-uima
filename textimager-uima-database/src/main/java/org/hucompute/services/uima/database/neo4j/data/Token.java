package org.hucompute.services.uima.database.neo4j.data;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Created by abrami on 17.02.17.
 */
public interface Token {

    Pos getPos();
    Lemma getLemma();

    void setPos(Pos pPos);
    void setLemma(Lemma pLemma);

    void setProperty(String pProperty, Object pObject);
    Object getProperty(String pProperty);

    Relationship createRelationshipTo(Node pNode, Const.RelationType pType);

}
