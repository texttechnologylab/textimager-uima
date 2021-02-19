package org.hucompute.textimager.uima.geonames.gazetteer.neo4j;

//import org.hucompute.textimager.uima.tree.SkipGramGazetteerModel;
//import org.neo4j.driver.*;

import java.io.IOException;

//public class StringTreeGazetteerModel extends SkipGramGazetteerModel {
//
//    private static String NAME = "name";
//    private static String VALUE = "value";
//    private static String LABEL = "TOKEN";
//
//    private Driver driver;
//
//    public StringTreeGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, double dMinLength) throws IOException {
//        super(aSourceLocations, bUseLowercase, dMinLength);
//
//        init(bUseLowercase);
//    }
//
//    public StringTreeGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, String sLanguage, double dMinLength, boolean bAllSkips, boolean bSplitHyphen) throws IOException {
//        super(aSourceLocations, bUseLowercase, sLanguage, dMinLength, bAllSkips, bSplitHyphen);
//
//        init(bUseLowercase);
//    }
//
//    public String checkString(String sValue){
//
//        String[] split = sValue.split(" ");
//
//
//
//        String rString = "";
//
//        try ( Session session = driver.session() ) {
//
//
//            boolean found = session.readTransaction(new TransactionWork<Boolean>() {
//
//                @Override
//                public Boolean execute(Transaction tx) {
//                    boolean rBool = false;
//
//                    String sQuery = "MATCH p=((n:TOKEN)-[r:leaf*]->(m:TOKEN) WHERE a.name = '"+split[0]+"' AND b.name = '"+split[split.length-1]+"') return nodes(p)";
//
//                    Result res = tx.run(sQuery);
//
//                    rBool = res.hasNext();
//
//                    return rBool;
//                }
//            });
//
//            if (found) {
//                rString = String.valueOf(elementUriMap.get(sValue));
//            }
//        }
//
//
//        return rString;
//
//    }
//
//    private void init(boolean bUseLowercase) {
//
//        driver = GraphDatabase.driver( "bolt://huaxal.hucompute.org:7687", AuthTokens.basic( "neo4j", "test") );
//
//        skipGramSet.stream().forEach(sk->{
//
//            final String[] lastId = {""};
//
//            for (String s : sk.split(" ")) {
//
//            try ( Session session = driver.session() ) {
//
//                session.writeTransaction(new TransactionWork<Boolean>() {
//                    @Override
//                    public Boolean execute(Transaction tx) {
//                        String sQuery = "";
//                        if (bUseLowercase) {
//                            sQuery = "MATCH (n:" + LABEL + ") WHERE n." + NAME + "= '" + s.toLowerCase() + "' return n;";
//                        } else {
//                            sQuery = "MATCH (n:" + LABEL + ") WHERE n." + NAME + "= '" + s + "' return n;";
//                        }
//
//                        String resCreate = "";
//                        Result result = tx.run(sQuery);
//
//                        if (!result.hasNext()) {
//                            String sCreate = "CREATE (n:" + LABEL + " {" + NAME + ": '" + (bUseLowercase ? s.toLowerCase() : s) + "'}) return n.name;";
//                            result = tx.run(sCreate);
//
////                            result.forEachRemaining(r->{
////                                System.out.println(r);
////                            });
//
//                            if (result.hasNext()) {
//                                resCreate = (bUseLowercase ? s.toLowerCase() : s);
//                            }
//
//                        } else {
//                            resCreate = (bUseLowercase ? s.toLowerCase() : s);
//                        }
//
//                        if(lastId[0].length()>0){
//
//                            String sCheck = "MATCH (a:TOKEN)-[r:leaf]->(b:TOKEN)\n" +
//                                    "WHERE a.name = '"+lastId[0]+"' AND b.name = '"+resCreate+"' return r;";
//
//                            result = tx.run(sCheck);
//
//                            if(!result.hasNext()){
//                                String sCreate = "MATCH (a:TOKEN), (b:TOKEN)\n" +
//                                        "WHERE a.name = '"+lastId[0]+"' AND b.name = '"+resCreate+"'\n" +
//                                        "CREATE (a)-[r:leaf]->(b)\n" +
//                                        "RETURN type(r)";
//                                result = tx.run(sCreate);
//
//                            }
//
//
//                        }
//
//                        lastId[0] = resCreate;
//
//                        return true;
//
//
//                    }
//                });
//            }
//
//        }
//
////            try (Transaction tx = gdbs.beginTx()) {
////
////                Node formerNode = null;
////
////                for (String s : sk.split(" ")) {
////                    Node n = null;
////                    if (bUseLowercase) {
////                        n = gdbs.findNode(pLabel, NAME, sk.toLowerCase());
////                    } else {
////                        n = gdbs.findNode(pLabel, NAME, sk);
////                    }
////
////                    if(n==null){
////                        n = gdbs.createNode(pLabel);
////                        n.setProperty(NAME, bUseLowercase ? sk.toLowerCase() : sk);
////
////                    }
////
////                    if(formerNode!=null && n!=formerNode){
////                        formerNode.createRelationshipTo(n, relType);
////                    }
////
////                    formerNode = n;
////
////                }
////
////                tx.success();
////            }
//        });
//
////        try (Transaction tx = gdbs.beginTx()) {
////
////            Result result = gdbs.execute("MATCH p=((n:TOKEN)-[r:leaf*]->(m:TOKEN)) return nodes(p)");
////
////            while (result.hasNext()) {
////                Map<String, Object> row = result.next();
//////                System.out.println(row);
////                List nodes = ((List) row.get("nodes(p)"));
////
////                nodes.forEach(node -> {
////
////                    Node pNode = (Node) node;
////
////                    System.out.println(pNode.getProperty("NAME"));
////                });
////
////            }
////
////            tx.success();
////        }
//
//    }
//}
