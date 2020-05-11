package org.hucompute.services.uima.database.neo4j.data;

import org.neo4j.graphdb.Node;

/**
 * Created by abrami on 17.02.17.
 */
public interface Pos {

    Node getNode();
    void setProperty(String pProperty, Object pObject);
    Object getProperty(String pProperty);
}
