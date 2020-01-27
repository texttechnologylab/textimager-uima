package org.hucompute.textimager.uima.io.mediawiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Infos about occurances of multiple DDCs. */
public class DDCInfos {

	private HashMap<String, DDCInfo> map = new HashMap<String, DDCInfo>();

	public class DDCOccurance {
		public String documentTitle;
		public int paragraph, sentence;
		public String text;

		public DDCOccurance(String documentTitle, int paragraph, int sentence, String text) {
			this.documentTitle = documentTitle;
			this.paragraph = paragraph;
			this.sentence = sentence;
			this.text = text.replace("\n", " ");
		}
	}
		
	/** Infos about occurances of a DDC. */
	public class DDCInfo {
		public List<String> documents;
		public List<DDCOccurance> sentences;
		public List<DDCOccurance> paragraphs;

		public DDCInfo() {
			documents = new ArrayList<String>();
			sentences = new ArrayList<DDCOccurance>();
			paragraphs = new ArrayList<DDCOccurance>();
		}

		public void addToParagraphs(String documentTitle, int paragraph, String text) {
			paragraphs.add(new DDCOccurance(documentTitle, paragraph, -1, text));
		}

		public void addToSentences(String documentTitle, int paragraph, int sentence, String text) {
			sentences.add(new DDCOccurance(documentTitle, paragraph, sentence, text));
		}
	}

	public DDCInfo get(String ddc) {
		if (ddc == null || ddc.length() != 3) {
			System.out.println(" BUG  | Tried to get DDC infos for invalid id: " + ddc);
			return null;
		}
		DDCInfo info = map.get(ddc);
		if (info == null) {
			info = new DDCInfo();
			map.put(ddc, info);
		}
		return info;
	}

}
