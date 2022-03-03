package org.hucompute.textimager.disambiguation.verbs;

import com.google.common.collect.Sets;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tuebingen.uni.sfs.germanet.api.Frame;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FrameEvaluator extends JCasAnnotator_ImplBase {

	public static final String PARAM_GERMANET_PATH = "germanetPath";
	@ConfigurationParameter(name = PARAM_GERMANET_PATH, mandatory = false, description = "The germanet directory")
	private String germanetPath;

	public static final String PARAM_GERMANET = "gnet";
	@ExternalResource(key=PARAM_GERMANET, mandatory = false, description = "You can pass a GermaNet object instead of a path, to avoid loading germanet multiple times")
	private GNetWrapper gnetwrapper;

	public static final String PARAM_CRITERIA = "strict";
	@ConfigurationParameter(name = PARAM_CRITERIA, mandatory = false, description = "Whether to use strict frame matching. Options 'strict', 'superstrict' or anything else", defaultValue = "strict")
	private String strict;

	public static final String PARAM_VERBOSE = "verbose";
	@ConfigurationParameter(name = PARAM_VERBOSE, mandatory = false, description = "Whether to use verbose console output or not", defaultValue = "false")
	private boolean verbose;

	private GermaNet gnet;
	private HashMap<String, HashSet<String>> senseInventory;
	private HashMap<String, HashSet<String>> senseCriteria;
	private HashMap<String, Set<String>> coveredVerbs;
	private HashMap<String, HashSet<Set<String>>> senseFramesUnique;
	private HashMap<String, HashSet<Set<String>>> senseFramesAmbiguous;
	private static HashSet<String> implementedFrames = new HashSet<String>(Arrays.asList(
			"NN", "AR", "DR", "AN", "PP", "DN"));


	// Implement only frames which actually exist in the unique sets: NN=4574, AN=1741, AR=895, PP=720, DN=327, BD=269, DS=191, AZ=182, BL=159, DR=114, BM=102, NE=99, FS=65, BR=32, BS=26, BO=25, BT=18, GN=14, NG=12, AI=9, BC=5

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		// Set up lemmatizer, pos, morph taggers
		try {

			if (gnetwrapper == null) {
				try {
					gnet = new GermaNet(new File(germanetPath));
				} catch (XMLStreamException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				gnet = gnetwrapper.getGnet();
			}

			senseInventory = new HashMap<String, HashSet<String>>();
			senseCriteria = new HashMap<String, HashSet<String>>();



			// TODO: Maybe build criteria for all senses here, instead of during predict call? Fewer duplicated operations?
			// TODO: senseInventory und senseCriteria are misnamed, are simply duplicates of germanet inventory
			// TODO: MergeMap?
			// TODO: Throw exceptions when init fails, due to failure to load gnet
			for (LexUnit lex: gnet.getLexUnits(WordCategory.verben)) {
				if (gnet.getLexUnits(lex.getOrthForm()).size() == 1) {
					continue;
				}

				String lemma = lex.getOrthForm();
				if(lemma.equals("sein")||
						lemma.equals("haben")||
						lemma.equals("werden")||
						lemma.equals("dürfen")||
						lemma.equals("können")||
						lemma.equals("mögen")||
						lemma.equals("müssen")||
						lemma.equals("sollen")||
						lemma.equals("wollen")||
						lemma.equals("machen"))
					continue;
				String id = String.valueOf(lex.getId());

				HashSet<String> frames = new HashSet<String>();
				if (senseCriteria.containsKey(id)) {
					frames = senseCriteria.get(id);
				}
				for (Frame frame: lex.getFrames()) {
					String data = frame.getData();
					frames.add(data);
				}
				if (!frames.isEmpty()) senseCriteria.put(id, frames);


				HashSet<String> senses = new HashSet<String>();
				if (senseInventory.containsKey(lemma)) {
					senses = senseInventory.get(lemma);
				}
				if (senseCriteria.containsKey(id)) senses.add(id);
				if (!senses.isEmpty()) senseInventory.put(lemma, senses);
			}

			senseFramesUnique = new HashMap<String, HashSet<Set<String>>>();
			senseFramesAmbiguous = new HashMap<String, HashSet<Set<String>>>();
			coveredVerbs = new HashMap<String, Set<String>>();
			for (String lemma : senseInventory.keySet()) {
				getUniques(lemma, true, true);
			}

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

			if (depType.equals("NK") || depType.equals("MO")) {
				if (childMap.containsKey(child)) {
					toProcess.addAll(childMap.get(child));
				}
			}
			HashSet<String> subjtags = new HashSet<String>(Arrays.asList("NN", "NE", "PPER", "PDS", "PIS", "PIAT", "PRELS"));
			if (depType.equals("SB") && (subjtags.contains(pos))) {
				frames.add("NN");
			} else if (depType.equals("DA") && (subjtags.contains(pos))) {
				frames.add("DN");
			} else if (pos.equals("PRF") && p_to_root.contains(dep.getGovernor())) {
				String[] morphs = child.getMorph().getValue().split("\\|");
				String ccase = null;
				for (String arg: morphs) {
					if (arg.startsWith("case")) {
						ccase = arg.split("=")[1];
					}
				}
				if ((ccase != null && ccase.equals("dat")) || depType.equals("DA")) {
					frames.add("DR");
				} else {
					frames.add("AR");
				}
			} else if (depType.equals("OA") && (subjtags.contains(pos))) {
				frames.add("AN");
			} else if ((depType.equals("OP") || ((Constituent)child.getParent()).getConstituentType().equals("PP")) && (pos.equals("APPR") || pos.equals("APPRART") || pos.equals("APPO"))) {
				frames.add("PP");
			}
			toProcess.remove(child);
		}
		return frames;
	}


	public HashMap<Token, ArrayList<Token>> preprocSentence(Sentence sentence, boolean verbose) {
		HashMap<Token, ArrayList<Token>> childMap = new HashMap<Token, ArrayList<Token>>();

		Token root = null;
		for (Token token: JCasUtil.selectCovered(Token.class, sentence)) {
			Dependency dep = JCasUtil.selectCovered(Dependency.class, token).get(0);
			Token governour = dep.getGovernor();
			String isroot = "0";
			if (governour.equals(token)) {
				root = token;
				isroot = "1";
			}
			if (verbose) System.out.println(token.getCoveredText() + "\t" + governour.getCoveredText() + "\t" + dep.getDependencyType() + "\t" + isroot + "\t" + token.getPos().getPosValue() + "\t" + token.getMorph().getValue() + "\t" + token.getLemma().getValue());
			if (governour.equals(token)) continue;
			ArrayList<Token> children = new ArrayList<Token>();
			if (childMap.containsKey(governour)) children = childMap.get(governour);
			children.add(token);
			childMap.put(governour, children);
		}

		if (root == null) return null; // Lemmatization error, couldn't find target word or the dep tree is malformed
		return childMap;
	}


	public HashSet<String> generateFrames(Sentence sentence, String target, HashMap<Token, ArrayList<Token>> childMap, boolean verbose) {
		HashSet<String> frames = new HashSet<String>();

		Token root = null;
		Token target_t = null;
		for (Token token: JCasUtil.selectCovered(Token.class, sentence)) {
			Dependency dep = JCasUtil.selectCovered(Dependency.class, token).get(0);
			Token governour = dep.getGovernor();
			if (governour.equals(token)) {
				root = token;
			}
			if (token.getLemma().getValue().equals(target))  {
				target_t = token;
			}
		}

		if (target_t == null || root == null) return null; // Lemmatization error, couldn't find target word or the dep tree is malformed

		ArrayList<Token> p_to_root = new ArrayList<Token>();
		p_to_root.add(target_t);
		if (!(target_t.getPos().getPosValue().equals("VAFIN") || target_t.getPos().getPosValue().equals("VVFIN"))) {
			Token sent_root = target_t;
			while (!(sent_root.getPos().getPosValue().equals("VAFIN") || sent_root.getPos().getPosValue().equals("VVFIN"))) {
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


	public HashMap<String, HashSet<Set<String>>> getCandidateCriteria(String target, String strict) {
		HashMap<String, HashSet<Set<String>>> senseFrameMap_uniques = new HashMap<String, HashSet<Set<String>>>();
		for (String sense : senseInventory.get(target)) {
			senseFrameMap_uniques.put(sense, senseFramesUnique.get(sense));
		}

		HashMap<String, HashSet<Set<String>>> outMap = new HashMap<String, HashSet<Set<String>>>();
		if ("strict".equals(strict) || "superstrict".equals(strict)) {
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
		if(JCasUtil.selectCovered(WordSense.class, token).size() == 0){
			WordSense sense = new WordSense(cas, token.getBegin(), token.getEnd());
			sense.setValue(annotation);
			sense.addToIndexes();
		}
	}

	// docId of null means that we are doing a test run and don't want to load a cached cas.
	// We still write a cas with id "test", but it will not be loaded and will be overwritten when we do another test run.
	public void process(JCas cas) {

		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
//			List<Dependency>svps = new ArrayList<>();
//			for (Dependency dependency : JCasUtil.selectCovered(Dependency.class, sentence)) {
//				if(dependency.getDependencyType().equals("SVP"))
//					svps.add(dependency);
//			}

			HashMap<Token, ArrayList<Token>> depTree = preprocSentence(sentence, verbose);

			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				String lemma = token.getLemma().getValue();
//				for (Dependency dependency : svps) {
//					if(dependency.getGovernor().equals(token)){
//						lemma = dependency.getDependent().getLemma().getValue()+lemma;
//						token.getLemma().setValue(lemma);
//					}
//				}
				if(gnet.getLexUnits(lemma, WordCategory.verben).size() == 1){
					addAnnotation(cas, token, Integer.toString(gnet.getLexUnits(lemma, WordCategory.verben).get(0).getId()));
					continue;
				}
				if ("VMFIN".equals(token.getPos().getPosValue()) || "VMINF".equals(token.getPos().getPosValue())) continue;
				if(token.getPos().getClass() == POS_VERB.class && coveredVerbs.containsKey(lemma)){
					String label = null;

					HashSet<String> sentence_frames = generateFrames(sentence, lemma, depTree, verbose);
					if (verbose) System.out.println("Generated frames " + sentence_frames + " for " + lemma);
					if (sentence_frames == null || sentence_frames.isEmpty()) continue;

					HashMap<String, HashSet<Set<String>>> criteriaMap = getCandidateCriteria(lemma, strict);
					if (verbose) System.out.println("Candidate senses: " + criteriaMap);
					HashMap<String, Set<Set<String>>> candidates = new HashMap<String, Set<Set<String>>>();
					boolean is_ambiguous = false;
					boolean annotated = false;
					for (String sense : senseInventory.get(lemma)) {
						// Check if sentence frames are identical to unique gold frame, if so, immediately put that into candidates
						// Currently disabled since it decreases accuracy drastically
//						if (criteriaMap.containsKey(sense) && criteriaMap.get(sense) != null) {
//							for (Set<String> gold_frame : criteriaMap.get(sense)) {
//								if (gold_frame.equals(sentence_frames)) {
//									Set<Set<String>> candidateframes = new HashSet<Set<String>>();
//									if (candidates.containsKey(sense)) candidateframes = candidates.get(sense);
//									candidateframes.add(gold_frame);
//									candidates.put(sense, candidateframes);
//									annotated = true;
//								}
//							}
//						}
//						if (annotated) break;
						// Check if the sentence frames are identical to an ambiguous frame
						if (senseFramesAmbiguous.containsKey(sense)) {
							for (Set<String> frame : senseFramesAmbiguous.get(sense)) {
								// Check for equality, better coverage, worse accuracy
//								if (sentence_frames.equals(frame)) {
//									is_ambiguous = true;
//									if (verbose) System.out.println("Sentence frames are ambiguous: " + frame);
//									break;
//								}
								// Don't just check if identical, but check if ambiguous is contained in sentence
								if (sentence_frames.containsAll(frame)) {
									is_ambiguous = true;
									if (verbose) System.out.println("Sentence frames are contained in ambiguous sense " + sense + ": " + frame);
									break;
								}
							}
						}

						// Go through all gold frames and check for candidates
						// A gold frame is a candidate if it is completely contained in the sentence frames
						if (!criteriaMap.containsKey(sense) || criteriaMap.get(sense) == null) {
							continue;
						}
						for (Set<String> gold_frame : criteriaMap.get(sense)) {
							boolean candidate = true;
							if ("superstrict".equals(strict)) {
								candidate = gold_frame.equals(sentence_frames);
							} else {
								for (String frag: gold_frame) {
									if (!sentence_frames.contains(frag)) {
										candidate = false;
										break;
									}
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
					if (is_ambiguous && !annotated) continue;
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
					if (verbose) System.out.println("Labeled " + lemma + " with sense " + label);
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
			out.add(frag.toUpperCase());
		}
		frame = out;
		return frame;
	}

	// Marks optional fragments with ZZ- prefix and removes any that are not implemented
	public HashMap<String, HashSet<Set<String>>> preprocFrames(String target) {
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
		return senseFrameMap;
	}


	public HashMap<String, HashSet<Set<String>>> getUniques(String target, boolean implemented) {
		return getUniques(target, implemented, false);
	}


	public HashMap<String, HashSet<Set<String>>> getUniques(String target, boolean implemented, boolean init_pop) {
		HashMap<String, HashSet<Set<String>>> candidateCriteria = new HashMap<String, HashSet<Set<String>>>();
		HashMap<String, HashSet<Set<String>>> senseFrameMap = preprocFrames(target);

		String[] senseArray = senseFrameMap.keySet().toArray(new String[senseFrameMap.keySet().size()]);
		for (int i = 0; i < senseArray.length; i++) {
			String sense = senseArray[i];
			for (Set<String> frame: senseFrameMap.get(sense)) {
				Set<String> r_frame = removeOptionals(frame, false);
				frame = removeOptionals(frame, true);
				boolean unique = true;
				boolean unique_with_opts = true;
				for (int j = 0; j < senseArray.length; j++) {
					if (i == j) continue;
					String comparesense = senseArray[j];

					for (Set<String> compareframe: senseFrameMap.get(comparesense)) {
						Set<String> r_temp = removeOptionals(compareframe, false);
						compareframe = removeOptionals(compareframe, true);
						if (frame.equals(compareframe)) {
							unique = false;
						}
						if (frame.equals(r_temp)) {
							unique = false;
						}
						if (r_frame.equals(compareframe)) {
							unique_with_opts = false;
						}
						if (r_frame.equals(r_temp)) {
							unique_with_opts = false;
						}
					}
				}
				if (unique || unique_with_opts) {
					HashSet<Set<String>> frames = new HashSet<Set<String>>();
					if (candidateCriteria.containsKey(sense)) frames = candidateCriteria.get(sense);
					if (unique) frames.add(frame);
					if (unique_with_opts) frames.add(r_frame);
					candidateCriteria.put(sense, frames);
				}
				if (init_pop && (!unique || !unique_with_opts)) {
					HashSet<Set<String>> ambiframes = new HashSet<Set<String>>();
					if (senseFramesAmbiguous.containsKey(sense)) ambiframes = senseFramesAmbiguous.get(sense);
					if (!unique) ambiframes.add(frame);
					if (!unique_with_opts) ambiframes.add(r_frame);
					senseFramesAmbiguous.put(sense, ambiframes);
				}
			}
		}
		if (init_pop) {
			senseFramesUnique.putAll(candidateCriteria);
			coveredVerbs.put(target, candidateCriteria.keySet());
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
