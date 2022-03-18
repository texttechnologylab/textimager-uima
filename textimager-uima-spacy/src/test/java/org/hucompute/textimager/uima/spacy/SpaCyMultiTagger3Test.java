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
import org.texttechnologylab.utilities.helper.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class SpaCyMultiTagger3Test {

    @Test
    public void bigText() throws UIMAException, IOException {

        String testFile = SpaCyMultiTagger3Test.class.getClassLoader().getResource("1000.txt").getPath();

        JCas pCas = JCasFactory.createText(FileUtils.getContentFromFile(new File(testFile)));

        AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
                SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://huaxal.hucompute.org:8106");

        SimplePipeline.runPipeline(pCas, spacyMulti);

        JCasUtil.selectAll(pCas).forEach(t -> {
            System.out.println(t);
        });


    }

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
			new int[] { 0, 29 },
			new int[] { 30, 51 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PDS", "VAFIN", "ART", "NN", "APPR", "NE", "$.", "KON", "PDS", "VAFIN", "ART", "NN", "$."
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"SB", "--", "NK", "PD", "PG","NK", "PUNCT", "JU", "SB", "--", "NK", "PD", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "MISC","ORG", "MISC"};
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 27 },
				new int[] { 28, 48 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"DT", "VBZ", "DT", "NN", "IN", "NNP", ".", "CC", "DT", "VBZ", "DT", "NNP", "."
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "DET", "ATTR", "PREP", "POBJ", "PUNCT", "CC", "NSUBJ", "--", "DET", "ATTR", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "ORG", "ORG" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 19 },
				new int[] { 20, 46 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "VERB", "DET", "NOUN", "PUNCT", "PROPN", "AUX", "VERB", "DET", "NOUN", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "DET", "OBJ", "PUNCT", "NSUBJ", "AUX:TENSE", "--", "DET", "OBJ", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "PER" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 14 },
				new int[] { 15, 31 },
				new int[] { 32, 48 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "ADV", "DET", "NOUN", "PUNCT", "PRON", "VERB", "DET", "NOUN", "PUNCT", "NOUN", "AUX", "VERB", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"--", "PUNCT", "DET", "OBJ", "PUNCT", "NSUBJ", "--", "DET", "OBJ", "PUNCT", "NSUBJ", "AUX", "--", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 42 },
				new int[] { 43, 98 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"VNW|pers|pron|nomin|vol|3|ev|masc", "WW|pv|verl|ev", "VZ|init", "VNW|bez|det|stan|vol|3|ev|prenom|zonder|agr",
				"N|soort|ev|basis|zijd|stan", "VG|neven", "VNW|bez|det|stan|vol|3|ev|prenom|zonder|agr", "N|soort|ev|basis|zijd|stan",
				"LET", "WW|pv|tgw|ev", "VNW|pers|pron|nomin|vol|2v|ev", "VNW|bez|det|stan|red|2v|ev|prenom|zonder|agr",
				"N|soort|ev|basis|zijd|stan", "BW", "WW|inf|vrij|zonder", "VG|onder", "VNW|pers|pron|stan|red|3|ev|onz",
				"VNW|onbep|grad|stan|vrij|zonder|basis", "BW", "ADJ|vrij|basis|zonder", "WW|pv|tgw|ev", "LET"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "CASE", "NMOD:POSS", "OBL", "CC", "NMOD:POSS", "CONJ", "PUNCT", "AUX", "NSUBJ", "NMOD:POSS",
				"OBJ", "ADVMOD", "--", "MARK", "NSUBJ", "NMOD", "ADVMOD", "CCOMP", "COP", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);
	}


	@Test
	public void multiTaggerTest_el() throws UIMAException {


// Greek Tests ========================================================================================================

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
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] { 0, 29 },
				new int[] { 30, 47 }
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"SCONJ", "ADP", "VERB", "VERB", "PART", "VERB", "PUNCT", "VERB", "DET", "PROPN", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"MARK", "CASE", "AMOD", "OBL", "ADVMOD", "--", "PUNCT", "--", "DET", "OBJ", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "GPE" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_it() throws UIMAException {


// Italian Tests ======================================================================================================

		JCas cas = JCasFactory.createText("Lo studio dell'italiano aguzza l'ingegno. Dov'è il bagno? Non capisco l'italiano.", "it");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 2},
				new int[] {3, 9},
				new int[] {10, 15},
				new int[] {15, 23},
				new int[] {24, 30},
				new int[] {31, 33},
				new int[] {33, 40},
				new int[] {40, 41},
				new int[] {42, 46},
				new int[] {46, 47},
				new int[] {48, 50},
				new int[] {51, 56},
				new int[] {56, 57},
				new int[] {58, 61},
				new int[] {62, 69},
				new int[] {70, 72},
				new int[] {72, 80},
				new int[] {80, 81}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 41},
				new int[] {42, 57},
				new int[] {58, 81}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"RD", "S", "E_RD", "S", "V", "RD", "S", "FS", "B", "V", "RD", "S", "FS", "BN", "V", "RD", "S", "FS"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"DET", "NSUBJ", "CASE", "NMOD", "--", "DET", "OBJ", "PUNCT", "ADVMOD", "--", "DET", "NSUBJ", "PUNCT", "ADVMOD", "--", "DET", "OBJ", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] {  };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_zh() throws UIMAException {


// Chinese Tests ======================================================================================================

		JCas cas = JCasFactory.createText("她是我最好的朋友。这里人很多。你认为怎样？", "zh");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 1},
				new int[] {1, 2},
				new int[] {2, 3},
				new int[] {3, 5},
				new int[] {5, 6},
				new int[] {6, 8},
				new int[] {8, 9},
				new int[] {9, 11},
				new int[] {11, 12},
				new int[] {12, 14},
				new int[] {14, 15},
				new int[] {15, 16},
				new int[] {16, 18},
				new int[] {18, 20},
				new int[] {20, 21}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 9},
				new int[] {9, 15},
				new int[] {15, 21}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PN", "VC", "PN", "VA", "DEC", "NN", "PU", "PN", "NN", "CD", "PU", "PN", "VV", "VA", "PU"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "COP", "DEP", "AMOD", "MARK", "--", "PUNCT", "NMOD:POSS", "NSUBJ", "--", "PUNCT", "NSUBJ", "--", "CCOMP", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] {  };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_ja() throws UIMAException {


// Japanese Tests ======================================================================================================

		JCas cas = JCasFactory.createText("地図を書いてもらえますか？ 日本語は話せません. 水をお願いします.", "ja");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 2},
				new int[] {2, 3},
				new int[] {3, 5},
				new int[] {5, 6},
				new int[] {6, 9},
				new int[] {9, 11},
				new int[] {11, 12},
				new int[] {12, 13},
				new int[] {14, 16},
				new int[] {16, 17},
				new int[] {17, 18},
				new int[] {18, 20},
				new int[] {20, 22},
				new int[] {22, 23},
				new int[] {23, 24},
				new int[] {25, 26},
				new int[] {26, 27},
				new int[] {27, 28},
				new int[] {28, 30},
				new int[] {30, 31},
				new int[] {31, 33},
				new int[] {33, 34}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 34}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"名詞-普通名詞-一般", "助詞-格助詞", "動詞-一般", "助詞-接続助詞", "動詞-非自立可能", "助動詞", "助詞-終助詞", "補助記号-句点",
				"名詞-固有名詞-地名-国", "名詞-普通名詞-一般", "助詞-係助詞", "動詞-一般", "助動詞", "助動詞", "補助記号-句点", "名詞-普通名詞-一般",
				"助詞-格助詞", "接頭辞", "動詞-非自立可能", "動詞-非自立可能", "助動詞", "補助記号-句点"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"OBJ", "CASE", "ACL", "MARK", "AUX", "AUX", "MARK", "COMPOUND", "COMPOUND", "NSUBJ", "CASE", "ACL", "AUX",
				"AUX", "PUNCT", "OBJ", "CASE", "COMPOUND", "--", "AUX", "AUX", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "LANGUAGE" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_lt() throws UIMAException {


// Lithuanian Tests ===================================================================================================

		JCas cas = JCasFactory.createText("Aš ieškau Jono. Eikite su manimi. Gal gali kalbėti lėčiau?", "lt");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 2},
				new int[] {3, 9},
				new int[] {10, 14},
				new int[] {14, 15},
				new int[] {16, 22},
				new int[] {23, 25},
				new int[] {26, 32},
				new int[] {32, 33},
				new int[] {34, 37},
				new int[] {38, 42},
				new int[] {43, 50},
				new int[] {51, 57},
				new int[] {57, 58}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 15},
				new int[] {16, 33},
				new int[] {34, 58}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"įv.vns.V.", "bdv.nelygin.mot.vns.V.", "dkt.tikr.vyr.vns.K.", "skyr.", "vksm.asm.sngr.liep.dgs.2.", "prl.Įn.",
				"įv.vns.Įn.", "skyr.", "dll.", "vksm.asm.tiesiog.es.dgs.3.", "vksm.bndr.", "prv.aukšt.", "skyr."
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "--", "NMOD", "PUNCT", "--", "CASE", "OBL:ARG", "PUNCT", "ADVMOD:EMPH", "--", "XCOMP", "ADVMOD", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "PERSON" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_pl() throws UIMAException {

// Polish Tests =======================================================================================================

		JCas cas = JCasFactory.createText("Nie rozumiem. Czy mówisz po angielsku? Wszystkiego najlepszego!", "pl");

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
				new int[] {12, 13},
				new int[] {14, 17},
				new int[] {18, 24},
				new int[] {25, 27},
				new int[] {28, 37},
				new int[] {37, 38},
				new int[] {39, 50},
				new int[] {51, 62},
				new int[] {62, 63}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 13},
				new int[] {14, 38},
				new int[] {39, 63}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"QUB", "FIN", "INTERP", "QUB", "FIN", "PREP", "ADJP", "INTERP", "ADJ", "ADJ", "INTERP"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"ADVMOD:NEG", "--", "PUNCT", "ADVMOD", "--", "CASE", "OBL", "PUNCT", "--", "AMOD", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "placeName" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_pt() throws UIMAException {

// Portugese Tests ====================================================================================================

		JCas cas = JCasFactory.createText("Alguém aqui fala inglês? Para onde vai esse ônibus? Você pode me mostrar no mapa?", "pt");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 6},
				new int[] {7, 11},
				new int[] {12, 16},
				new int[] {17, 23},
				new int[] {23, 24},
				new int[] {25, 29},
				new int[] {30, 34},
				new int[] {35, 38},
				new int[] {39, 43},
				new int[] {44, 50},
				new int[] {50, 51},
				new int[] {52, 56},
				new int[] {57, 61},
				new int[] {62, 64},
				new int[] {65, 72},
				new int[] {73, 75},
				new int[] {76, 80},
				new int[] {80, 81}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 24},
				new int[] {25, 51},
				new int[] {52, 81}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "ADV", "VERB", "NOUN", "PUNCT", "ADP", "PRON", "VERB", "DET", "NOUN", "PUNCT", "PRON", "VERB", "PRON", "VERB", "ADP", "NOUN", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "ADVMOD", "--", "OBJ", "PUNCT", "CASE", "OBL", "--", "DET", "OBJ", "PUNCT", "NSUBJ", "--", "OBJ", "XCOMP", "CASE", "OBL", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] {  };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_ro() throws UIMAException {

// Romanian Tests ====================================================================================================

		JCas cas = JCasFactory.createText("Pot să fac o poză? Mă bucur să vă cunosc. Vorbesc puțin limba română. Pot intra pe internet de aici?", "ro");

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
				new int[] {7, 10},
				new int[] {11, 12},
				new int[] {13, 17},
				new int[] {17, 18},
				new int[] {19, 21},
				new int[] {22, 27},
				new int[] {28, 30},
				new int[] {31, 33},
				new int[] {34, 40},
				new int[] {40, 41},
				new int[] {42, 49},
				new int[] {50, 55},
				new int[] {56, 61},
				new int[] {62, 68},
				new int[] {68, 69},
				new int[] {70, 73},
				new int[] {74, 79},
				new int[] {80, 82},
				new int[] {83, 91},
				new int[] {92, 94},
				new int[] {95, 99},
				new int[] {99, 100}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 18},
				new int[] {19, 41},
				new int[] {42, 69},
				new int[] {70, 100}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"Vmip3p", "Qs", "Vmip3p", "Tifsr", "Ncfsrn", "QUEST", "Pp1-sa--------w", "Vmip1s", "Qs", "Pp2-pa--------w", "Vmip1s", "PERIOD", "Vmip3p",
				"Rgp", "Ncfsry", "Afpfsrn", "PERIOD", "Vmip3p", "Vmnp", "Spsa", "Ncms-n", "Spsa", "Rgp", "QUEST"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"--", "MARK", "CCOMP", "DET", "OBJ", "PUNCT", "EXPL:PV", "--", "MARK", "EXPL:PV", "CCOMP", "PUNCT", "--", "ADVMOD", "OBJ",
				"AMOD", "PUNCT", "--", "CCOMP", "CASE", "OBL", "CASE", "ADVMOD", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "LANGUAGE" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_ru() throws UIMAException {

// Russian Tests ====================================================================================================

		JCas cas = JCasFactory.createText("Меня зовут Мандли. Говорите помедленнее. Где туалет? Я скоро вернусь.", "ru");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 4},
				new int[] {5, 10},
				new int[] {11, 17},
				new int[] {17, 18},
				new int[] {19, 27},
				new int[] {28, 39},
				new int[] {39, 40},
				new int[] {41, 44},
				new int[] {45, 51},
				new int[] {51, 52},
				new int[] {53, 54},
				new int[] {55, 60},
				new int[] {61, 68},
				new int[] {68, 69} // nice
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 18},
				new int[] {19, 40},
				new int[] {41, 52},
				new int[] {53, 69} // nice
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"PRON", "VERB", "PROPN", "PUNCT", "VERB", "ADV", "PUNCT", "ADV", "NOUN", "PUNCT", "PRON", "ADV", "VERB", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"OBJ", "--", "NSUBJ", "PUNCT", "--", "ADVMOD", "PUNCT", "--", "NSUBJ", "PUNCT", "NSUBJ", "ADVMOD", "--", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "PER" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_es() throws UIMAException {

// Spanish Tests ======================================================================================================

		JCas cas = JCasFactory.createText("Estoy perdido. ¿Puedes hablar más despacio? Yo tengo 21 años.", "es");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 5},
				new int[] {6, 13},
				new int[] {13, 14},
				new int[] {15, 16},
				new int[] {16, 22},
				new int[] {23, 29},
				new int[] {30, 33},
				new int[] {34, 42},
				new int[] {42, 43},
				new int[] {44, 46},
				new int[] {47, 52},
				new int[] {53, 55},
				new int[] {56, 60},
				new int[] {60, 61}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 14},
				new int[] {15, 43},
				new int[] {44, 61}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"AUX", "ADJ", "PUNCT", "PUNCT", "AUX", "VERB", "ADV", "ADV", "PUNCT", "PRON", "VERB", "NUM", "NOUN", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"COP", "--", "PUNCT", "PUNCT", "AUX", "--", "ADVMOD", "OBJ", "PUNCT", "NSUBJ", "--", "NUMMOD", "OBJ", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "PER", "MISC" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);

		System.out.println(XmlFormatter.getPrettyString(cas));

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_ca() throws UIMAException {

// Catalan Tests ======================================================================================================

		JCas cas = JCasFactory.createText("Has begut oli. Déu n’hi do! Què és això?", "ca");

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
				new int[] {4, 9},
				new int[] {10, 13},
				new int[] {13, 14},
				new int[] {15, 18},
				new int[] {19, 21},
				new int[] {21, 23},
				new int[] {24, 26},
				new int[] {26, 27},
				new int[] {28, 31},
				new int[] {32, 34},
				new int[] {35, 39},
				new int[] {39, 40}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 14},
				new int[] {15, 40}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"AUX", "VERB", "NOUN", "PUNCT", "PROPN", "VERB", "PRON", "ADP", "PUNCT", "PRON", "AUX", "PRON", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"AUX", "--", "OBJ", "PUNCT", "NSUBJ", "FLAT", "OBL", "OBL", "PUNCT", "NSUBJ", "COP", "--", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "LOC", "ORG" };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);


		System.out.println(XmlFormatter.getPrettyString(cas));
		System.out.println(casPos[0] + ", " + casPos[1] + ", " +casPos[2] +", " + casPos[3]+", " + casPos[4]+", " +
				casPos[5]+", " + casPos[6]+ ", " +casPos[7]+ ", " +casPos[8]+
				", " +casPos[9]+", " + casPos[10]+", " + casPos[11] + ", " +casPos[12]);

		System.out.println(casDeps[0] + ", " +casDeps[1] +", " + casDeps[2] + ", " +casDeps[3]+", " + casDeps[4]+", " +
				casDeps[5]+", " + casDeps[6]+ ", " +casDeps[7]+ ", " +casDeps[8]+
				", " +casDeps[9]+ ", " +casDeps[10]+ ", " +casDeps[11] +", " + casDeps[12]);

		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}


	@Test
	public void multiTaggerTest_mk() throws UIMAException {

// Makedonian Tests ======================================================================================================

		JCas cas = JCasFactory.createText("Мачката ја каса кучето. и не нѐ воведувај во искушение, но избави нѐ од лукавиот "
				+"Зашто Твое е Царството и Силата и Славата, во вечни векови.", "mk");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, spacyMulti);

		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
				new int[] {0, 7},
				new int[] {8, 10},
				new int[] {11, 15},
				new int[] {16, 22},
				new int[] {22, 23},
				new int[] {24, 25},
				new int[] {26, 28},
				new int[] {29, 31},
				new int[] {32, 41},
				new int[] {42, 44},
				new int[] {45, 54},
				new int[] {54, 55},
				new int[] {56, 58},
				new int[] {59, 65},
				new int[] {66, 68},
				new int[] {69, 71},
				new int[] {72, 80},
				new int[] {81, 86},
				new int[] {87, 91},
				new int[] {92, 93},
				new int[] {94, 103},
				new int[] {104, 105},
				new int[] {106, 112},
				new int[] {113, 114},
				new int[] {115, 122},
				new int[] {122, 123},
				new int[] {124, 126},
				new int[] {127, 132},
				new int[] {133, 139},
				new int[] {139, 140}
		};
        int[][] casTokens = JCasUtil.select(cas, Token.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		int[][] sents = new int[][] {
				new int[] {0, 23},
				new int[] {24, 140}
		};

        int[][] casSents = JCasUtil.select(cas, Sentence.class).stream().map(s -> new int[]{s.getBegin(), s.getEnd()}).toArray(int[][]::new);

		String[] pos = new String[] {
				"NOUN", "PRON", "NOUN", "NOUN", "PUNCT", "CONJ", "VERB", "ADP", "NOUN", "ADP", "NOUN", "PUNCT", "CONJ", "VERB", "ADP", "ADP", "NOUN",
				"SCONJ", "PROPN", "AUX", "NOUN", "CONJ", "PROPN", "CONJ", "PROPN", "PUNCT", "ADP", "ADJ", "NOUN", "PUNCT"
		};
        String[] casPos = JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"NSUBJ", "PPDO", "--", "DOBJ", "PUNCT", "DEP", "NEG", "DEP", "--", "PREP", "IOBJ", "PUNCT", "PREP",
				"POBJ", "PREP", "PREP", "POBJ", "DEP", "DEP", "AUX", "IOBJ", "CC", "POBJ", "CC", "POBJ", "PUNCT", "PREP",
				"ATT", "POBJ", "PUNCT"
		};
        String[] casDeps = JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] {  };
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);


		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
		assertArrayEquals(sents, casSents);

	}
}

