package org.hucompute.services.uima.database.cassandra;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.json.JsonCasSerializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.uima.database.AbstractWriter;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.exceptions.SyntaxError;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


public class CassandraWriter extends AbstractWriter {

	private Cluster cluster;
	private Session session;
	private int id = 0;
    Set<String> types = null;
    Pattern typePattern = null;
    Pattern secondTypePattern = null;
    Set<String> tableCreates = null;
    Set<String> tableCreated = null;
    Set<String> insertQueries = null;


    private JsonCasSerializer xcs;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

        types = new HashSet<>();
        tableCreates = new HashSet<>();
        tableCreated = new HashSet<>();
        insertQueries = new HashSet<>();
		//Initialize db connection

		cluster = null;
        typePattern = Pattern.compile("([^\\s]*\\.type\\.)([^\\.|\\s]*)");
        secondTypePattern = Pattern.compile("[^\\s]*\\.type\\.[^\\s|^\\.]*\\.([^\\s]*)");

		try {
//			String id = "0_";
			cluster = Cluster.builder()
					.addContactPoint("127.0.0.1")
					.withPort(
							9042
					)
					.withCredentials(
							"cassandra",
							"cassandra"
					)
					//.withClusterName("textimager")
					.build();
			session = cluster.connect();
//			session.execute("drop textimager;");
			session.execute("create keyspace textimager WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};");
			session.execute("use textimager;");
		}
		catch (Exception e){
			e.printStackTrace();
		}
//		finally {if ( cluster != null) cluster.close();
//		
//		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		resumeWatch();
		//saveCAS(jCas)

		Matcher m = null;
        try {

            FSIterator iterator = jCas.getAnnotationIndex().iterator();
            while (iterator.hasNext()) {
                Annotation annoation = (Annotation) iterator.next();
                m = typePattern.matcher(annoation.getType().toString());
                if (m.find()) {
//                    String typeFull = m.group(1) + m.group(2);
                    String type = m.group(2); // what does this exactly do? And for what is typeFull?

                    types.add(type);
                    // uncommented line below 
                    addByType(type, annoation, DocumentMetaData.get(jCas).getDocumentId());
                }

            }
//            System.out.println(types);
            }
            catch(Exception e){
                e.printStackTrace();
            }
		
        int count = insertQueries.size() + tableCreates.size();
        if(count >= 34845){ // @Hemati: why this exact number?
            System.out.println("Starting queries...");
            batchQueries();
            System.out.println("...finished.");
        }
        suspendWatch();
		log();
		
	}


    private void createTable(String name, LinkedHashMap<String,String> map) throws Exception{
        StringBuilder fields = new StringBuilder();
        StringBuilder primKey = new StringBuilder();
        primKey.append("(");

//      String fields = "", primKey = "(";
        for (String s : map.keySet()) {
            fields.append(s);
            fields.append(" text,");
            primKey.append(s);
            primKey.append(", ");
//            fields += s + " text,";
//            primKey += s + ", ";
        }
        primKey.delete(primKey.length() - 2, primKey.length()-1);
        primKey.append(")");
//        primKey = primKey.substring(0, primKey.length() - 2);
//        primKey += ")";
//        System.out.println(primKey);
//        System.out.println(map);

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(name);
        query.append(" (");
        query.append(fields.toString());
        query.append(" primary key ");
        query.append(primKey.toString());
        query.append(");");
        tableCreates.add(query.toString());
//        String query = "CREATE TABLE IF NOT EXISTS " + name + " (" + fields + " primary key " + primKey + ");";

//        System.out.println(query.toString());

        ResultSet rs = (ResultSet) session.execute(query.toString());


    }

	private void insert(String table, LinkedHashMap<String,String> map) throws Exception{
        try {
            StringBuilder fields = new StringBuilder(), value = new StringBuilder();
            fields.append("(");
            value.append("(");
//            String fields = "(", value = "(";
            for (String s : map.keySet()) {
                fields.append(s);
                fields.append(", ");
                value.append("'");
                value.append(map.get(s));
                value.append("'");
                value.append(", ");
//                fields += s + ", ";
//                value += "'" + map.get(s) + "'" + ", ";
            }
            fields.delete(fields.length() - 2, fields.length() - 1);
            value.delete(value.length() - 2, value.length() - 1);
            fields.append(")");
            value.append(")");
//            fields = fields.substring(0, fields.length() - 2);
//            fields += ")";
//            value = value.substring(0, value.length() - 2);
//            value += ")";
            StringBuilder  query = new StringBuilder();
            query.append("insert into ");
            query.append(table);
            query.append(" ");
            query.append(fields);
            query.append(" values");
            query.append(value);
            query.append(";");
            insertQueries.add(query.toString());
//            String query = "insert into " + table + " " + fields + " values" + value + ";";
//            System.out.println(query.toString());

//            ResultSet rs = session.execute(query.toString());
        }
        catch (SyntaxError e){
            for(String key: map.keySet()){
                map.put(key, repairQuotation(map.get(key)));
            }
            insert(table,map);
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}

	private void addByType(String type, Annotation annotation, String id){
        Matcher m = null;
        LinkedHashMap<String,String> map = new LinkedHashMap<>();

        map.put("xmi", id);
        map.put("start", String.valueOf(annotation.getBegin()));
        map.put("end", String.valueOf(annotation.getEnd()));
        switch (type){
//            case "WikiDataHyponym":
//                WikiDataHyponym wdh = (WikiDataHyponym) annotation;
//                map.put("depth", String.valueOf(wdh.getDepth()));
//
//                break;
            case "pos":
                POS pos = (POS) annotation;
                map.put("value", pos.getPosValue());
                break;
            case "dependency":
                m = secondTypePattern.matcher(annotation.getType().toString());
//                Dependency dep = (Dependency) annotation;
//                dep.getDependent().getId();

                if(m.find()){
                    map.put("type", m.group(1));
                }
                break;
            case "Lemma":
                Lemma lemma = (Lemma) annotation;
                map.put("value", lemma.getValue().equals("'")?"''":lemma.getValue());
                break;
            case "html":
                m = secondTypePattern.matcher(annotation.getType().toString());
                if(m.find()){
                    map.put("type", m.group(1));
                }
                break;
            case "Token":
                Token token = (Token) annotation;
                type = "tokens";
                break;
            case "DocumentMetaData":
                DocumentMetaData meta = (DocumentMetaData) annotation;
                map.put("title", meta.getDocumentTitle());
                map.put("collectionId",meta.getCollectionId());
                map.put("baseUri",meta.getDocumentBaseUri());
                map.put("fileUri",meta.getDocumentUri());
                type = "xmi";
                try {
                    createTable(type, map);
                    insert(type,map);
                }catch (Exception e){
                e.printStackTrace();
                }
                return;
//            case "Wikify":
//                Wikify wikify = (Wikify) annotation;
//                map.put("link", wikify.getLink());
//                map.put("title", wikify.getTitle());
//                break;
            case "morph":
                m = secondTypePattern.matcher(annotation.getType().toString());
                if(m.find()){
                    map.put("type", m.group(1));
                }
                break;
            case "Sentence":
                Sentence sentence = (Sentence) annotation;
//                map.put("id", sentence.getId());
                break;
            case "TagsetDescription":
                TagsetDescription tagsetDescription = (TagsetDescription) annotation;
                m = secondTypePattern.matcher(tagsetDescription.getLayer().toString());
                if(m.find()){
                    map.put("layer", m.group(1));
                }
                map.put("name",tagsetDescription.getName());
                break;
            case "Paragraph":
                Paragraph paragraph = (Paragraph) annotation;
//                map.put("id",paragraph.getId());
                break;
            default:
                m = secondTypePattern.matcher(annotation.getType().toString());
                String secondType = "";
                if(m.find()){
                    map.put("type", m.group(1));
                }
                break;
        }
        try {
            createTable(type, map);

            insert(type,map);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String repairQuotation(String s){
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i)=='\''){
                s = s.substring(0,i) +"'"+ s.substring(i);
                i++;
            }
        }
        return s;
    }

    private void batchQueries(){
        for(String s : tableCreates){
            if(!tableCreated.contains(s)) {
                session.execute(s);
                tableCreated.add(s);
            }
        }

        BatchStatement stmt = new BatchStatement();
        Set<String> currentBatch = new HashSet<>();
        try{
        int count = 0;
            System.out.print("Batch execute");

        for(String s : insertQueries){
            stmt.add(new SimpleStatement(s));

            currentBatch.add(s);
            count++;
            if(count==2500){
                session.execute(stmt);
                System.out.print(".");
                stmt.clear();
                currentBatch.clear();
                count = 0;
            }
        }

        session.execute(stmt);
        insertQueries.clear();
        } catch (SyntaxError e){
            System.out.println("Syntax error in batch, trying to fix queries...");
            stmt.clear();
            for(String s : currentBatch){
                try {
                    session.execute(s);
                }catch (Exception ex){
                    System.out.println("Broken Query: " + s);
                }

            }
        }
        System.out.println("finished.");
    }

//    private static String escapeCharacter(String string){
//        switch(string){
//            case "'":
//            case "(":
//            case ")":
//            case "[":
//            case "]":
//            case "$":
//            case "?":
//            case "\\":
//            case "{":
//            case "}":
//            case "!":
//            case "|":
//            case "*":
//            case "<":
//            case ">":
//
//        }
//    }

}