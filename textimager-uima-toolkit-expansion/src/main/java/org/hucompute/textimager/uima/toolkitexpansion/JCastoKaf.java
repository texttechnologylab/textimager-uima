package org.hucompute.textimager.uima.toolkitexpansion;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Term;
import ixa.kaflib.Terminal;
import ixa.kaflib.Tree;
import ixa.kaflib.TreeNode;
import ixa.kaflib.WF;

public class JCastoKaf {
	
	JCas jCas = null; 
	KAFDocument kaf = null;
	String KAF_LOCATION = "";
	
	public JCastoKaf(JCas aJCas) {
		jCas = aJCas;
		kaf = new KAFDocument(jCas.getDocumentLanguage(), "1.0");
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		KAF_LOCATION = "/tmp/kaf" + timestamp.getTime() + ".kaf";
		
		String text = jCas.getDocumentText();
		while(text.contains("\n\n")) {
			text = text.replaceAll("\n\n", "\n");
		}
		
		kaf.setRawText(text);
	}
	
	public KAFDocument getKaf() {
		return kaf;
	}
	public JCas getJCas() {
		return jCas;
	}
	public String getKafLocation() {
		return KAF_LOCATION;	
	}
	
	/**
	 * Add Sentences and Token to JCas
	 * 
	 * @return
	 */
	public KAFDocument add_Token_Sentence(){
		int i = 0;
		for (Sentence jCasSentence : JCasUtil.select(jCas, Sentence.class)) {
			i++;
			List<Token> jCasTokens = selectCovered(jCas, Token.class, jCasSentence);
			
			for(Token jCasToken :jCasTokens) {
				kaf.newWF(jCasToken.getCoveredText(), jCasToken.getBegin(),i);	
				//System.out.println(jCasToken.getCoveredText());
			}
		}
		return kaf;
		
	}
	
	/**
	 * Add Sentences , Token , Lemma and POS to JCas
	 * 
	 * @return
	 */
	public KAFDocument add_POS_Lemma() {
		
		int i = 0;
		for (Sentence jCasSentence : JCasUtil.select(jCas, Sentence.class)) {
			i++;
			List<Token> jCasTokens = selectCovered(jCas, Token.class, jCasSentence);
			
			for(Token jCasToken :jCasTokens) {
				int begin = jCasToken.getBegin();
				int end = jCasToken.getEnd();
				WF wf = kaf.newWF(jCasToken.getCoveredText(), jCasToken.getBegin(),i);	
				POS pos = JCasUtil.selectSingleAt(jCas, POS.class, begin, end);
				Lemma lem = JCasUtil.selectSingleAt(jCas, Lemma.class, begin, end);
				Morpheme morph = JCasUtil.selectSingleAt(jCas, Morpheme.class, begin, end);
				
				List<WF> wfList = new ArrayList<WF>();
				wfList.add(wf);
				kaf.newTerm("", lem.getValue(), pos.getPosValue(), kaf.newWFSpan(wfList)).setMorphofeat(morph.getMorphTag());
			}
		}
		return kaf;
		
	}
	

}
