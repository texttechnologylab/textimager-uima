package org.hucompute.textimager.disambiguation.verbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense;
import de.tuebingen.uni.sfs.germanet.api.Frame;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;

public class FrameEvaluator extends JCasAnnotator_ImplBase {
	
	public static final String PARAM_GERMANET_PATH = "gnetPath";
    @ConfigurationParameter(name = PARAM_GERMANET_PATH, mandatory = true, description = "The germanet directory")
    private String gnetPath;
    
	public static final String PARAM_CRITERIA = "strict";
    @ConfigurationParameter(name = PARAM_CRITERIA, mandatory = false, description = "Whether to use strict frame matching or not", defaultValue = "true")
    private boolean strict = true;
    
    public static final String PARAM_VERBOSE = "verbose";
    @ConfigurationParameter(name = PARAM_VERBOSE, mandatory = false, description = "Whether to use verbose console output or not", defaultValue = "false")
	private boolean verbose = false;
    

	private GermaNet gnet;
	private HashMap<String, HashSet<String>> senseInventory;
	private HashMap<String, HashSet<String>> senseCriteria;
	private HashMap<String, Set<String>> coveredVerbs;
	private static HashSet<String> implementedFrames = new HashSet<String>(Arrays.asList(
			"NN", "AN", "DN", "AR", "DR", "PP"));
	

	// Implement only frames which actually exist in the unique sets: NN=4574, AN=1741, AR=895, PP=720, DN=327, BD=269, DS=191, AZ=182, BL=159, DR=114, BM=102, NE=99, FS=65, BR=32, BS=26, BO=25, BT=18, GN=14, NG=12, AI=9, BC=5

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		// Set up lemmatizer, pos, morph taggers
		try {

			senseInventory = new HashMap<String, HashSet<String>>();
			senseCriteria = new HashMap<String, HashSet<String>>();
			gnet = new GermaNet(gnetPath);
			
			
			// TODO: Maybe build criteria for all senses here, instead of during predict call? Fewer duplicated operations?
			// TODO: senseInventory und senseCriteria are misnamed, are simply duplicates of germanet inventory
			// TODO: MergeMap?
			// TODO: Throw exceptions when init fails, due to failure to load gnet
			for (LexUnit lex: gnet.getLexUnits(WordCategory.verben)) {
				if (gnet.getLexUnits(lex.getOrthForm()).size() == 1) {
					continue;
				}

				String lemma = lex.getOrthForm();
				String id = String.valueOf(lex.getId());

				HashSet<String> frames = new HashSet<String>();
				if (senseCriteria.containsKey(id)) {
					frames = senseCriteria.get(id);
				}
				for (Frame frame: lex.getFrames()) {
					String data = frame.getData();
					frames.add(data);
				}
				if (!frames.isEmpty())senseCriteria.put(id, frames);


				HashSet<String> senses = new HashSet<String>();
				if (senseInventory.containsKey(lemma)) {
					senses = senseInventory.get(lemma);
				} 
				if (senseCriteria.containsKey(id)) senses.add(id);
				if (!senses.isEmpty()) senseInventory.put(lemma, senses);
			}

			coveredVerbs = findCoveredVerbs(true);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public GermaNet getGNet() {
		return gnet;
	}
	
	
	private HashSet<String> generateFramesFromTree(HashMap<Token, ArrayList<Token>> childMap, List<Token> p_to_root) {
		HashSet<String> frames = new HashSet<String>();
		HashSet<Token> toProcess = new HashSet<Token>();
		if (p_to_root.isEmpty()) return null;
		if (p_to_root.size() == 1 && !childMap.containsKey(p_to_root.get(0))) return null;
		for (Token t : p_to_root) {
			if (childMap.containsKey(t)) toProcess.addAll(childMap.get(t));
			toProcess.add(t);
		}
		while (!toProcess.isEmpty()) {
			Token child = toProcess.iterator().next();
			Dependency dep = JCasUtil.selectCovered(Dependency.class, child).get(0);
			String depType = dep.getDependencyType();
			String pos = child.getPos().getPosValue();
			if (depType.equals("SB") && (pos.equals("NN") || pos.equals("NE") || pos.equals("PPER") || pos.equals("PDS") || pos.equals("PIS") || pos.equals("PIAT"))) {
				frames.add("NN");
			} else if (depType.equals("DA") && (pos.equals("NN") || pos.equals("NE") || pos.equals("PPER") || pos.equals("PDS") || pos.equals("PIS") || pos.equals("PIAT"))) {
				frames.add("DN");
			} else if (pos.equals("PRF") && p_to_root.contains(dep.getGovernor())) {
				String[] morphs = child.getMorph().getValue().split("\\|");
				String ccase = null;
				for (String arg: morphs) {
					if (arg.startsWith("case")) {
						ccase = arg.split("=")[1];
					}
				}
				if (ccase != null && ccase.equals("dat")) {
					frames.add("DR");
				} else {
					frames.add("AR");
				}
			} else if (depType.equals("OA") && (pos.equals("NN") || pos.equals("NE") || pos.equals("PPER") || pos.equals("PDS") || pos.equals("PIS") || pos.equals("PIAT"))) {
				frames.add("AN");
			} else if (depType.equals("OP") && (pos.equals("APPR") || pos.equals("APPRART") || pos.equals("APPO"))) {
				frames.add("PP");
			}
			toProcess.remove(child);
		}
		return frames;
	}



	public HashSet<String> generateFrames(Sentence sentence, String target, boolean verbose) {
		HashSet<String> frames = new HashSet<String>();

		// Build Child tree
		HashMap<Token, ArrayList<Token>> childMap = new HashMap<Token, ArrayList<Token>>();

		Token root = null;
		Token target_t = null;
		for (Token token: JCasUtil.selectCovered(Token.class, sentence)) {
			Dependency dep = JCasUtil.selectCovered(Dependency.class, token).get(0);
			Token governour = dep.getGovernor();
			String isroot = "0";
			if (governour.equals(token)) {
				root = token;
				isroot = "1";
			}
			String istarget = "0";
			if (token.getLemma().getValue().equals(target))  {
				target_t = token;
				istarget = "1";
			}
			if (verbose) System.out.println(token.getCoveredText() + "\t" + governour.getCoveredText() + "\t" + dep.getDependencyType() + "\t" + isroot + "\t" + token.getPos().getPosValue() + "\t" + token.getMorph().getValue() + "\t" + token.getLemma().getValue() + "\t" + istarget);
			if (governour.equals(token)) continue;
			ArrayList<Token> children = new ArrayList<Token>();
			if (childMap.containsKey(governour)) children = childMap.get(governour);
			children.add(token);
			childMap.put(governour, children);
		}

		if (target_t == null || root == null) return null; // Lemmatization error, couldn't find target word or the dep tree is malformed

		ArrayList<Token> p_to_root = new ArrayList<Token>();
		p_to_root.add(target_t);
		if (!target_t.getPos().getPosValue().equals("VAFIN") && !target_t.getPos().getPosValue().equals("VVFIN")) {
			Token sent_root = target_t;
			while (!sent_root.getPos().getPosValue().equals("VAFIN") && !target_t.getPos().getPosValue().equals("VVFIN")) {
				Token sent_root_it = JCasUtil.selectCovered(Dependency.class, sent_root).get(0).getGovernor();
				if (sent_root_it.equals(sent_root)) return null;
				sent_root = sent_root_it;
				p_to_root.add(sent_root);
			}
			if (verbose) System.out.println(p_to_root.stream().map(x -> x.getCoveredText()).collect(Collectors.joining("\t")));
		}
		frames = generateFramesFromTree(childMap, p_to_root);
		return frames;
	}


	public HashMap<String, HashSet<Set<String>>> getCandidateCriteria(String target, boolean strict) {
		HashMap<String, HashSet<Set<String>>> senseFrameMap_uniques = getUniques(target, true);
		HashMap<String, HashSet<Set<String>>> outMap = new HashMap<String, HashSet<Set<String>>>();
		if (strict) {
			outMap = senseFrameMap_uniques;
		} else {
			HashMap<String, HashSet<Set<String>>> powerfragments = new HashMap<String, HashSet<Set<String>>>();
			for (String sense: senseInventory.get(target)) {
				HashSet<Set<String>> powerset = new HashSet<Set<String>>();
				for (String frame: senseCriteria.get(sense)) {
					HashSet<String> fragments = new HashSet<String>();
					for (String frag: frame.toUpperCase().split("\\.")) {
						if (implementedFrames.contains(frag)) fragments.add(frag);
					}
					Set<Set<String>> tmp = Sets.powerSet(fragments);
					for (Set<String> set: tmp) {
						if (set.isEmpty()) continue;
						powerset.add(set);
					}
				}
				powerfragments.put(sense, powerset);

			}

			for (String sense: senseFrameMap_uniques.keySet()) {
				HashSet<Set<String>> criteria = new HashSet<Set<String>>();
				for (Set<String> frame: senseFrameMap_uniques.get(sense)) {
					criteria.add(frame);
					Set<Set<String>> powerset_unique = Sets.powerSet(frame);
					for (Set<String> set: powerset_unique) {
						boolean unique = true;
						for (String comp_sense: powerfragments.keySet()) {
							if (comp_sense.equals(sense)) continue;
							for (Set<String> comp_frame: powerfragments.get(comp_sense)) {
								if (set.equals(comp_frame)) {
									unique = false;
									break;
								}
							}
							if (!unique) break;
						}
						if (unique && !set.isEmpty()) {
							criteria.add(set);
						}
					}
				}
				outMap.put(sense, criteria);
			}

		}
		return outMap;
	}

	
	public void addAnnotation(JCas cas, Token token, String annotation) {
		WordSense sense = new WordSense(cas, token.getBegin(), token.getEnd());
		sense.setValue(annotation);
		sense.addToIndexes();
	}
	
	// docId of null means that we are doing a test run and don't want to load a cached cas. 
	// We still write a cas with id "test", but it will not be loaded and will be overwritten when we do another test run.
	public void process(JCas cas) {
		
		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
			List<Dependency>svps = new ArrayList<>();
			for (Dependency dependency : JCasUtil.selectCovered(Dependency.class, sentence)) {
				if(dependency.getDependencyType().equals("SVP"))
					svps.add(dependency);
			}
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				String lemma = token.getLemma().getValue();
				for (Dependency dependency : svps) {
					if(dependency.getGovernor().equals(token)){
						lemma = dependency.getDependent().getLemma().getValue()+lemma;
						token.getLemma().setValue(lemma);
					}
				}
				if(gnet.getLexUnits(lemma, WordCategory.verben).size() == 1){
					addAnnotation(cas, token, Integer.toString(gnet.getLexUnits(lemma, WordCategory.verben).get(0).getId()));
					continue;
				}
				if(token.getPos().getClass() == POS_VERB.class && coveredVerbs.containsKey(lemma)){
					String label = null;

					HashSet<String> sentence_frames = generateFrames(sentence, lemma, verbose);
					if (verbose) System.out.println("Generated frames: " + sentence_frames);
					if (sentence_frames == null || sentence_frames.isEmpty()) continue;

					HashMap<String, HashSet<Set<String>>> criteriaMap = getCandidateCriteria(lemma, strict); 

					HashMap<String, Set<Set<String>>> candidates = new HashMap<String, Set<Set<String>>>();
					for (String sense : criteriaMap.keySet()) {
						for (Set<String> gold_frame : criteriaMap.get(sense)) {
							boolean candidate = true;
							for (String frag: gold_frame) {
								if (!sentence_frames.contains(frag)) {
									candidate = false;
									break;
								}
							}
							if (candidate) {
								Set<Set<String>> candidateframes = new HashSet<Set<String>>();
								if (candidates.containsKey(sense)) candidateframes = candidates.get(sense);
								candidateframes.add(gold_frame);
								candidates.put(sense, candidateframes);
							}
						}
					}
					if (candidates.size() == 1) addAnnotation(cas, token, candidates.keySet().iterator().next());
					
					int overlap = -1;
					for (String candidate: candidates.keySet()) {
						Set<Set<String>> candidate_frames = candidates.get(candidate);
						for (Set<String> frame: candidate_frames) {
							HashSet<String> intersection = new HashSet<String>(sentence_frames);
							intersection.retainAll(frame);
							if (intersection.size() > overlap) {
								label = candidate;
								overlap = intersection.size();
							}
						}
					}
					if (overlap == -1 || label == null) continue;
					addAnnotation(cas, token, label);
				}
			}
		}
	}


	private static Set<String> removeOptionals(Set<String> frame, boolean clean) {
		HashSet<String> toRemove = new HashSet<String>();
		for (String frag : frame) {
			if (frag.startsWith("ZZ")) {
				toRemove.add(frag.split("-")[1]);
			}
		}
		HashSet<String> out = new HashSet<String>();
		for (String frag : frame) {
			if (frag.startsWith("ZZ")) continue;
			if (toRemove.contains(frag) && clean) continue;
			out.add(frag);
		}
		frame = out;
		return frame;
	}


	public HashMap<String, HashSet<Set<String>>> getUniques(String target, boolean implemented) {
		HashMap<String, HashSet<Set<String>>> candidateCriteria = new HashMap<String, HashSet<Set<String>>>();
		HashMap<String, HashSet<Set<String>>> senseFrameMap = new HashMap<String, HashSet<Set<String>>>();

		// Get rid of optionals and filter non implemented
		for (String sense: senseInventory.get(target)) {
			HashSet<Set<String>> frames_altered = new HashSet<Set<String>>();
			for (String frame: senseCriteria.get(sense)) {
				String[] frags = frame.split("\\.");
				HashSet<String> fragments = new HashSet<String>();
				for (String frag : frags) {
					if (!implementedFrames.contains(frag.toUpperCase())) {
						continue;
					}
					fragments.add(frag.toUpperCase());
					if (!frag.toUpperCase().equals(frag)) {
						fragments.add("ZZ-" + frag.toUpperCase());
					}
				}
				if (!fragments.isEmpty()) frames_altered.add(fragments);
			}
			senseFrameMap.put(sense, frames_altered);
		}

		String[] senseArray = senseFrameMap.keySet().toArray(new String[senseFrameMap.keySet().size()]);
		for (int i = 0; i < senseArray.length; i++) {
			String sense = senseArray[i];
			for (Set<String> frame: senseFrameMap.get(sense)) {
				Set<String> r_frame = removeOptionals(frame, false);
				frame = removeOptionals(frame, true);
				boolean unique = true;
				for (int j = 0; j < senseArray.length; j++) {
					if (i == j) continue;
					String comparesense = senseArray[j];

					for (Set<String> compareframe: senseFrameMap.get(comparesense)) {
						Set<String> r_temp = removeOptionals(compareframe, false);
						compareframe = removeOptionals(compareframe, true);
						if (frame.equals(compareframe)) {
							unique = false;
							break;
						}
						if (r_frame.equals(r_temp)) {
							unique = false;
							break;
						}
					}
					if (!unique) break;
				}
				if (unique) {
					HashSet<Set<String>> frames = new HashSet<Set<String>>();
					if (candidateCriteria.containsKey(sense)) frames = candidateCriteria.get(sense);
					frames.add(frame);
					candidateCriteria.put(sense, frames);
				}
			}
		}
		return candidateCriteria;
	}


	public HashMap<String, Set<String>> findCoveredVerbs(boolean implemented) {
		HashMap<String, Set<String>> lemmas_with_uniques = new HashMap<String, Set<String>>();
		for (String lemma: senseInventory.keySet()) {
			HashMap<String, HashSet<Set<String>>> senseFrameMap = getUniques(lemma, implemented);
			if (!senseFrameMap.isEmpty()) lemmas_with_uniques.put(lemma, senseFrameMap.keySet());
		}
		return lemmas_with_uniques;
	}


	public HashMap<String, Set<String>> getCoveredVerbs() {
		return coveredVerbs;
	}
}
