package org.hucompute.services.uima.database.neo4j.data;

import org.neo4j.graphdb.RelationshipType;

/**
 * Extension of {@link org.hucompute.services.uima.database.neo4j.Const} that adds Neo4j specific Relationship types.
 */
public class Const extends org.hucompute.services.uima.database.neo4j.Const{

	/**
	 * Neo4j relationship types.
	 * <p>Basic types <i>pos, lemma, token, paragraph</i> and <i>sentence</i> connect Document-[<i>type</i>]-&gt;Type.</p>
	 * <p><i>inDocument, inParagraphS, inParagraphT and inSentence</i> types connect Child-[<i>inType</i>]-&gt;Type.</p>
	 * <p><i>inParagraphS</i> connect paragraphs with Sentences, <i>inParagraphT</i> connect paragraphs with tokens.</p>
	 * <p><i>successorT, successorP successorS</i> connect a node with the following one.
	 * Note: sentences do not connect into the next paragraph; tokens do not connect into the next sentence.</p>
	 * @author Manuel Stoeckel
	 */
    public enum RelationType implements RelationshipType {
        pos,
        lemma,
        token,
        inDocument,
        inParagraphS,
        inParagraphT,
        inSentence,
        successorT,
        successorP,
        successorS,
        paragraph,
        sentence
    }

}
