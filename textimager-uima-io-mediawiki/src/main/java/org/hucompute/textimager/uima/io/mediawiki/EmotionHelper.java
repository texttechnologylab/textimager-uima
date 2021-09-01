package org.hucompute.textimager.uima.io.mediawiki;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bitbucket.rkilinger.ged.Emotion;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
class EmotionHelper {
    Set<Token> disgustSet = new HashSet<Token>();
    Set<Token> contemptSet = new HashSet<Token>();
    Set<Token> surpriseSet = new HashSet<Token>();
    Set<Token> fearSet = new HashSet<Token>();
    Set<Token> mourningSet = new HashSet<Token>();
    Set<Token> angerSet = new HashSet<Token>();
    Set<Token> joySet = new HashSet<Token>();
	

	public EmotionHelper(JCas cas) {
		for (Emotion emotion : JCasUtil.select(cas, Emotion.class)) {
            if(emotion.getDisgust() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
                    disgustSet.add(token);
                }
            }
            if(emotion.getContempt() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
					contemptSet.add(token);
                }
            }
            if(emotion.getSurprise() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
					surpriseSet.add(token);
				}
			}
            if(emotion.getFear() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
                    fearSet.add(token);
                }
            }
            if(emotion.getMourning() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
					mourningSet.add(token);
				}
            }
            if(emotion.getAnger() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
					angerSet.add(token);
				}
            }
            if(emotion.getJoy() == 1) {
                for (Token token : JCasUtil.selectCovered(Token.class, emotion)) {
                    joySet.add(token);
                }
            }

        }
        
	}
	public String getEmotionHTMLDataAttribute_dummy() {
		StringBuilder res = new StringBuilder();
		res.append("Haus_NN Baum_NN Dach_NN Wand_NN Tisch_NN Flasche_NN");
		return res.toString();
	}
	public String getEmotionStringListJS_dummy() {
		StringBuilder res = new StringBuilder();
		res.append("[");
		res.append("\"Wut\",\"Hass\"");
		res.append("]");
		return res.toString();
	}

	public String getEmotionHTMLDataAttribute(Set<Token> emotionSet) {
		StringBuilder res = new StringBuilder();
		String delim= "";

		for (Token entry : emotionSet) {
			String txt = entry.getCoveredText();
			String pos_tag = entry.getPosValue() != null ? entry.getPosValue() : "NN";
			res.append(delim).append(txt).append("_").append(pos_tag);
			delim = " ";
		}
		return res.toString();
	}
    
	public String getEmotionStringListJS(Set<Token> emotionSet) {
		StringBuilder res = new StringBuilder();
		res.append("[");
		String delim= "";

		for (Token entry : emotionSet) {
			String txt = entry.getCoveredText();
			res.append(delim).append("\"").append(txt).append("\"");
			delim = ",";
		}
		res.append("]");
		return res.toString();
	}

}
