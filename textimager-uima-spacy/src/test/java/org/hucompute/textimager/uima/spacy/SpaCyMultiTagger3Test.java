package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class SpaCyMultiTagger3Test {
	@Test
	public void multiTaggerTest_de() throws UIMAException {

// German Tests =======================================================================================================

		JCas cas = JCasFactory.createText("Das ist ein IPhone von Apple. Und das ist ein iMac.", "de");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);
		
		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
			new int[] { 0, 3 }, //Das
			new int[] { 4, 7 }, //ist
			new int[] { 8, 11 }, //ein
			new int[] { 12, 18 }, //IPhone
			new int[] { 19, 22 }, //von
			new int[] { 23, 28 }, //Apple
			new int[] { 28, 29 }, //.
			new int[] { 30, 33 }, //Und
			new int[] { 34, 37 }, //das
			new int[] { 38, 41 }, //ist
			new int[] { 42, 45 }, //ein
			new int[] { 46, 50 }, //iMac
			new int[] { 50, 51 } //.
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
			new int[] { 0, 29 },
			new int[] { 30, 51 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"PDS", "VAFIN", "ART", "NN", "APPR", "NE", "$.", "KON", "PDS", "VAFIN", "ART", "NN", "$."
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"SB", "--", "NK", "PD", "PG","NK", "PUNCT", "JU", "SB", "--", "NK", "PD", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "MISC","ORG", "MISC"};
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_en() throws UIMAException {

// English Tests =======================================================================================================

		JCas cas = JCasFactory.createText("This is an IPhone by Apple. And this is an iMac.", "en");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] { 0, 4 }, //This
				new int[] { 5, 7 }, //is
				new int[] { 8, 10 }, //an
				new int[] { 11, 17 }, //IPhone
				new int[] { 18, 20 }, //by
				new int[] { 21, 26 }, //Apple
				new int[] { 26, 27 }, //.
				new int[] { 28, 31 }, //And
				new int[] { 32, 36 }, //this
				new int[] { 37, 39 }, //is
				new int[] { 40, 42}, //an
				new int[] { 43, 47 }, //iMac
				new int[] { 47, 48 } //.
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 27 },
				new int[] { 28, 48 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"DT", "VBZ", "DT", "NN", "IN", "NNP", ".", "CC", "DT", "VBZ", "DT", "NNP", "."
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "DET", "ATTR", "PREP", "POBJ", "PUNCT", "CC", "NSUBJ", "--", "DET", "ATTR", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "ORG", "ORG" };
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_fr() throws UIMAException {

// French Tests =======================================================================================================

		JCas cas = JCasFactory.createText("Elle aime le chien. Marc a conduit la voiture.", "fr");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] { 0, 4 }, //Elle
				new int[] { 5, 9 }, //aime
				new int[] { 10, 12 }, //le
				new int[] { 13, 18 }, //chien
				new int[] { 18, 19 }, //.
				new int[] { 20, 24 }, //Marc
				new int[] { 25, 26 }, //a
				new int[] { 27, 34 }, //conduit
				new int[] { 35, 37 }, //la
				new int[] { 38, 45 }, //voiture
				new int[] { 45, 46} //.
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 19 },
				new int[] { 20, 46 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "VERB", "DET", "NOUN", "PUNCT", "PROPN", "AUX", "VERB", "DET", "NOUN", "PUNCT"
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "DET", "OBJ", "PUNCT", "NSUBJ", "AUX:TENSE", "--", "DET", "OBJ", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "PER" };
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_da() throws UIMAException {

// Danish Tests =======================================================================================================

		JCas cas = JCasFactory.createText("Jeg så et hus. Han tog sin hat. Flyet er fløjet.", "da");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] { 0, 3 }, //Jeg
				new int[] { 4, 6 }, //så
				new int[] { 7, 9 }, //et
				new int[] { 10, 13 }, //hus
				new int[] { 13, 14 }, //.
				new int[] { 15, 18 }, //Han
				new int[] { 19, 22 }, //tog
				new int[] { 23, 26 }, //sin
				new int[] { 27, 30 }, //hat
				new int[] { 30, 31 }, //.
				new int[] { 32, 37 }, //Flyet
				new int[] { 38, 40 }, //er
				new int[] { 41, 47 }, //fløjet
				new int[] { 47, 48 } //.
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 14 },
				new int[] { 15, 31 },
				new int[] { 32, 48 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "ADV", "DET", "NOUN", "PUNCT", "PRON", "VERB", "DET", "NOUN", "PUNCT", "NOUN", "AUX", "VERB", "PUNCT"
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"--", "PUNCT", "DET", "OBJ", "PUNCT", "NSUBJ", "--", "DET", "OBJ", "PUNCT", "NSUBJ", "AUX", "--", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { };
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_nl() throws UIMAException {


// Dutch Tests =======================================================================================================

		JCas cas = JCasFactory.createText(
				"Hij vertelde over zijn zoon en zijn vrouw. Kun jij je pen niet vinden omdat het veel te donker is?", "nl");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 3},
				new int[] {4, 12},
				new int[] {13, 17},
				new int[] {18, 22},
				new int[] {23, 27},
				new int[] {28, 30},
				new int[] {31, 35},
				new int[] {36, 41},
				new int[] {41, 42},
				new int[] {43, 46},
				new int[] {47, 50},
				new int[] {51, 53},
				new int[] {54, 57},
				new int[] {58, 62},
				new int[] {63, 69},
				new int[] {70, 75},
				new int[] {76, 79},
				new int[] {80, 84},
				new int[] {85, 87},
				new int[] {88, 94},
				new int[] {95, 97},
				new int[] {97, 98}

		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 42 },
				new int[] { 43, 98 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"VNW|pers|pron|nomin|vol|3|ev|masc", "WW|pv|verl|ev", "VZ|init", "VNW|bez|det|stan|vol|3|ev|prenom|zonder|agr",
				"N|soort|ev|basis|zijd|stan", "VG|neven", "VNW|bez|det|stan|vol|3|ev|prenom|zonder|agr", "N|soort|ev|basis|zijd|stan",
				"LET", "WW|pv|tgw|ev", "VNW|pers|pron|nomin|vol|2v|ev", "VNW|bez|det|stan|red|2v|ev|prenom|zonder|agr",
				"N|soort|ev|basis|zijd|stan", "BW", "WW|inf|vrij|zonder", "VG|onder", "VNW|pers|pron|stan|red|3|ev|onz",
				"VNW|onbep|grad|stan|vrij|zonder|basis", "BW", "ADJ|vrij|basis|zonder", "WW|pv|tgw|ev", "LET"
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "CASE", "NMOD:POSS", "OBL", "CC", "NMOD:POSS", "CONJ", "PUNCT", "AUX", "NSUBJ", "NMOD:POSS",
				"OBJ", "ADVMOD", "--", "MARK", "NSUBJ", "NMOD", "ADVMOD", "CCOMP", "COP", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { };
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_el() throws UIMAException {


// Greek Tests =======================================================================================================

		JCas cas = JCasFactory.createText("πως σε λένε? Δεν καταλαβαίνω. Αγαπώ την Ελλάδα!", "el");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 3},
				new int[] {4, 6},
				new int[] {7, 11},
				new int[] {11, 12},
				new int[] {13, 16},
				new int[] {17, 28},
				new int[] {28, 29},
				new int[] {30, 35},
				new int[] {36, 39},
				new int[] {40, 46},
				new int[] {46, 47}
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 29 },
				new int[] { 30, 47 }
		};

		int[][] casSents = (int[][]) JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"SCONJ", "ADP", "VERB", "VERB", "PART", "VERB", "PUNCT", "VERB", "DET", "PROPN", "PUNCT"
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"MARK", "CASE", "AMOD", "OBL", "ADVMOD", "--", "PUNCT", "--", "DET", "OBJ", "PUNCT"
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "GPE" };
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}
}

