package org.hucompute.textimager.uima.tree.neo4j;

import org.hucompute.textimager.uima.tree.SkipGramGazetteerModel;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StringTreeGazetteerModel extends SkipGramGazetteerModel {

    private GraphDatabaseService gdbs = null;
    private RelationshipType relType = RelationshipType.withName("leaf");
    private Label pLabel = Label.label("TOKEN");
    private static String NAME = "name";

    public StringTreeGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, double dMinLength) throws IOException {
        super(aSourceLocations, bUseLowercase, dMinLength);

        init(bUseLowercase);
    }

    public StringTreeGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, String sLanguage, double dMinLength, boolean bAllSkips, boolean bSplitHyphen) throws IOException {
        super(aSourceLocations, bUseLowercase, sLanguage, dMinLength, bAllSkips, bSplitHyphen);

        init(bUseLowercase);
    }


    private void init(boolean bUseLowercase) {

//        org.apache.lucene.codecs.lucene54.Lucene54Codec.availableCodecs().stream().forEach(c->{
//            System.out.println(c);
//        });
//
//        driver = GraphDatabase.driver("neo4j://huaxal.hucompute.org:7687", AuthTokens.basic("neo4j", "test"));
//
//        try (Session session = driver.session()){
//
//            Transaction t = session.beginTransaction();
//
//            Result rs = session.run("MATCH (n:TOKEN{NAME:'L.'})  RETURN n");
//
//            while(rs.hasNext()){
//                Record r = rs.next();
//                System.out.println(r);
//            }
//
//        }

        this.gdbs = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("/tmp/geonames")).newGraphDatabase();
//        this.gdbs = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("/home/ducc/.cache/geonames/")).newGraphDatabase();
        skipGramSet.stream().forEach(sk->{
            try (Transaction tx = gdbs.beginTx()) {

                Node formerNode = null;

                for (String s : sk.split(" ")) {
                    Node n = null;
                    if (bUseLowercase) {
                        n = gdbs.findNode(pLabel, NAME, sk.toLowerCase());
                    } else {
                        n = gdbs.findNode(pLabel, NAME, sk);
                    }

                    if(n==null){
                        n = gdbs.createNode(pLabel);
                        n.setProperty(NAME, bUseLowercase ? sk.toLowerCase() : sk);

                    }

                    if(formerNode!=null && n!=formerNode){
                        formerNode.createRelationshipTo(n, relType);
                    }

                    formerNode = n;

                }

                tx.success();
            }
        });

        Result result = gdbs.execute( "MATCH p=((n:TOKEN)-[r:leaf*]->(m:TOKEN)) return nodes(p)");

        while ( result.hasNext() ){
            Map<String,Object> row =result.next();
//                System.out.println(row);
            List nodes = ((List)row.get("nodes(p)"));

            nodes.forEach(node->{

                Node pNode = (Node)node;

                System.out.println(pNode.getProperty("NAME"));
            });

        }

    }
}
