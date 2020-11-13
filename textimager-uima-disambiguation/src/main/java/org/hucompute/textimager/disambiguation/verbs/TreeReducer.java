package org.hucompute.textimager.disambiguation.verbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GmlExporter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;

public class TreeReducer {
	
	public static final String GROUP_PREFIX = "g_";
	public static final String MEMBER_PREFIX = "m_";
	public static final String MEMBER = "is-member";
	public static final String CHILD = "is-child";
	
	private Graph<GraphVertex, GraphEdge> graph = new DefaultDirectedGraph<GraphVertex, GraphEdge>(GraphEdge.class);
	private HashMap<String, ArrayList<GraphVertex>> grouping = new HashMap<String, ArrayList<GraphVertex>>();
	private HashSet<GraphVertex> senseSet = new HashSet<GraphVertex>();
	private HashMap<String, GraphVertex> labelref = new HashMap<String, GraphVertex>();
	
	
	public void loadgnet(GermaNet gnet) {
		System.out.println("Loading GermaNet into graph");
		int counter = 1;
		
		int lexcounter = 0;
		
		for (Synset syn : gnet.getSynsets(WordCategory.verben)) {
			if (counter % 1000 == 0) System.out.println("Processed " + counter + " Synsets");
			counter += 1;
			
			GraphVertex vertex = new GraphVertex(GROUP_PREFIX + Integer.toString(syn.getId()), syn);
			boolean vert_new = graph.addVertex(vertex);
			if (vert_new) labelref.put(GROUP_PREFIX + Integer.toString(syn.getId()), vertex);
			else vertex = labelref.get(GROUP_PREFIX + Integer.toString(syn.getId()));
			
			List<Synset> parents = syn.getRelatedSynsets(ConRel.has_hypernym);
			for (Synset parent : parents) {
				String id = GROUP_PREFIX + Integer.toString(parent.getId());
				GraphVertex newvert = new GraphVertex(id, parent);
				boolean added = graph.addVertex(newvert);
				if (added) labelref.put(id, newvert);
				else newvert = labelref.get(id);
				graph.addEdge(vertex, newvert, new GraphEdge(CHILD));
			}
			
			List<Synset> children = syn.getRelatedSynsets(ConRel.has_hyponym);
			for (Synset child : children) {
				String id = GROUP_PREFIX + Integer.toString(child.getId());
				GraphVertex newvert = new GraphVertex(id, child);
				boolean added = graph.addVertex(newvert);
				
				if (added) labelref.put(id, newvert);
				else newvert = labelref.get(id);
				graph.addEdge(newvert, vertex, new GraphEdge(CHILD));
			}
			
			List<LexUnit> senses = syn.getLexUnits();
			for (LexUnit sense : senses) {
				//if (gnet.getLexUnits(sense.getOrthForm()).size() < 2) continue;
				lexcounter += 1;
				String id = MEMBER_PREFIX + Integer.toString(sense.getId());
				GraphVertex newvert = new GraphVertex(id, sense);
				boolean added = graph.addVertex(newvert);
				
				if (added) {
					graph.addEdge(newvert, vertex, new GraphEdge(MEMBER));
					senseSet.add(newvert);
					labelref.put(id, newvert);
					String lemma = sense.getOrthForm();
					ArrayList<GraphVertex> senseset = new ArrayList<GraphVertex>();
					if (grouping.containsKey(lemma)) {
						senseset = grouping.get(lemma);
					}
					senseset.add(newvert);
					grouping.put(lemma, senseset);
				}
			}
		}
		
		System.out.println("Finished importing GermaNet! The graph contains " + lexcounter + " ambiguous senses");
	}

	public void loadgnet(String gnetpath) throws FileNotFoundException, XMLStreamException, IOException {
		System.out.println("Opening GermaNet");
		GermaNet gnet = new GermaNet(gnetpath);
		loadgnet(gnet);
	}
	
	public Set<GraphVertex> senseSet() {
		return senseSet;
	}
	
	public void resetMarks() {
		for (GraphVertex vertex : graph.vertexSet()) {
			vertex.setMark(MARK.PLAIN);
		}
	}
	
	public void reattach(GraphVertex source, GraphVertex target_old, GraphVertex target_new, String edge) {
		graph.removeEdge(source, target_old);
		graph.addEdge(source, target_new, new GraphEdge(edge));
	}
	
	
	public void reduce() {
		
		System.out.println("Reducing graph!");
		
		int counter = 0;
		int max = grouping.entrySet().size();
		
		for (Map.Entry<String, ArrayList<GraphVertex>> entry : grouping.entrySet()) {
			counter += 1;
			if (counter % 1000 == 0) {
				System.out.println("Processed " + counter + " of " + max + " groups");
			}
			resetMarks();
			ArrayList<GraphVertex> groupnodes = entry.getValue();
			
			// mark graph
			for (GraphVertex node : groupnodes) {
				// Check direct parents are ok
				for (GraphVertex parent : node.getParents()) {
					if (parent != null && (parent.getMark().equals(MARK.TOUCHED) || parent.getMark().equals(MARK.CONFLICT))) {
						parent.setMark(MARK.CONFLICT);
						continue;
					}
				}
				
				ArrayDeque<GraphVertex> to_process = new ArrayDeque<GraphVertex>();
				to_process.addAll(node.getParents());
				
				while (!to_process.isEmpty()) {
					GraphVertex cur_node = to_process.poll();
					if (cur_node.getMark().equals(MARK.PLAIN)) {
						cur_node.setMark(MARK.TOUCHED);
					}
					else if (cur_node.getMark().equals(MARK.TOUCHED)) {
						cur_node.setMark(MARK.CONFLICT);
					}
					to_process.addAll(cur_node.getParents());
				}
			}
			
			
			// reattach lexunit nodes
			for (GraphVertex node : groupnodes) {
				ArrayList<GraphVertex> parents = new ArrayList<GraphVertex>();
				for (GraphVertex parent : node.getParents()) {
					if (!parent.getMark().equals(MARK.CONFLICT)) parents.add(parent);
				}
				
				for (GraphVertex parent : parents) {
					ArrayDeque<GraphVertex> to_process = new ArrayDeque<GraphVertex>();
					to_process.add(parent);
					while (!to_process.isEmpty()) {
						GraphVertex cur_node = to_process.poll();
						for (GraphVertex next_node : cur_node.getParents()) {
							if (next_node.getParents().isEmpty() || next_node.getMark().equals(MARK.CONFLICT)) {
								reattach(node, parent, cur_node, MEMBER);
							} else {
								to_process.add(next_node);
							}
						}
					}
				}
			}
		}
		
		// Clean-up synsets without member senses by removing them
		ArrayList<GraphVertex> to_remove = new ArrayList<GraphVertex>();
		for (GraphVertex vertex : graph.vertexSet()) {
			Set<GraphVertex> members = vertex.getMembers();
			if (vertex.isGroup() && members.isEmpty()) {
				Set<GraphVertex> children = vertex.getChildren();
				if (children.isEmpty()) to_remove.add(vertex);
				if (vertex.getParents().isEmpty()) continue;
				for (GraphVertex parent : vertex.getParents()) {
					for (GraphVertex child : children) {
						reattach(child, vertex, parent, CHILD);
					}
				}
				to_remove.add(vertex);
			}
		}
		for (GraphVertex vertex : to_remove) {
			graph.removeVertex(vertex);
		}
		System.out.println("Finished reduction!");
	}
	
	public String map(String label) {
		String new_label = null;
		if (labelref.containsKey(MEMBER_PREFIX + label)) {
			// TODO: Think about what to do with senses contained in multiple synsets
			new_label = labelref.get(MEMBER_PREFIX + label).getParents().get(0).getLabel().replace(GROUP_PREFIX, "");
		}
		return new_label;
	}
	
	public String reverseMap(String group, String label) {
		String old_label = null;
		if (labelref.containsKey(GROUP_PREFIX + label)) {
			GraphVertex syn = labelref.get(GROUP_PREFIX + label);
			for (GraphVertex sense: syn.getMembers()) {
				if (grouping.get(group).contains(sense)) {
					old_label = sense.getLabel().replace(MEMBER_PREFIX, "");
				}
			}
		}
		return old_label;
	}
	
	public void exportMappings(String filename) throws IOException {
		System.out.println("Exporting Mapping");
		ArrayList<String> outlines = new ArrayList<String>();
		outlines.add("old\tnew\tgroup");
		for (String group: grouping.keySet()) {
			List<GraphVertex> members = grouping.get(group);
			for (GraphVertex member : members) {
				outlines.add(member.getLabel().replace(MEMBER_PREFIX, "") + "\t" + 
								map(member.getLabel().replace(MEMBER_PREFIX, "")) + "\t" + 
								group);
			}
		}
		FileUtils.writeLines(new File(filename), outlines);
	}
	
	public void exportLemmaIds(String filename) throws IOException {
		System.out.println("Exporting VerbLemmaIds");
		ArrayList<String> outlines = new ArrayList<String>();
		for (String group: grouping.keySet()) {
			List<GraphVertex> members = grouping.get(group);
			outlines.add(group + "\t" + StringUtils.join(members.stream().map(x -> map(x.getLabel().replace(MEMBER_PREFIX, ""))).collect(Collectors.toList()), "\t"));
		FileUtils.writeLines(new File(filename), outlines);
		}
	}
	
	public HashMap<String, HashSet<String>> getLemmaIds() {
		HashMap<String, HashSet<String>> out = new HashMap<String, HashSet<String>>();
		for (String group: grouping.keySet()) {
			HashSet<String> senses = new HashSet<String>();
			senses.addAll(grouping.get(group).stream().map(x -> map(x.getLabel().replace(MEMBER_PREFIX, ""))).collect(Collectors.toSet()));
			out.put(group, senses);
		}
		return out;
	}
	
	public GraphVertex getVertByLabel(String label) {
		if (labelref.containsKey(label)) {
			return labelref.get(label);
		} else {
			throw new NullPointerException();
		}
	}

	
	public static void exportGraphSized(String filename, Graph<GraphVertex, GraphEdge> outgraph, HashMap<GraphVertex, Integer> sizes) throws IOException {
		FileWriter w = new FileWriter(filename);
		int size = 30; //default size
		String shape = "ellipse";
		w.write("Creator\t\"yFiles\"\n");
		w.write("Version\t\"2.16\"\n");
		w.write("graph\n");
		w.write("[\n");
		w.write("\thierarchic\t1\n");
		w.write("\tlabel\t\"\"\n");
		w.write("\tdirected\t1\n");
		
		int i = 0;
		HashMap<GraphVertex, Integer> ids = new HashMap<GraphVertex, Integer>();
		for (GraphVertex node : outgraph.vertexSet()) {
			ids.put(node, i);
			w.write("\tnode\n");
			w.write("\t[\n");
			w.write("\t\tid\t" + i + "\n");
			w.write("\t\tlabel\t\"" + node.toString() +"\"\n");
			if (sizes != null && sizes.containsKey(node) && sizes.get(node) != null) size = sizes.get(node);
			w.write("\t\tgraphics\n");
			w.write("\t\t[\n");
			w.write("\t\t\tw\t" + size + "\n");
			w.write("\t\t\th\t" + size + "\n");
			w.write("\t\t\ttype\t\"" + shape + "\"\n");
			w.write("\t\t]\n");
			w.write("\t]\n");
			i += 1;
		}
		
		for (GraphEdge edge : outgraph.edgeSet()) {
			int source_id = ids.get(outgraph.getEdgeSource(edge));
			int target_id = ids.get(outgraph.getEdgeTarget(edge));
			String label = edge.toString();
			w.write("\tedge\n");
			w.write("\t[\n");
			w.write("\t\tsource\t" + source_id + "\n");
			w.write("\t\ttarget\t" + target_id + "\n");
			w.write("\t\tlabel\t\"" +label+ "\"\n");
			w.write("\t]\n");
		}
		
		w.write("]");
		w.flush();
		w.close();
		
	}
	
	public static GraphVertex getRoot(Graph<GraphVertex, GraphEdge> outgraph) {
		GraphVertex root = null;
		for (GraphVertex vert : outgraph.vertexSet()) {
			if (outgraph.outDegreeOf(vert) == 0 ) {
				root = vert;
				break;
			}
		}
		return root;
	}
	
	private static JSONObject process_vert(Graph<GraphVertex, GraphEdge> outgraph, GraphVertex vert) {
		JSONObject json = new JSONObject();
		json.put("name", vert.toString());
		JSONArray children = new JSONArray();
		for (GraphEdge edge : outgraph.incomingEdgesOf(vert)) {
			GraphVertex child = outgraph.getEdgeSource(edge);
			children.put(process_vert(outgraph, child));
		}
		if (!children.isEmpty())
			json.put("children", children);
		return json;
	}
	
	public static JSONObject exportGraphJSON(Graph<GraphVertex, GraphEdge> outgraph) {
		GraphVertex root = getRoot(outgraph);
		return process_vert(outgraph, root);
	}
	
	public static void exportGraph(String filename, Graph<GraphVertex, GraphEdge> outgraph) throws IOException, ExportException {
		System.out.println("Exporting Graph");
		GmlExporter<GraphVertex, GraphEdge> exporter = new GmlExporter<GraphVertex, GraphEdge>();
		FileWriter w = new FileWriter(filename);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
		exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
		exporter.exportGraph(outgraph, w);
	}
	
	public void exportGraph(String filename) throws IOException, ExportException {
		exportGraph(filename, graph);
	}
	
	
	public Graph<GraphVertex, GraphEdge> subgraph(Set<GraphVertex> leafs) {
		// hinschlagen, trennen raffen
		HashSet<GraphVertex> subset = new HashSet<GraphVertex>();
		for (GraphVertex leaf : leafs) {
			subset.add(leaf);
			ArrayDeque<GraphVertex> to_process = new ArrayDeque<GraphVertex>();
			to_process.addAll(leaf.getParents());
			while (!to_process.isEmpty()) {
				leaf = to_process.poll();
				subset.add(leaf);
				for (GraphVertex parent : leaf.getParents())
					to_process.add(parent);
			}
		}
		Graph<GraphVertex, GraphEdge> subgraph = new AsSubgraph<GraphVertex, GraphEdge>(graph, subset);
		return subgraph;
	}
	
	public Graph<GraphVertex, GraphEdge> subgraph(List<String> labels) {
		HashSet<GraphVertex> subset = new HashSet<GraphVertex>();
		for (String label : labels) {
			if (labelref.containsKey(label)) {
				subset.add(labelref.get(label));
			}
		}
		return subgraph(subset);
	}
	
	public Graph<GraphVertex, GraphEdge> subgraphVerbs(List<String> verbs) {
		HashSet<GraphVertex> leafs = new HashSet<GraphVertex>();
		for (String verb : verbs) {
			leafs.addAll(grouping.get(verb));
		}
		return subgraph(leafs);
	}
	
	public static enum MARK {
		PLAIN,
		TOUCHED,
		CONFLICT
	};
	
	
	public class GraphVertex {

		private MARK mark;
		
		private String label;
		
		private final LexUnit lex;
		private final Synset syn;
		
		private GraphVertex(String newlabel, Synset syn, LexUnit lex) {
			this.label = newlabel;
			this.syn = syn;
			this.lex = lex;
		}
		
		public GraphVertex(String newlabel, LexUnit lex) {
			this(newlabel, null, lex);
		}
		
		public GraphVertex(String newlabel, Synset syn) {
			this(newlabel, syn, null);
		}

		public String getLabel() {
			return label;
		}
			
		public void setLabel(String label) {
			this.label = label;
		}
		
		public MARK getMark() {
			return mark;
		}
		
		public void setMark(MARK newmark) {
			this.mark = newmark;
		}
		
		public boolean isMember() {
			return (label.startsWith(MEMBER_PREFIX));
		}
		
		public boolean isGroup() {
			return (label.startsWith(GROUP_PREFIX));
		}
		
		public Synset getSynset() {
			return syn;
		}
		
		public LexUnit getSense() {
			return lex;
		}
		
		public ArrayList<GraphVertex> getParents() {
			ArrayList<GraphVertex> parents = new ArrayList<GraphVertex>();
			Set<GraphEdge> edges = graph.outgoingEdgesOf(this);
			for (GraphEdge edge : edges) {
				parents.add(graph.getEdgeTarget(edge));
			}
			return parents;
		}
		
		public Set<GraphVertex> getChildren() {
			HashSet<GraphVertex> children = new HashSet<GraphVertex>();
			Set<GraphEdge> edges = graph.incomingEdgesOf(this);
			for (GraphEdge edge : edges) {
				if (CHILD.equals(edge.getLabel())) {
					children.add(graph.getEdgeSource(edge));
				}
			}
			return children;
		}
		
		public Set<GraphVertex> getMembers() {
			HashSet<GraphVertex> members = new HashSet<GraphVertex>();
			Set<GraphEdge> edges = graph.incomingEdgesOf(this);
			for (GraphEdge edge : edges) {
				if (MEMBER.equals(edge.getLabel()))
					members.add(graph.getEdgeSource(edge));
			}
			return members;
		}
		
		public String toString() {
			if (lex != null) {
				return lex.getOrthForm() + ": " + label.replace(MEMBER_PREFIX, "");
			}
			if (syn != null) {
				return syn.getAllOrthForms().toString().replace("[", "").replace("]", "")+ ": " + label.replace(GROUP_PREFIX, "");
			}
			return label;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GraphVertex other = (GraphVertex) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			return true;
		}
	}
	
	public static class GraphEdge extends DefaultEdge {
		
		private String label;
		
		public GraphEdge(String label) {
			this.label = label;
		}
		
		public String toString() {
			return label + "-" + getSource() + "-" + getTarget();
		}
		
		public String getLabel() {
			return label;
		}
		
	}
	
//	public static void main(String...args) throws FileNotFoundException, XMLStreamException, IOException, ExportException {
//		TreeReducer tr = new TreeReducer();
//		tr.loadgnet("/resources/nlp/models/disambig/verbs/GN_V140_verbs");
//		tr.exportGraph("gnet.gml");
//		ArrayList<String> subgraphlabels = new ArrayList<String>();
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "73510");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "75522");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "76529");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "79474");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "79740");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "79800");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "83425");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "83445");
//		subgraphlabels.add(TreeReducer.MEMBER_PREFIX + "84013");
//		
//		tr.exportGraph("subgraph.gml", tr.subgraph(subgraphlabels));
//		tr.reduce();
//		tr.exportGraph("gnet_reduced.gml");
//		tr.exportGraph("subgraph_reduced.gml", tr.subgraph(subgraphlabels));
//		//tr.exportGraphSized("subgraph_reduced_sized.gml", tr.subgraph(subgraphlabels), null);
//		//tr.exportMappings("reducermappings");
//		
//	}
}
