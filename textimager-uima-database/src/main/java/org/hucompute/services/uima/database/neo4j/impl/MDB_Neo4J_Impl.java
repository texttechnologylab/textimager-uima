package org.hucompute.services.uima.database.neo4j.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.hucompute.services.uima.database.neo4j.data.MDB;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.ErrorState;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexCreator;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

/**
 * Created by abrami on 17.02.17.
 */
public class MDB_Neo4J_Impl implements MDB, TransactionEventHandler<Object>, KernelEventHandler {

    public static GraphDatabaseService gdbs = null;

    private static String confFile = "";
    
    private static Map<String, String> conf = new HashMap<>();
    private static File db_dir;

    public MDB_Neo4J_Impl(String sConf){
        confFile = sConf;
        
        Properties lProperties = new Properties();
        try {
            lProperties.load(new FileInputStream(new File(confFile)));

        } catch (IOException e) {

        }
        for (String lString : lProperties.stringPropertyNames()) {
            conf.put(lString, (String) lProperties.get(lString));
        }
        db_dir = new File(conf.get("db_dir"));
        
        // Check if Graphdatabase is intialized and if not- do it
        //if (gdbs == null && new File(conf.get("db_dir")).list().length == 0) {
        if (gdbs == null && new File(conf.get("db_dir")).list() == null) {
            instance();
            gdbs.registerTransactionEventHandler(this);
        } else if (gdbs == null) {
        	System.out.println("Directory not empty!");
        	open();
        }
    }

    private static GraphDatabaseService open() {
    	gdbs = new GraphDatabaseFactory().newEmbeddedDatabase(db_dir);
    	Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (gdbs != null) {
                    gdbs.shutdown();
                }
            }
        });
    	return gdbs;
	}

	public static GraphDatabaseService instance() {
        //boolean lForceRDFReindexing = false;
        if (gdbs == null) {
            gdbs = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(db_dir).loadPropertiesFromFile(confFile).newGraphDatabase();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (gdbs != null) {
                        gdbs.shutdown();
                    }
                }
            });

        }
        return gdbs;

    }

    @Override
    public void beforeShutdown() {

    }

    @Override
    public void kernelPanic(ErrorState errorState) {

    }

    @Override
    public Object getResource() {
        return null;
    }

    @Override
    public ExecutionOrder orderComparedTo(KernelEventHandler kernelEventHandler) {
        return null;
    }

    @Override
    public Object beforeCommit(TransactionData transactionData) throws Exception {
        return null;
    }

    @Override
    public void afterCommit(TransactionData transactionData, Object o) {

    }

    @Override
    public void afterRollback(TransactionData transactionData, Object o) {

    }

    @Override
    public void setSession(String session) {

    }

    @Override
    public String getSession() {
        return null;
    }

    @Override
    public boolean backup() {
        return false;
    }

    @Override
    public Node getNodeById(long lID) {
        return null;
    }

    @Override
    public Relationship getRelationShipById(long lID) {
        return null;
    }

    @Override
    public Node findNode(Label label, String sProperty, Object object) {
        return null;
    }

    @Override
    public Set<Node> getNodes(Label label, String sProperty, Object object) {
		Set<Node> rSet = new HashSet<>(0);
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			rSet = gdbs.findNodes(label, sProperty, object).stream().collect(Collectors.toSet());
			tx.success();
		}
		return rSet;
    }

    @Override
    public Set<Node> getNodes(Label label) {
		Set<Node> rSet = new HashSet<>(0);
		try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
			rSet = gdbs.findNodes(label).stream().collect(Collectors.toSet());
			tx.success();
		}
		return rSet;
    }

    @Override
    public void createIndex(Label pLabel, String ptype) {
    	try {
    	    IndexDefinition indexDefinition;
    	    try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {

    	        Schema schema = MDB_Neo4J_Impl.gdbs.schema();

    	        //schema.constraintFor(pLabel).assertPropertyIsUnique(ptype).create();

    	        IndexCreator creator = schema.indexFor(pLabel);

    	        // only one type allowed! bad!
    	        creator = creator.on(ptype);

    	        indexDefinition = creator.create();

    	        tx.success();
    	    }

    	    try (Transaction tx = gdbs.beginTx()) {
    	        Schema schema = gdbs.schema();
    	       System.out.println(String.format("Percent complete: %1.0f%%",
    	                schema.getIndexPopulationProgress(indexDefinition).getCompletedPercentage()));
    	    }
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    }
}
