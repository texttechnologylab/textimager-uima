package org.hucompute.textimager.uima.io.mediawiki;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bitbucket.rkilinger.ged.Emotion;
import org.json.JSONArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import io.swagger.util.Json;
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

	public String getUpToNSubString(String text, int n) {
		//Return the first n characters(or less if there are less than n characters) of the given text
		return text.substring(0, Math.min(text.length(), n));
	}

	public String getEmotionHTMLDataAttribute(Set<Token> emotionSet) {
		StringBuilder res = new StringBuilder();
		String delim= "";

		for (Token entry : emotionSet) {
			String txt = entry.getLemma().getValue();
			String tpos_tag = entry.getPosValue() != null ? entry.getPosValue() : "NN";
			String pos_tag;
			if (getUpToNSubString(tpos_tag, 2).equals("VV")) {
				pos_tag = "V";
				res.append(delim).append(txt).append("_").append(pos_tag);
				delim = " ";
			}else if(getUpToNSubString(tpos_tag, 3).equals("ADJ")) {
				pos_tag = "ADJ";
				res.append(delim).append(txt).append("_").append(pos_tag);
				delim = " ";
			}else if(getUpToNSubString(tpos_tag, 2).equals("NN")) {
				pos_tag = "NN";
				res.append(delim).append(txt).append("_").append(pos_tag);
				delim = " ";
			}else{
				System.out.println("Emotionhelper: Semiograph - Unsupported POS tag: " + tpos_tag);
				res.append("");
			}
		}
		return res.toString();
	}
	public JSONArray getEmotionJSONArray(Set<Token> emotionSet) {
		JSONArray res = new JSONArray();

		for (Token entry : emotionSet) {
			String txt = entry.getLemma().getValue();
			String tpos_tag = entry.getPosValue() != null ? entry.getPosValue() : "NN";
			String pos_tag;
			if (getUpToNSubString(tpos_tag, 2).equals("VV")) {
				pos_tag = "V";
				String tagged_word = txt + "_" + pos_tag;
				res.put(tagged_word);
			}else if(getUpToNSubString(tpos_tag, 3).equals("ADJ")) {
				pos_tag = "ADJ";
				String tagged_word = txt + "_" + pos_tag;
				res.put(tagged_word);
			}else if(getUpToNSubString(tpos_tag, 2).equals("NN")) {
				pos_tag = "NN";
				String tagged_word = txt + "_" + pos_tag;
				res.put(tagged_word);
			}else{
				System.out.println("Emotionhelper: Semiograph - Unsupported POS tag: " + tpos_tag);
				
			}
		}
		return res;
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
