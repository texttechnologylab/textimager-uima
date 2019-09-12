package org.hucompute.textimager.uima.io.mediawiki;

import java.util.Collection;
import java.util.HashMap;

public class GraphHelper {
	
	StringBuilder nodes = new StringBuilder();
	StringBuilder edges = new StringBuilder();
	HashMap<LemmaInfos.LemmaPos,Integer> indices = new HashMap<LemmaInfos.LemmaPos,Integer>();
	int index = 0;

	public GraphHelper() {
	}

	public GraphHelper(LemmaInfos.LemmaPos center) {
		start(center);
	}

	public void add(LemmaInfos.LemmaPos lemmapos) {
		nodes.append("{\"color\":\"#C0C0C0\",\"size\":1,\"name\":\"")
			.append(lemmapos.toString(" "))
			.append("\",\"href\":\"/index.php/Lemma:")
			.append(lemmapos.toString())
			.append("\"},");
		indices.put(lemmapos, index);
		index++;
	}

	public void add(String word) {
		add(new LemmaInfos.LemmaPos(word));
	}

	public void add(Collection<String> words) {
		for (String word : words) {
			add(word);
		}
	}

	public void add(String words[]) {
		for (String word : words) {
			add(word);
		}
	}

	public void add(LemmaInfos.LemmaPos source, LemmaInfos.LemmaPos target, double value) {
		Integer sourceIndex = indices.get(source);
		Integer targetIndex = indices.get(target);
		if (sourceIndex != null && targetIndex != null) {
			edges.append("{\"source\":")
				.append(sourceIndex)
				.append(",\"value\":")
				.append(value)
				.append(",\"target\":")
				.append(targetIndex)
				.append("},");
		} else {
			throw new IllegalArgumentException("source and target have to be added first");
		}
	}

	public void add(String source, String target, double value) {
		add(new LemmaInfos.LemmaPos(source), new LemmaInfos.LemmaPos(target), value);
	}

	public void clear() {
		nodes.replace(0, nodes.length(), "");
		edges.replace(0, edges.length(), "");
		indices = new HashMap<LemmaInfos.LemmaPos,Integer>();
		index = 0;
	}

	public String end() {
		StringBuffer text = new StringBuffer();
		text.append("<div class=\"mw-collapsible\">")
			.append("<div class=\"graph\" style=\"border:1px solid black;height:500px;width:800px\">{\"nodes\":[");
		if (nodes.length() > 0) {
			nodes.replace(nodes.length() - 1, nodes.length(), "");
		}
		text.append(nodes.toString())
			.append("],\"edges\":[");
		if (edges.length() > 0) {
			edges.replace(edges.length() - 1, edges.length(), "");
		}
		text.append(edges.toString())
			.append("]}</div>\n</div>\n");
		return text.toString();
	}

	public void start(LemmaInfos.LemmaPos center) {
		clear();
		if (center != null) {
			nodes.append("{\"color\":\"#FD482F\",\"size\":2,\"name\":\"")
				.append(center.toString(" "))
				.append("\",\"href\":\"/index.php/Lemma:")
				.append(center.toString())
				.append("\"},");
			indices.put(center, index);
			index++;
		}
	}

}
