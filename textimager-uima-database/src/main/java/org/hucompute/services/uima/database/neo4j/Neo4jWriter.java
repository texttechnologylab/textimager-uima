package org.hucompute.services.uima.database.neo4j;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.uima.database.AbstractWriter;
import org.hucompute.services.uima.database.neo4j.data.Const.RelationType;
import org.hucompute.services.uima.database.neo4j.impl.*;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.HashSet;

public class Neo4jWriter extends AbstractWriter {
	MDB_Neo4J_Impl pMDB;
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		pMDB = new MDB_Neo4J_Impl("src/main/resources/neo4j/conf.conf");
		pMDB.createIndex(Lemma_Neo4J_Impl.getLabel(), "value");
		pMDB.createIndex(Pos_Neo4J_Impl.getLabel(), "value");
	}

	/**
	 * Processes one document jCas in a single (offline) Neo4j transaction.
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		resumeWatch();
		try{
			try (Transaction tx = MDB_Neo4J_Impl.gdbs.beginTx()) {
				/* Document creation. */
				final String docId = DocumentMetaData.get(jCas).getDocumentId();
				Document_Neo4J_Impl neo4jDocument = Document_Neo4J_Impl.getOrCreate(pMDB,docId);
				neo4jDocument.setProperty("id", docId);
				neo4jDocument.setProperty("text", jCas.getDocumentText());
				neo4jDocument.setProperty("language", jCas.getDocumentLanguage());

				/* Iteration variable initialization. */
				Paragraph_Neo4J_Impl previousP = null;
				Paragraph_Neo4J_Impl neo4jPara = null;
				Sentence_Neo4J_Impl previousS = null;
				Sentence_Neo4J_Impl neo4jSentence = null;
				Token_Neo4J_Impl previousT = null;
				Token_Neo4J_Impl neo4jToken = null;
				Pos_Neo4J_Impl neo4jpos = null;
				Lemma_Neo4J_Impl neo4jlemma = null;

				/*
				 * Iterate over each element of the jCas that was annotated as a Paragraph.
				 * Each paragraph gets properties for its beginning and ending character position
				 * and the id of the document it is contained in. This is done, so matching paragraphs don't
				 * get relationships with more than one document.
				 */
				for(Paragraph paragraph : JCasUtil.select(jCas, Paragraph.class)) {
					neo4jPara = Paragraph_Neo4J_Impl.create(pMDB);
					neo4jPara.setProperty("begin", paragraph.getBegin());
					neo4jPara.setProperty("end", paragraph.getEnd());
					neo4jPara.setProperty("id", docId);

					/* A successor relationship to the previous paragraph is created. */
					neo4jDocument.addAnnotation(neo4jPara, RelationType.paragraph);
					if(previousP != null)
						neo4jPara.createRelationshipTo(previousP.getNode(), RelationType.successorP);
					previousP = neo4jPara;

					/*
					 * Previous sentence is set to null, thus no relationships between sentences of neighboring paragraphs.
					 * Other: see above.
					 */
					previousS = null;
					for(Sentence sentence : JCasUtil.selectCovered(jCas, Sentence.class, paragraph)) {
						neo4jSentence = Sentence_Neo4J_Impl.create(pMDB);
						neo4jSentence.setProperty("begin", sentence.getBegin());
						neo4jSentence.setProperty("end", sentence.getEnd());
						neo4jSentence.setProperty("id", docId);

						/* A successor relationship to the previous sentence in the same paragraph is created. */
						neo4jDocument.addAnnotation(neo4jSentence, RelationType.sentence);
						if(previousS != null)
							neo4jSentence.createRelationshipTo(previousS.getNode(), RelationType.successorS);
						previousS = neo4jSentence;

						/* Connect paragraph with sentence. */
						neo4jSentence.createRelationshipTo(neo4jPara.getNode(), RelationType.inParagraphS);

						/* See above. */
						previousT = null;
						for (Token token : JCasUtil.selectCovered(jCas, Token.class, sentence)) {
							neo4jToken = Token_Neo4J_Impl.create(pMDB);
							neo4jToken.setProperty("begin", token.getBegin());
							neo4jToken.setProperty("end", token.getEnd());
							neo4jToken.setProperty("id", docId);
							neo4jToken.setProperty("value", token.getCoveredText());

							/* POS and lemma are set. */
							neo4jpos = Pos_Neo4J_Impl.getOrCreate(pMDB,token.getPos().getPosValue());
							neo4jToken.setPos(neo4jpos);

							neo4jlemma = Lemma_Neo4J_Impl.getOrCreate(pMDB,token.getLemma().getValue());
							neo4jToken.setLemma(neo4jlemma);

							/* A successor relationship to the previous token in the same sentence is created. */
							neo4jDocument.addAnnotation(neo4jToken, RelationType.token);
							if(previousT != null)
								neo4jToken.createRelationshipTo(previousT.getNode(), RelationType.successorT);
							previousT = neo4jToken;

							/* Connects the token to its parent sentence and paragraph. */
							neo4jToken.createRelationshipTo(neo4jSentence.getNode(), RelationType.inSentence);
							neo4jToken.createRelationshipTo(neo4jPara.getNode(), RelationType.inParagraphT);
						}
					}
				}

				/* Creates a HashSet of {@link de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma lemmata}. */
				HashSet<String> LL = new HashSet<>();
				JCasUtil.select(jCas, Lemma.class).forEach(e -> LL.add(e.getValue()));
				/* Gets the Lemma_Neo4J_Impl Object for each lemmata. */
				ArrayList<Lemma_Neo4J_Impl> lemmata = new ArrayList<>();
				LL.forEach(e -> lemmata.add(Lemma_Neo4J_Impl.getOrCreate(pMDB,e)));

				/* Creates a inDocument realtionship for each lemma. */
				for (Lemma_Neo4J_Impl lemma : lemmata) {
					lemma.createRelationshipTo(neo4jDocument.getNode(), RelationType.inDocument);
				}
				tx.success();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		suspendWatch();
		log();
	}
}
