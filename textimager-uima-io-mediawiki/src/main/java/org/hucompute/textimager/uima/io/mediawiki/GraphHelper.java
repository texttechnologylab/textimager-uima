package org.hucompute.textimager.uima.io.mediawiki;

import java.util.HashMap;

/** A helper class to build graphs from Word2Vec data. */
public class GraphHelper {
	
	StringBuilder nodes = new StringBuilder();
	StringBuilder edges = new StringBuilder();
	HashMap<LemmaInfos.LemmaPos,Integer> indices = new HashMap<LemmaInfos.LemmaPos,Integer>();
	int index = 0;

	/** Create an empty graph. Use start() to start it. */
	public GraphHelper() {
	}

	/** Create a graph with an initial node. */
	public GraphHelper(LemmaInfos.LemmaPos center) {
		start(center);
	}

	/** Add a node and create edges to every existing node. */
	public void add(LemmaInfos.LemmaPos lemmapos, Word2VecHelper word2Vec) {
		// add the node
		nodes.append("{\"color\":\"#C0C0C0\",\"size\":1,\"name\":\"")
			.append(lemmapos.toString(" "))
			.append("\",\"href\":\"/index.php/Lemma:")
			.append(lemmapos.toString())
			.append("\"},");

		// add edges to all contained nodes
		for (HashMap.Entry<LemmaInfos.LemmaPos,Integer> entry : indices.entrySet()) {
			edges.append("{\"source\":")
				.append(entry.getValue())
				.append(",\"value\":")
				.append(word2Vec.getSimilarity(entry.getKey(), lemmapos))
				.append(",\"target\":")
				.append(index)
				.append("},");
		}
		indices.put(lemmapos, index);
		index++;
	}

	/** Clear this object to start a new graph. */
	public void clear() {
		nodes.replace(0, nodes.length(), "");
		edges.replace(0, edges.length(), "");
		indices = new HashMap<LemmaInfos.LemmaPos,Integer>();
		index = 0;
	}

	/** Return a text representation of the full graph. */
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

	/** Start a new graph with an initial node. */
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
