package org.hucompute.textimager.uima.io.mediawiki;

import java.io.File;   
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Comparable;
import java.lang.Math;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;


// TODO replace with new "types" category
import org.hucompute.services.type.CategoryCoveredTagged;

public class MediawikiWriter extends JCasConsumer_ImplBase{

	/**
	 * Output directory
	 */
	public static final String PARAM_TARGET_LOCATION = "targetLocation";
	@ConfigurationParameter(name = PARAM_TARGET_LOCATION, mandatory = true)
	protected String targetLocation;

	/**
	 * Username
	 */
	public static final String PARAM_USERNAME = "username";
	@ConfigurationParameter(name = PARAM_USERNAME, mandatory = false, defaultValue = "Admin")
	protected String username;

	/**
	 * User ID
	 */
	public static final String PARAM_USERID = "userid";
	@ConfigurationParameter(name = PARAM_USERID, mandatory = false, defaultValue = "0")
	protected String userid;

	/**
	 * Start Page ID
	 */
	public static final String PARAM_START_PAGEID = "startPageId";
	@ConfigurationParameter(name = PARAM_START_PAGEID, mandatory = false, defaultValue = "0")
	protected long startPageId;

	// How many Word2Vec hits should be shown in graphs
	private static final int LEMMA_GRAPH_NEAREST_WORDS_COUNT = 10;
	// How many Word2Vec hits should be shown in word lists (this must be higher then graphs and tooltips)
	private static final int LEMMA_PAGE_NEAREST_WORDS_COUNT = 30;
	// How many Word2Vec hits should be shown in tooltips
	private static final int LEMMA_TOOLTIP_NEAREST_WORDS_COUNT = 10;
	private MessageDigest md;
	private long pageIdGlobal = 0;
	// Variable to add lines of the output file step by step
	private PrintWriter writer;
	// To save names of all text files 
	private HashMap<String, HashSet<String>> folderPages;
	// Collect occurances for every ddc
	private DDCInfos ddcInfos;
	// Collect features for every lemma
	private LemmaInfos lemmaInfos;
	private Set<LemmaInfos.LemmaPos> failedLemmaPosMorphologicalFeatures;

	private static final String generatorVersion = "org.hucompute.textimager.uima.io.mediawiki.MediawikiWriter 1.1";

	private static final String nsPage = "0";
	private static final String nsMediaWiki = "8";
	private static final String nsCategory = "14";
	private static final String nsLemma = "102";
	private static final String nsTooltip = "104";

	private static final String categoryPrefix = "Category:";
	private static final String lemmaPrefix = "Lemma:";

	private int documentCount = 0;

	@Override
	public void destroy() {
		// To build all sub-pages for texts 

		// Write Common.js
		String common = "// Any JavaScript here will be loaded for all users on every page load.\n";
		common += "mw.loader.using('graph');\n";
		common += "mw.loader.using('tooltip');\n";
		common += "mw.loader.using('wikification');\n";
		writePage("Common.js", "javascript", common, nsMediaWiki, "javascript");

		//To save all names of files for the Corpus-page 
		StringBuilder corpusTextBuilder = new StringBuilder();

		//For each text 
		for (HashMap.Entry<String, HashSet<String>> entry : folderPages.entrySet()) {
			StringBuilder textBuilder = new StringBuilder();

			//read the name of text 
			for (String page : entry.getValue()) {
				textBuilder.append("* [[").append(page).append("]]\n");
			}
			//Comment for sub-page 
			String comment = "Generated from directory " + entry.getKey();
			//Building pages for all text files
			writePage(entry.getKey(), comment, textBuilder.toString(), nsPage);
			//Save the name for Corpus-page 
			if (!entry.getKey().isEmpty()) {
				corpusTextBuilder.append("'''").append(entry.getKey()).append("'''").append("\n\n");
			}
			corpusTextBuilder.append(textBuilder).append("\n\n");
		}

		// Special pages
		writePage("Corpus", "Corpus overview", corpusTextBuilder.toString(), nsPage);
		writeDDCPages();
		writeLemmaPages("de"); // TODO get right language

		writer.println("</mediawiki>");

		writer.flush();
		writer.close();

		super.destroy();
	}

	//To initialize and write the beginning of output file 
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
				
		File outputDir = new File(targetLocation);
		outputDir.mkdirs();
		// To write the file with name: output.wiki.xml
		File outputDumpFile = new File(targetLocation + "/output.wiki.xml");

		String sitename = outputDir.getName();
		String dbname = outputDir.getName().toLowerCase().replaceAll(" ", "_");
		String base = outputDir.getAbsolutePath().replaceAll(" ", "_");

		folderPages = new HashMap<String, HashSet<String>>();
		ddcInfos = new DDCInfos();
		lemmaInfos = new LemmaInfos();
		failedLemmaPosMorphologicalFeatures = new HashSet<LemmaInfos.LemmaPos>();

		pageIdGlobal = startPageId;

		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}

		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputDumpFile), Charset.forName("UTF-8")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}
		//The header part of output file 
		writer.println(
				"<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd\" version=\"0.10\" xml:lang=\"en\">");
		writer.println("<siteinfo>");
		writer.println("<sitename>" + sitename + "</sitename>");
		writer.println("<dbname>" + dbname + "</dbname>");
		writer.println("<base>" + base + "</base>");
		writer.println("<generator>" + generatorVersion + "</generator>");
		writer.println("<case>first-letter</case>");
		writer.println("<namespaces>");
		writer.println("<namespace key=\"-2\" case=\"first-letter\">Media</namespace>");
		writer.println("<namespace key=\"-1\" case=\"first-letter\">Special</namespace>");
		writer.println("<namespace key=\"0\" case=\"first-letter\" />");
		writer.println("<namespace key=\"1\" case=\"first-letter\">Talk</namespace>");
		writer.println("<namespace key=\"2\" case=\"first-letter\">User</namespace>");
		writer.println("<namespace key=\"3\" case=\"first-letter\">User talk</namespace>");
		writer.println("<namespace key=\"4\" case=\"first-letter\">Wikipedia</namespace>");
		writer.println("<namespace key=\"5\" case=\"first-letter\">Wikipedia talk</namespace>");
		writer.println("<namespace key=\"6\" case=\"first-letter\">File</namespace>");
		writer.println("<namespace key=\"7\" case=\"first-letter\">File talk</namespace>");
		writer.println("<namespace key=\"8\" case=\"first-letter\">MediaWiki</namespace>");
		writer.println("<namespace key=\"9\" case=\"first-letter\">MediaWiki talk</namespace>");
		writer.println("<namespace key=\"10\" case=\"first-letter\">Template</namespace>");
		writer.println("<namespace key=\"11\" case=\"first-letter\">Template talk</namespace>");
		writer.println("<namespace key=\"12\" case=\"first-letter\">Help</namespace>");
		writer.println("<namespace key=\"13\" case=\"first-letter\">Help talk</namespace>");
		writer.println("<namespace key=\"14\" case=\"first-letter\">Category</namespace>");
		writer.println("<namespace key=\"15\" case=\"first-letter\">Category talk</namespace>");
		writer.println("<namespace key=\"100\" case=\"first-letter\">Portal</namespace>");
		writer.println("<namespace key=\"101\" case=\"first-letter\">Portal talk</namespace>");
		writer.println("<namespace key=\"102\" case=\"first-letter\">Lemma</namespace>");
		writer.println("<namespace key=\"103\" case=\"first-letter\">Lemma talk</namespace>");
		writer.println("<namespace key=\"104\" case=\"first-letter\">Tooltip</namespace>");
		writer.println("<namespace key=\"105\" case=\"first-letter\">Tooltip talk</namespace>");
		writer.println("<namespace key=\"108\" case=\"first-letter\">Book</namespace>");
		writer.println("<namespace key=\"109\" case=\"first-letter\">Book talk</namespace>");
		writer.println("<namespace key=\"118\" case=\"first-letter\">Draft</namespace>");
		writer.println("<namespace key=\"119\" case=\"first-letter\">Draft talk</namespace>");
		writer.println("<namespace key=\"446\" case=\"first-letter\">Education Program</namespace>");
		writer.println("<namespace key=\"447\" case=\"first-letter\">Education Program talk</namespace>");
		writer.println("<namespace key=\"710\" case=\"first-letter\">TimedText</namespace>");
		writer.println("<namespace key=\"711\" case=\"first-letter\">TimedText talk</namespace>");
		writer.println("<namespace key=\"828\" case=\"first-letter\">Module</namespace>");
		writer.println("<namespace key=\"829\" case=\"first-letter\">Module talk</namespace>");
		writer.println("<namespace key=\"2300\" case=\"first-letter\">Gadget</namespace>");
		writer.println("<namespace key=\"2301\" case=\"first-letter\">Gadget talk</namespace>");
		writer.println("<namespace key=\"2302\" case=\"case-sensitive\">Gadget definition</namespace>");
		writer.println("<namespace key=\"2303\" case=\"case-sensitive\">Gadget definition talk</namespace>");
		writer.println("</namespaces>");
		writer.println("</siteinfo>");
	}

	/** Write a page for every DDC. */
	private void writeDDCPages() {
		for (HashMap.Entry<String, String> entry : MediawikiDDCHelper.getAllDDCClasses().entrySet()) {
			String id = entry.getKey();
			String name = MediawikiDDCHelper.getDDCClassName(id);
			int level = MediawikiDDCHelper.getDDCLevel(id);
			DDCInfos.DDCInfo info = ddcInfos.get(id);

			StringBuilder pageBuilder = new StringBuilder();
			// Build the header
			pageBuilder.append("= ").append(name).append(" (").append(id).append(") =\n");
			// Build the hierarchy list
			String firstLevelId = id.substring(0, 1) + "00";
			String firstLevelName = MediawikiDDCHelper.getDDCClassName(firstLevelId);
			pageBuilder.append("* [[:Category:DDC").append(firstLevelId).append("|").append(firstLevelName).append(" (").append(firstLevelId).append(")]]\n");
			if (level > 1) {
				String secondLevelId = id.substring(0, 2) + "0";
				String secondLevelName = MediawikiDDCHelper.getDDCClassName(secondLevelId);
				pageBuilder.append("** [[:Category:DDC").append(secondLevelId).append("|").append(secondLevelName).append(" (").append(secondLevelId).append(")]]\n");
			}
			if (level == 3) {
				pageBuilder.append("*** [[:Category:DDC").append(id).append("|").append(name).append(" (").append(id).append(")]]\n");
			}
			
			// List subcategories
			if (level < 3) {
				pageBuilder.append("== Subcategories ==\n");
				String prefix = id.substring(0, level);
				String suffix = level == 1 ? id.substring(2, 3) : ""; //id.substring(level == 1 ? 2 : 3, 3);
				for (int i = 1; i <= 9; i++) {
					String subcategoryId = prefix + Integer.toString(i) + suffix;
					String subcategoryName = MediawikiDDCHelper.getDDCClassName(subcategoryId);
					pageBuilder.append("* [[:Category:DDC").append(subcategoryId).append("|").append(subcategoryName).append(" (").append(subcategoryId).append(")]]\n");
				}
			}

			// List texts of this category
			pageBuilder.append("== Texts ==\n")
				.append(info.documents.size()).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n");
			for (String documentTitle : info.documents) {
				pageBuilder.append("|-\n");
				pageBuilder.append("|align=\"left\"|[[").append(documentTitle).append("]]\n");
			}
			pageBuilder.append("|}\n");

			// List paragraphs of this category
			pageBuilder.append("== Paragraphs ==\n")
				.append(info.paragraphs.size()).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Paragraph\n");
			for (DDCInfos.DDCOccurance occurance : info.paragraphs) {
				pageBuilder.append("|-\n");
				pageBuilder.append("|align=\"left\"|[[").append(occurance.documentTitle).append("#PARAGRAPH_").append(occurance.paragraph).append("|").append(occurance.documentTitle).append("]]\n")
					.append("|align=\"left\"|").append(occurance.text).append("\n");
			}
			pageBuilder.append("|}\n");

			// List sentences of this category
			pageBuilder.append("== Sentences ==\n")
				.append(info.sentences.size()).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Sentence\n");
			for (DDCInfos.DDCOccurance occurance : info.sentences) {
				pageBuilder.append("|-\n");
				pageBuilder.append("|align=\"left\"|[[").append(occurance.documentTitle).append("#SENTENCE_").append(occurance.sentence).append("|").append(occurance.documentTitle).append("]]\n")
					.append("|align=\"left\"|").append(occurance.text).append("\n");
			}
			pageBuilder.append("|}\n");

			// List texts for this category
			writePage(categoryPrefix + "DDC" + id, "Generated DDC category", pageBuilder.toString(), nsCategory);
		}
	}

	/** Write a page for every lemma. */
	private void writeLemmaPages(String lang) {
		System.out.println(" INFO | MediaWikiWriter write " + lemmaInfos.size() + " lemma pages for " + documentCount + " documents");
		Word2VecHelper word2VecParadigmatic = new Word2VecHelper("word2vec/paradigmatic-" + lang + ".vec");
		Word2VecHelper word2VecSyntactic = new Word2VecHelper("word2vec/syntagmatic-" + lang + ".vec");
		GraphHelper graph = new GraphHelper();
		StringBuilder text = new StringBuilder();

		for (HashMap.Entry<LemmaInfos.LemmaPos, LemmaInfos.LemmaInfo> entry : lemmaInfos.entrySet()) {
			LemmaInfos.LemmaPos lemmapos = entry.getKey();
			LemmaInfos.LemmaInfo info = entry.getValue();
			boolean isVerb = lemmapos.pos.equals("V");
			text.replace(0, text.length(), "");

			// General info
			text.append("{{#lemmainfo: ")
				.append("Name:").append(lemmapos.lemma).append(",")
				.append("Part of Speech:").append(lemmapos.pos).append(",")
				.append("SyntacticWords:").append(info.morphologicalFeatures.size()).append(",")
				.append("Frequency Class:").append(info.getFrequencyClass()).append(",")
				.append("Frequency:").append(info.frequency).append(",")
				.append("Text Frequency:").append(info.getDocumentFrequency()).append(",")
				.append("Inverse Document Frequency:").append(info.getInverseDocumentFrequencyAsString(documentCount)).append(",")
				.append("Wiktionary:WIKTIONARY ").append(lang).append(" ").append(lemmapos.lemma)
				.append("}}\n\n");

			// Morphological features
			text.append("== Morphology ==\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|token\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|form\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|").append(isVerb ? "mood" : "case").append("\n")
				.append(isVerb ? "" : "!bgcolor=#F2F2F2 align=\"left\"|gender\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|number\n")
				.append(isVerb ? "!bgcolor=#F2F2F2 align=\"left\"|person\n" : "")
				.append("!bgcolor=#F2F2F2 align=\"left\"|pos\n")
				.append(isVerb ? "!bgcolor=#F2F2F2 align=\"left\"|tense\n" : "");
			for (EnhancedMorphologicalFeatures features : info.morphologicalFeatures) {
				text.append("|-\n")
					.append("|align=\"left\"|").append(features.getToken()).append("\n")
					.append("|align=\"left\"|").append(features.getVerbForm()).append("\n")
					.append("|align=\"left\"|").append(isVerb ? features.getMood() : features.getCase()).append("\n");
				if (!isVerb) {
					text.append("|align=\"left\"|").append(features.getGender()).append("\n");
				}
				text.append("|align=\"left\"|").append(features.getNumber()).append("\n");
				if (isVerb) {
					text.append("|align=\"left\"|").append(features.getPerson()).append("\n");
				}
				text.append("|align=\"left\"|").append(lemmapos.pos).append("\n");
				if (isVerb) {
					text.append("|align=\"left\"|").append(features.getTense()).append("\n");
				}
			}
			text.append("|}\n");

			// Get nearest words
			String wordsParadigmatic[] = null, wordsSyntactic[] = null;
			int max_nearest_words_count = Math.max(
					Math.max(LEMMA_PAGE_NEAREST_WORDS_COUNT, LEMMA_GRAPH_NEAREST_WORDS_COUNT),
					LEMMA_TOOLTIP_NEAREST_WORDS_COUNT);
			String nearestWordsParadigmatic[] = word2VecParadigmatic.getWordsNearestAsArray(lemmapos, max_nearest_words_count);
			String nearestWordsSyntactic[] = word2VecSyntactic.getWordsNearestAsArray(lemmapos, max_nearest_words_count);

			// Get the right counts for every Word2Vec presentation
			int graphNearestWordsCountParadigmatic = nearestWordsParadigmatic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_GRAPH_NEAREST_WORDS_COUNT) : 0;
			int graphNearestWordsCountSyntactic = nearestWordsSyntactic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_GRAPH_NEAREST_WORDS_COUNT) : 0;
			int pageNearestWordsCountParadigmatic = nearestWordsParadigmatic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_PAGE_NEAREST_WORDS_COUNT) : 0;
			int pageNearestWordsCountSyntactic = nearestWordsSyntactic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_PAGE_NEAREST_WORDS_COUNT) : 0;
			int tooltipNearestWordsCountParadigmatic = nearestWordsParadigmatic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_TOOLTIP_NEAREST_WORDS_COUNT) : 0;
			int tooltipNearestWordsCountSyntactic = nearestWordsSyntactic != null ? Math.min(nearestWordsParadigmatic.length, LEMMA_TOOLTIP_NEAREST_WORDS_COUNT) : 0;

			// Paradigmatic similarity
			text.append("== Paradigmatic Similarity (Word2Vec) ==\n");
			if (graphNearestWordsCountParadigmatic > 0) {
				// Create graph with nodes and edges
				LemmaInfos.LemmaPos words[] = new LemmaInfos.LemmaPos[graphNearestWordsCountParadigmatic];
				graph.start(lemmapos);
				for (int i = 0; i < graphNearestWordsCountParadigmatic; i++) {
					graph.add(new LemmaInfos.LemmaPos(nearestWordsParadigmatic[i]), word2VecParadigmatic);
				}
				text.append(graph.end());

				// Create a word list with hyperlinks
				text.append("<div class=\"mw-collapsible\" style=\"width:100%;overflow:auto;\">\n")
					.append("<div style=\"font-weight:bold;line-height:1.6;\">Word List</div>\n")
					.append("<div class=\"mw-collapsible-content\">");
				for (int i = 0; i < pageNearestWordsCountParadigmatic; i++) {
					LemmaInfos.LemmaPos wordLemmaPos = new LemmaInfos.LemmaPos(nearestWordsParadigmatic[i]);
					text.append("[[Lemma:").append(wordLemmaPos).append("|").append(wordLemmaPos.toString(" ")).append("]], ");
				}
				text.replace(text.length() - 2, text.length(), "");
				text.append("</div></div>\n");
			} else {
				text.append("''Nothing found''\n");
			}

			// Syntactic similarity
			text.append("== Syntactic Similarity (Word2Vec) ==\n");
			if (graphNearestWordsCountSyntactic > 0) {
				// Create graph with nodes and edges
				LemmaInfos.LemmaPos words[] = new LemmaInfos.LemmaPos[graphNearestWordsCountSyntactic];
				graph.start(lemmapos);
				for (int i = 0; i < graphNearestWordsCountSyntactic; i++) {
					graph.add(new LemmaInfos.LemmaPos(nearestWordsSyntactic[i]), word2VecSyntactic);
				}
				text.append(graph.end());

				// Create a word list with hyperlinks
				text.append("<div class=\"mw-collapsible\" style=\"width:100%;overflow:auto;\">\n")
					.append("<div style=\"font-weight:bold;line-height:1.6;\">Word List</div>\n")
					.append("<div class=\"mw-collapsible-content\">");
				for (int i = 0; i < pageNearestWordsCountSyntactic; i++) {
					LemmaInfos.LemmaPos wordLemmaPos = new LemmaInfos.LemmaPos(nearestWordsSyntactic[i]);
					text.append("[[Lemma:").append(wordLemmaPos).append("|").append(wordLemmaPos.toString(" ")).append("]], ");
				}
				text.replace(text.length() - 2, text.length(), "");
				text.append("</div></div>\n");
			} else {
				text.append("''Nothing found''\n");
			}

			// Concordance
			text.append("== Concordance ==\n")
				.append(info.occurances.size()).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Sentence\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Left Context\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Keyword\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Right Context\n");
			for (LemmaInfos.LemmaInText occurance : info.occurances) {
				text.append("|-\n")
					.append("|align=\"left\"|[[").append(occurance.text).append("#SENTENCE_").append(occurance.sentence).append("|").append(occurance.text).append("]]\n")
					.append("|align=\"right\"|").append(occurance.sentence).append("\n")
					.append("|align=\"right\"|").append(occurance.leftContext).append("\n")
					.append("|align=\"center\"|").append(occurance.keyword).append("\n")
					.append("|align=\"left\"|").append(occurance.rightContext).append("\n");
			}
			text.append("|}\n");

			// Feature vectors
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			text.append("== Feature Vectors ==\n")
				.append("<div class=\"mw-collapsible mv-collapsed\" style=\"width:100%;overflow:auto;\">\n")
				.append("<div style=\"font-weight:bold;line-height:1.6;\">Paradigmatic Similarity (Word2Vec)</div>\n")
				.append("<div class=\"mw-collapsible-content mv-collapsed\" style=\"display:none;\">[ ");
			double[] vector = word2VecParadigmatic.getWordVector(lemmapos.toString());
			if (vector != null && vector.length > 0) {
				for (double d : vector) {
					text.append(decimalFormat.format(d)).append(", ");
				}
				text.replace(text.length() - 2, text.length(), "");
			}
			text.append(" ]</div></div>\n")
				.append("<div class=\"mw-collapsible mv-collapsed\" style=\"width:100%;overflow:auto;\">\n")
				.append("<div style=\"font-weight:bold;line-height:1.6;\">Syntactic Similarity (Word2Vec)</div>\n")
				.append("<div class=\"mw-collapsible-content mv-collapsed\" style=\"display:none;\">[ ");
			vector = word2VecSyntactic.getWordVector(lemmapos.toString());
			if (vector != null && vector.length > 0) {
				for (double d : vector) {
					text.append(decimalFormat.format(d)).append(", ");
				}
				text.replace(text.length() - 2, text.length(), "");
			}
			text.append(" ]</div></div>\n");
			
			// TODO: invalid page names: :_: #_# [_-LRB- ]_-RRB-
			writePage(lemmaPrefix + lemmapos, "Generated Lemma page", text.toString(), nsLemma);

			/*
			 * Create a tooltip for this lemma
			 */
			text.replace(0, text.length(), "");
			text.append("{|\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Name\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|POS\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Syntactic Words\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Frequency\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|F. Class\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Text F.\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Inverse Document F.\n")
				.append("|-\n")
				.append("|[[Lemma:").append(lemmapos).append("|").append(lemmapos.lemma).append("]]\n")
				.append("|").append(lemmapos.pos).append("\n")
				.append("|").append(info.morphologicalFeatures.size()).append("\n")
				.append("|").append(info.frequency).append("\n")
				.append("|").append(info.getFrequencyClass()).append("\n")
				.append("|").append(info.getDocumentFrequency()).append("\n")
				.append("|").append(info.getInverseDocumentFrequencyAsString(documentCount)).append("\n")
				.append("|}\n");

			// Paradigmatic similarity
			text.append("== Paradigmatic Similarity ==\n");
			if (tooltipNearestWordsCountParadigmatic > 0) {
				// Create a word list with hyperlinks
				for (int i = 0; i < tooltipNearestWordsCountParadigmatic; i++) {
					LemmaInfos.LemmaPos wordLemmaPos = new LemmaInfos.LemmaPos(nearestWordsParadigmatic[i]);
					text.append("[[Lemma:").append(wordLemmaPos).append("|").append(wordLemmaPos.toString(" ")).append("]], ");
				}
				text.replace(text.length() - 2, text.length(), "\n");
			} else {
				text.append("''Nothing found''\n");
			}

			// Syntactic similarity
			text.append("== Syntactic Similarity ==\n");
			if (tooltipNearestWordsCountSyntactic > 0) {
				// Create a word list with hyperlinks
				for (int i = 0; i < tooltipNearestWordsCountSyntactic; i++) {
					LemmaInfos.LemmaPos wordLemmaPos = new LemmaInfos.LemmaPos(nearestWordsSyntactic[i]);
					text.append("[[Lemma:").append(wordLemmaPos).append("|").append(wordLemmaPos.toString(" ")).append("]], ");
				}
				text.replace(text.length() - 2, text.length(), "\n");
			} else {
				text.append("''Nothing found''\n");
			}

			// TODO: invalid page names: :_: #_# [_-LRB- ]_-RRB-
			writePage("Tooltip:Lemma_" + lemmapos, "Generated Lemma tooltip", text.toString(), nsTooltip);
		}
	}

	// To write pages for texts, Corpus - overview page and DDC-Categories page 
	// Title of page, comment for this page, the body text of page  
	private void writePage(String pageTitle, String comment, String textBufferString, String pageNs) {
		writePage(pageTitle, comment, textBufferString, pageNs, "wikitext");
	}

	private void writePage(String pageTitle, String comment, String textBufferString, String pageNs, String model) {
		if (pageTitle == null || pageTitle.equals("")) {
			System.out.println(" BUG  | MediaWikiWriter tries to create a page with no title:");
			System.out.println("      | Namespace: " + pageNs);
			System.out.println("      | Comment:   " + comment);
			System.out.println("      | Text:      " + textBufferString != null ? textBufferString.substring(0, 20).replace("\n", " ") : null);
			return;
		}

		//Define ID for the pages 
		String pageId = String.valueOf(pageIdGlobal);
		pageIdGlobal++;

		String revisionId = "1";
		String revParentId = "";	// No revision...

		//To save the time 
		String revTimestamp = Instant.now().toString();

		// TODO SHA1 from a text? Or from all?
		String sha1String = Base64.getEncoder().encodeToString((md.digest(textBufferString.getBytes())));

		//The header for all sub-pages
		writer.println("<page>");
		writer.println(" <title>" + pageTitle + "</title>");
		writer.println(" <ns>" + pageNs + "</ns>");
		writer.println(" <id>" + pageId + "</id>");
		writer.println(" <revision>");
		writer.println("  <id>" + revisionId + "</id>");
		writer.println("  <parentid>" + revParentId + "</parentid>");
		writer.println("  <timestamp>" + revTimestamp + "</timestamp>");
		writer.println("  <contributor>");
		writer.println("   <username>" + username + "</username>");
		writer.println("   <id>" + userid + "</id>");
		writer.println("  </contributor>");
		writer.println("  <minor />");
		writer.println("  <comment>" + comment + "</comment>");
		writer.println("  <model>" + model + "</model>");
		if (model == "wikitext") {
			writer.println("  <format>text/x-wiki</format>");
		} else {
			writer.println("  <format>text/" + model + "</format>");
		}
		writer.println("  <text xml:space=\"preserve\"><![CDATA[\n" + textBufferString.replace("]]>", "]] >").trim() + "\n]]></text>");
		writer.println("  <sha1>" + sha1String + "</sha1>");
		writer.println(" </revision>");
		writer.println("</page>");
	}

	//This process is to get all information from TextImager-Client about the document, paragraphs, sentences and words
	// All this information is in jCas 
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		DocumentMetaData meta = DocumentMetaData.get(jCas);		 
		String lang = meta.getLanguage();

		String pageTitle = meta.getDocumentId().replaceAll(" ", "_").replaceAll("%20", "_");
		pageTitle = pageTitle.replace(".txt", "");

		// To separate and remember sub-pages to create the overview pages later
		String[] split = pageTitle.split("/", -1);
		// If it is greater than 1, than there are sub-pages 
		if (split.length > 1) {
			String level = "";
			//The last one is the name of page 
			for (int ind = 0; ind < split.length-1; ++ind) {
				String s = level + split[ind];
				
				// To add the next deep level  
				if (!folderPages.containsKey(s)) {
					folderPages.put(s, new HashSet<String>());
				}
				folderPages.get(s).add(s + "/" + split[ind+1]);
				
				level += s + "/";
			}
		} else {
			if (!folderPages.containsKey("")) {
				folderPages.put("", new HashSet<String>());
			}
			folderPages.get("").add(split[0]);
		}

		String comment = "Generated from file " + meta.getDocumentUri();

		StringBuilder pageBuilder = new StringBuilder();
		HashSet<String> categories = new HashSet<String>();

		// Get all wikipedia links from TagMeLocalAnnotator
		ArrayList<WikipediaLink> wikipediaLinks = new ArrayList<WikipediaLink>();
		wikipediaLinks.addAll(JCasUtil.select(jCas, WikipediaLink.class));
		int startingWikipediaLinksSize = wikipediaLinks.size();
		System.out.println(" INFO | MediawikiWriter got " + wikipediaLinks.size() + " Wikipedia links from TagMeAnnotator for text " + pageTitle);

		// Get all categories for the document (sorted by their plausibility)
		SortedDDCSet ddcs = new SortedDDCSet();
		for (CategoryCoveredTagged cct : JCasUtil.select(jCas, CategoryCoveredTagged.class)) {
			if (cct.getStart() == 0 && cct.getEnd() == jCas.getDocumentText().length()) {
				ddcs.add(cct);
			}
		}
		pageBuilder.append("{{#textinfo: DDC");
		for (CategoryCoveredTagged cct : ddcs) {
			String ddc = cct.getValue().replaceAll("__label_ddc__", "");
			pageBuilder.append(":DDC").append(ddc).append(" ").append(MediawikiDDCHelper.getDDCClassName(ddc).replace(";", ",").replace(":", " -")).append(";");
			ddcInfos.get(ddc).documents.add(pageTitle);
		}
		pageBuilder.append(" }}\n\n");

		int paragraphN = 0;
		int sentenceN = 0;
		// Inhalt: Paragraphenweise alle Token + Lemma als Tooltip
		for (Paragraph paragraph : JCasUtil.select(jCas, Paragraph.class)) {
			StringBuilder paragraphBuilder = new StringBuilder();
			paragraphBuilder.append("{{#paragraph: ").append(paragraphN).append(" | START");
			// TODO Seperate Typen für DDC Kategorien und Wikipedia Disambiguation
			// TODO Disambiguation Links
			// DDC Kategorien: Von jedem Paragraphen den besten
			String paragraphCategory = null;
			{
				SortedDDCSet paragraphCats = new SortedDDCSet();
				paragraphCats.addAll(JCasUtil.selectCovered(CategoryCoveredTagged.class, paragraph));
				if (!paragraphCats.isEmpty()) {
					paragraphCategory = paragraphCats.get(0).getValue().replaceAll("__label_ddc__", "");
					categories.add("[[" + categoryPrefix + "DDC" + paragraphCategory + "]]");
					// To add DDC information of paragraph to START tag of paragraph 
					paragraphBuilder.append(" | DDC:").append(paragraphCategory)
						.append("_").append(MediawikiDDCHelper.getDDCClassName(paragraphCategory));
				}
			}
			paragraphBuilder.append("}} \n");

			for (Sentence sentence : JCasUtil.selectCovered(Sentence.class, paragraph)) {
				StringBuilder sentenceBuilder = new StringBuilder();
				sentenceBuilder.append("{{#sentence: ").append(sentenceN).append(" | START }}");

				ArrayList<String> sentenceTokens = new ArrayList<String>();
				ArrayList<LemmaInfos.LemmaPos> sentenceLemmas = new ArrayList<LemmaInfos.LemmaPos>();
				ArrayList<LemmaInfos.LemmaInfo> sentenceLemmaInfos = new ArrayList<LemmaInfos.LemmaInfo>();

				StringBuilder namedEntityBuilder = new StringBuilder();
				int firstNamedEntityToken = -1;

				int tokenN = 0;
				boolean inLink = false, closeLink = false;
				for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
					StringBuilder tokenBuilder = new StringBuilder();
					if (wikipediaLinks.size() > 0) {
						if (!inLink && wikipediaLinks.get(0).getStart() <= token.getStart()) {
							// start a new link
							tokenBuilder.append("[").append("https://").append(lang).append(".wikipedia.org/wiki/").append(wikipediaLinks.get(0).getTarget());
							inLink = true;
							if (token.getEnd() >= wikipediaLinks.get(0).getEnd()) {
								// close the link, because it is just one token long
								closeLink = true;
							}
						} else if (inLink && token.getEnd() >= wikipediaLinks.get(0).getEnd()) {
							closeLink = true;
						}
					}
					String text = token.getCoveredText();
					LemmaInfos.LemmaPos lemmapos = lemmaInfos.createLemmaPos(token);
					LemmaInfos.LemmaInfo lemmaInfo = lemmaInfos.get(lemmapos);

					// count lemma frequency
					lemmaInfo.frequency++;
					lemmaInfo.containingDocuments.add(pageTitle);

					tokenBuilder.append("{{#word: ").append(text)
						.append(" |lemma:").append(lemmapos.lemma)
						.append(",pos:").append(lemmapos.pos);

					// collect morphological features
					try {
						// try to get features from token.getMorph()
						EnhancedMorphologicalFeatures morph = lemmaInfo.addMorphologicalFeatures(text, token.getMorph());
					} catch (Exception e) {
						// try to get features from covered
						List<MorphologicalFeatures> morphFeatures = JCasUtil.selectCovered(MorphologicalFeatures.class, token);
						boolean gotMorphologicalFeatures = false;
						if (morphFeatures != null && morphFeatures.size() > 0) {
							for (MorphologicalFeatures morph : morphFeatures) {
								try {
									EnhancedMorphologicalFeatures m = lemmaInfo.addMorphologicalFeatures(text, morph);
									gotMorphologicalFeatures = true;
									break;
								} catch (IllegalArgumentException iae) {}
							}
						}
						if (!gotMorphologicalFeatures && !failedLemmaPosMorphologicalFeatures.contains(lemmapos)) {
							failedLemmaPosMorphologicalFeatures.add(lemmapos);
							System.out.println(" WARN | MediawikiWriter could not get morphological features for " + text + " (Lemma_POS: " + lemmapos + ")");
						}
					}

				    for (NamedEntity ne : JCasUtil.selectCovered(NamedEntity.class, token)) {
						tokenBuilder.append(",NE:").append(ne.getValue());
					}

					tokenBuilder.append("}}");
					if (closeLink) {
						inLink = false;
						closeLink = false;
						tokenBuilder.append("]");
						wikipediaLinks.remove(0);
					}
					tokenBuilder.append(" ");

					String tokenString = tokenBuilder.toString();
					sentenceLemmas.add(lemmapos);
					sentenceLemmaInfos.add(lemmaInfo);
					sentenceTokens.add(tokenString);
					// append the token to the complete text
					sentenceBuilder.append(tokenString);
					tokenN++;
				}

				// create entries for every keyword in the sentence
				StringBuilder leftContext = new StringBuilder();
				for (int i = 0; i < sentenceLemmas.size(); i++) {
					StringBuilder rightContext = new StringBuilder();
					for (int j = i + 1; j < sentenceLemmas.size(); j++) {
						rightContext.append(sentenceTokens.get(j));
					}
					lemmaInfos.get(sentenceLemmas.get(i)).addOccurance(pageTitle, sentenceN, leftContext.toString(), sentenceTokens.get(i), rightContext.toString());
					leftContext.append(sentenceTokens.get(i));
				}

				sentenceBuilder.append("{{#sentence: ").append(sentenceN).append(" | END ");
				String sentenceCategory = null;
				{
					ArrayList<CategoryCoveredTagged> sentenceCats = new ArrayList<CategoryCoveredTagged>();
					sentenceCats.addAll(JCasUtil.selectCovered(CategoryCoveredTagged.class, sentence));
					if (!sentenceCats.isEmpty()) {
						Collections.sort(sentenceCats, (r1, r2) -> ((r1.getScore() > r2.getScore()) ? -1 : ((r1.getScore() < r2.getScore()) ? 1 : 0)));
						sentenceCategory = sentenceCats.get(0).getValue().replaceAll("__label_ddc__", "");
						sentenceBuilder.append(" | DDC:").append(sentenceCategory).append("_")
							.append(MediawikiDDCHelper.getDDCClassName(sentenceCategory));
					}
				}

				sentenceBuilder.append("}} ");
				String sentenceString = sentenceBuilder.toString();
				if (sentenceCategory != null) {
					ddcInfos.get(sentenceCategory).addToSentences(pageTitle, paragraphN, sentenceN, sentenceString);
				}
				paragraphBuilder.append(sentenceString);
				sentenceN++;
			}

			paragraphBuilder.append("\n")
				.append("{{#paragraph: ").append(paragraphN).append(" | END }} ");
			String paragraphString = paragraphBuilder.toString();
			if (paragraphCategory != null) {
				ddcInfos.get(paragraphCategory).addToParagraphs(pageTitle, paragraphN, paragraphString);
			}
			pageBuilder.append(paragraphString).append("\n\n");
			paragraphN++;
		}

		for (String category : categories) {
			pageBuilder.append(category).append("\n");
		}
		pageBuilder.append("\n\n\n");
		writePage(pageTitle, comment, pageBuilder.toString(), nsPage);
		documentCount++;
	}

	private <K,T> void addToMappedList(HashMap<K, ArrayList<T>> mappedList, K key, T value) {
		if (!mappedList.containsKey(key)) {
			mappedList.put(key, new ArrayList<T>());
		}
		mappedList.get(key).add(value);
	}

	private <K,T> void addToMappedSet(HashMap<K, Set<T>> mappedList, K key, T value) {
		if (!mappedList.containsKey(key)) {
			mappedList.put(key, new TreeSet<T>());
		}
		mappedList.get(key).add(value);
	}

}
