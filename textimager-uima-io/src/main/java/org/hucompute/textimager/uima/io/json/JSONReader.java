package org.hucompute.textimager.uima.io.json;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class JSONReader extends JCasAnnotator_ImplBase{


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		//		JSONArray textArray = new JSONArray(aJCas.getDocumentText());
		//		
		//		StringBuilder sb = new StringBuilder();
		//		int offset = 0;
		//		System.out.println("drin in 152");
		//		for(int i = 0; i< textArray.length(); i++){
		//			JSONObject currentText = textArray.getJSONObject(i);
		//			JCas cas = parseInputText(currentText.getString("text"), currentText.getString("name"), offset);
		//			System.out.println("drin in 155");
		//			if(cas == null){
		//				System.out.println("drin in 157");
		//				sb.append(currentText.getString("text")).append(System.lineSeparator()).append(System.lineSeparator()).append(" ");
		//				DocElement element = new DocElement(aJCas, offset, offset+currentText.getString("text").length()+1);
		//				element.setName(currentText.getString("name"));
		//				offset = sb.length()-1;
		//				element.addToIndexes();
		//			}
		//			else{
		//				System.out.println("drin in 165");
		//				sb.append(cas.getDocumentText()).append(System.lineSeparator()).append(System.lineSeparator()).append(" ");
		//				DocElement element = new DocElement(aJCas, offset, offset+cas.getDocumentText().length()+1);
		//				element.setName(currentText.getString("name"));
		//
		//				element.addToIndexes();
		//
		//				List<Sentence> sentences = IteratorUtils.toList(select(cas, Sentence.class).iterator());
		//				CasCopier copier = new CasCopier(cas.getCas(), aJCas.getCas());
		//				for (Sentence sentence : sentences) {
		//					Sentence destItem = (Sentence) copier.copyFs(sentence);
		//					destItem.setBegin(destItem.getBegin()+offset);
		//					destItem.setEnd(destItem.getEnd()+offset);
		//
		//					aJCas.addFsToIndexes(destItem);
		//				}
		//				List<Token> tokens = IteratorUtils.toList(select(cas, Token.class).iterator());
		//				for (Token sentence : tokens) {
		//					Token destItem = (Token) copier.copyFs(sentence);
		//					destItem.setBegin(destItem.getBegin()+offset);
		//					destItem.setEnd(destItem.getEnd()+offset);
		//
		//					aJCas.addFsToIndexes(destItem);
		//				}
		//				offset = sb.length()-1;
		//				//				mergeJCas(cas,jCas);
		//			}
		//		}
		//		aJCas.setDocumentText(sb.toString());
	}



	public JCas init(String input) throws UIMAException{
		JCas cas =  JCasFactory.createJCas();

		JSONObject object = new JSONObject(input);

		cas.setDocumentLanguage(object.getString("language"));

		String text = "";

		JSONArray sentences = object.getJSONArray("sentences");
		for (int i = 0; i<sentences.length();i++) {
			JSONArray elements = sentences.getJSONObject(i).getJSONArray("elements");
			Sentence sent = new Sentence(cas, text.length(), 0);
			for(int j = 0; j< elements.length();j++){
				JSONObject currentElement = elements.getJSONObject(j);
				if(text.length() >0 && !currentElement.getString("word_form").equals("."))
					text+=" ";
				Token token = new Token(cas,text.length(),text.length()+currentElement.getString("word_form").length());
				text += currentElement.getString("word_form");	

				if(!currentElement.getString("pos").equals("empty")){
					POS pos = new POS(cas, token.getBegin(), token.getEnd());
					pos.setPosValue(currentElement.getString("pos"));
					pos.addToIndexes();
					token.setPos(pos);
				}

				if(!currentElement.getString("lemma").equals("empty")){
					Lemma lemma = new Lemma(cas,token.getBegin(),token.getEnd());
					lemma.setValue(currentElement.getString("lemma"));
					lemma.addToIndexes();
					token.setLemma(lemma);
				}
				token.addToIndexes();
			}
			sent.setEnd(text.length());
			sent.addToIndexes();
		}

		cas.setDocumentText(text);
		return cas;
	}


}