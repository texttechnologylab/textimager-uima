package org.hucompute.textimager.uima.io.mediawiki;

import java.util.Set;
import org.apache.uima.jcas.JCas;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SemiographHelper {
    EmotionHelper eh;
	public SemiographHelper(JCas cas) {
        eh = new EmotionHelper(cas);

    }
    public String mergeStaticSemiographString(String embedding_id){
        StringBuilder res = new StringBuilder();
        res.append(eh.disgustSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "disgust", eh.disgustSet));
        res.append(eh.contemptSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "contempt", eh.contemptSet));
        res.append(eh.surpriseSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "surprise", eh.surpriseSet));
        res.append(eh.fearSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "fear", eh.fearSet));
        res.append(eh.mourningSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "mourning", eh.mourningSet));
        res.append(eh.angerSet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "anger", eh.angerSet));
        res.append(eh.joySet.size() == 0 ? "" : buildStaticSemiographHTMLDiv(embedding_id, "joy", eh.joySet));
        return res.toString();
    }
	public String buildStaticSemiographHTMLDiv(String embedding_id, String emotion_name, Set<Token> emotionToken){
		StringBuilder res = new StringBuilder();

        res.append("\n== ").append("Semiograph ").append(emotion_name).append(" ==\n");
		res.append("<html>");
        res.append("<div id='embedding_" + emotion_name + "' class=\"embeddingviz\" data-word=\"");
        res.append(eh.getEmotionHTMLDataAttribute(emotionToken)); //POS-Tagged Token; separated by spaces
        res.append("\" ");
        res.append("data-embedding=\"" + embedding_id + "\" ");
        res.append("data-ddc=\"" + "1" + "\" ");
        res.append("data-maxn=\"" + "8" + "\" ");
        res.append("data-width=\"" + "1000" + "\" ");
        res.append("data-height=\"" + "500" + "\"");
        res.append("></div></html>");
        res.append("\n");
		return res.toString();
	}


}

