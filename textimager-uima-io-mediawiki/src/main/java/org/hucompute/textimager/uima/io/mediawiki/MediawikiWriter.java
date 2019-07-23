package org.hucompute.textimager.uima.io.mediawiki;

import java.io.File;   
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Math;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
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

	private class LemmaInText {
		public String text;
		public String leftContext;
		public String rightContext;
		public String keyword;
		public int sentence;
		public LemmaInText(String text, int sentence, String leftContext, String keyword, String rightContext) {
			this.text = text;
			this.sentence = sentence;
			this.leftContext = leftContext;
			this.rightContext = rightContext;
			this.keyword = keyword;
		}
	}
	
	private class LemmaFrequency {
		public int frequency = 1;
		public TreeSet<String> texts = new TreeSet<String>();
		public double getInverseDocumentFrequency(int docCount) {
			return Math.log(docCount / (1 + texts.size()));
		}
	}

	private class LemmaPos {
		public String lemma;
		public String pos;
		public LemmaPos(Token token) {
			lemma = token.getLemma().getValue();
			pos = token.getPos().getPosValue();
		}
		public boolean equals(Object obj) {
			return obj instanceof LemmaPos && toString().equals(((LemmaPos) obj).toString());
		}
		public String toString() {
			return lemma + "_" + pos;
		}
	}

	private class OccuranceInText {
		public String text;
		public int paragraph, sentence;
		public String occurance;
		public OccuranceInText(String text, int paragraph, int sentence, String occurance) {
			this.text = text;
			this.paragraph = paragraph;
			this.sentence = sentence;
			this.occurance = occurance.replace("\n", " ");
		}
	}
		
		
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
	
	
	private MessageDigest md;
	private long pageIdGlobal = 0;
	// Variable to add lines of the output file step by step
	private PrintWriter writer;
	// To save names of all text files 
	private HashMap<String, HashSet<String>> folderPages;
	// To save every entity belonging to a DDC
	private HashMap<String, ArrayList<String>> ddcTexts;
	private HashMap<String, ArrayList<OccuranceInText>> ddcSentences;
	private HashMap<String, ArrayList<OccuranceInText>> ddcParagraphs;
	// To create lemma pages with links to all texts
	private HashMap<String, ArrayList<LemmaInText>> lemmaFolders;
	private HashMap<String, Boolean> validWikipediaLemmas;
	// Count Frequencies and Text Frequencies of Lemmas
	private HashMap<String, LemmaFrequency> lemmaFrequencies;
	// Collect morphological features for every lemma
	private HashMap<LemmaPos, Set<MorphologicalFeatures>> lemmaMorphologicalFeatures;
	
	private static final String generatorVersion = "org.hucompute.textimager.uima.io.mediawiki.MediawikiWriter 1.1";
	
	private static final String nsPage = "0";
	private static final String nsCategory = "14";
	private static final String nsLemma = "102";	

	private static final String categoryPrefix = "Category:";
	private static final String lemmaPrefix = "Lemma:";

	private int documentCount = 0;

	@Override
	public void destroy() {
		// To build all sub-pages for texts 
		
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
		//Building the Corpus-page
		writePage("Corpus", "Corpus overview", corpusTextBuilder.toString(), nsPage);
				
		//Pages for all DDC-Categories 
		writeDDCPages();

		//Pages for all Lemmas
		writeLemmaPages();
				
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
		ddcTexts = new HashMap<String, ArrayList<String>>();
		ddcSentences = new HashMap<String, ArrayList<OccuranceInText>>();
		ddcParagraphs = new HashMap<String, ArrayList<OccuranceInText>>();
		lemmaFolders = new HashMap<String, ArrayList<LemmaInText>>();
		lemmaFrequencies = new HashMap<String, LemmaFrequency>();
		validWikipediaLemmas = new HashMap<String, Boolean>();
		lemmaMorphologicalFeatures = new HashMap<LemmaPos, Set<MorphologicalFeatures>>();
		
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
			int level = id.substring(2, 3).equals("0") ? (id.substring(1, 2).equals("0") ? 1 : 2) : 3;
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
			ArrayList<String> texts = ddcTexts.get(id);
			pageBuilder.append("== Texts ==\n")
				.append(texts != null ? texts.size() : 0).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n");
			if (texts != null) {
				for (String text : texts) {
					pageBuilder.append("|-\n");
					pageBuilder.append("|align=\"left\"|[[").append(text).append("]]\n");
				}
			}
			pageBuilder.append("|}\n");

			// List paragraphs of this category
			ArrayList<OccuranceInText> paragraphs = ddcParagraphs.get(id);
			pageBuilder.append("== Paragraphs ==\n")
				.append(paragraphs != null ? paragraphs.size() : 0).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Paragraph\n");
			if (paragraphs != null) {
				for (OccuranceInText occurance : ddcParagraphs.get(id)) {
					pageBuilder.append("|-\n");
					pageBuilder.append("|align=\"left\"|[[").append(occurance.text).append("#PARAGRAPH_").append(occurance.paragraph).append("|").append(occurance.text).append("]]\n")
						.append("|align=\"left\"|").append(occurance.occurance).append("\n");
				}
			}
			pageBuilder.append("|}\n");

			// List sentences of this category
			ArrayList<OccuranceInText> sentences = ddcSentences.get(id);
			pageBuilder.append("== Sentences ==\n")
				.append(sentences != null ? sentences.size() : 0).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Sentence\n");
			if (sentences != null) {
				for (OccuranceInText occurance : ddcSentences.get(id)) {
					pageBuilder.append("|-\n");
					pageBuilder.append("|align=\"left\"|[[").append(occurance.text).append("#SENTENCE_").append(occurance.sentence).append("|").append(occurance.text).append("]]\n")
						.append("|align=\"left\"|").append(occurance.occurance).append("\n");
				}
			}
			pageBuilder.append("|}\n");

			// List texts for this category
			writePage(categoryPrefix + "DDC" + entry.getKey(), "Generated DDC category", pageBuilder.toString(), nsCategory);
		}
	}

	/** Write a page for every lemma. */
	private void writeLemmaPages() {
		System.out.println("INFO | MediaWikiWriter write lemma pages for " + documentCount + " documents");
		for (HashMap.Entry<String, ArrayList<LemmaInText>> entry : lemmaFolders.entrySet()) {
			String[] lemmapos = entry.getKey().split("_");
			String lemma = entry.getKey(), pos = "";
			if (lemmapos.length == 2) {
				lemma = lemmapos[0];
				pos = lemmapos[1];
			} else {
				System.out.println("BUG  | MediaWikiWriter got malformed Lemma_POS: " + entry.getKey());
			}
			boolean isVerb = pos.equals("V");
			ArrayList<LemmaInText> textOccurances = entry.getValue();
			LemmaFrequency frequency = lemmaFrequencies.get(entry.getKey());
			if (frequency == null) {
				System.out.println("BUG  | MediaWikiWriter got a lemma but no frequency for it: " + lemma);
				frequency = new LemmaFrequency();
				frequency.frequency = 0;
			}
			Set<MorphologicalFeatures> morph = lemmaMorphologicalFeatures.get(lemma);
			if (morph == null) {
				morph = new TreeSet<MorphologicalFeatures>();
			}

			StringBuilder text = new StringBuilder();
			text.append("{{#lemmainfo: ")
				.append("Name:").append(lemma).append(",")
				.append("Part of Speech:").append(pos).append(",")
				.append("SyntacticWords:").append(morph.size()).append(",")
				.append("Frequency Class:").append(",") // TODO calculate frequency class
				.append("Frequency:").append(frequency.frequency).append(",")
				.append("Text Frequency:").append(frequency.texts.size()).append(",")
				.append("Inverse Document Frequency:").append((new DecimalFormat("0.0")).format(frequency.getInverseDocumentFrequency(documentCount))).append(",")
				.append("Wiktionary:WIKTIONARY en ").append(lemma) // TODO needs right language but does its job nonetheless
				.append("}}\n\n")
				.append("== Morphology ==\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|form\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|").append(isVerb ? "mood" : "case").append("\n")
				.append(isVerb ? "" : "!bgcolor=#F2F2F2 align=\"left\"|gender\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|number\n")
				.append(isVerb ? "!bgcolor=#F2F2F2 align=\"left\"|person\n" : "")
				.append("!bgcolor=#F2F2F2 align=\"left\"|pos\n")
				.append(isVerb ? "!bgcolor=#F2F2F2 align=\"left\"|tense\n" : "");
			for (MorphologicalFeatures features : morph) {
				text.append("|-\n")
					.append("|align=\"left\"|").append(features.getVerbForm()).append("\n")
					.append("|align=\"left\"|").append(isVerb ? features.getMood() : features.getCase()).append("\n");
				if (!isVerb) {
					text.append("|align=\"left\"|").append(features.getGender()).append("\n");
				}
				text.append("|align=\"left\"|").append(features.getNumber()).append("\n");
				if (isVerb) {
					text.append("|align=\"left\"|").append(features.getPerson()).append("\n");
				}
				text.append("|align=\"left\"|").append(pos).append("\n");
				if (isVerb) {
					text.append("|align=\"left\"|").append(features.getTense()).append("\n");
				}
			}
			text.append("|}\n")
				.append("== Concordance ==\n")
				.append(textOccurances.size()).append(" entries total<br/>\n")
				.append("{| class=\"mw-collapsible\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" valign=\"top\"\n")
				.append("!bgcolor=#F2F2F2 align=\"left\"|Document\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Sentence\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Left Context\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Keyword\n")
				.append("!bgcolor=#F2F2F2 align=\"center\"|Right Context\n");
			for (LemmaInText occurance : textOccurances) {
				text.append("|-\n")
					.append("|align=\"left\"|[[").append(occurance.text).append("#SENTENCE_").append(occurance.sentence).append("|").append(occurance.text).append("]]\n")
					.append("|align=\"right\"|").append(occurance.sentence).append("\n")
					.append("|align=\"right\"|").append(occurance.leftContext).append("\n")
					.append("|align=\"center\"|").append(occurance.keyword).append("\n")
					.append("|align=\"left\"|").append(occurance.rightContext).append("\n");
			}
			text.append("|}\n");
			// TODO: invalid page names: :_: #_# [_-LRB- ]_-RRB-
			writePage("Lemma:" + entry.getKey(), "Generated Lemma page", text.toString(), nsLemma);
		}
	}

	// To write pages for texts, Corpus - overview page and DDC-Categories page 
	// Title of page, comment for this page, the body text of page  
	private void writePage(String pageTitle, String comment, String textBufferString, String pageNs) {
		if (pageTitle == null || pageTitle.equals("")) {
			System.out.println("BUG  | MediaWikiWriter tries to create a page with no title:");
			System.out.println("     | Namespace: " + pageNs);
			System.out.println("     | Comment:   " + comment);
			System.out.println("     | Text:      " + textBufferString != null ? textBufferString.substring(0, 20) : null);
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
		writer.println("  <model>wikitext</model>");
		writer.println("  <format>text/x-wiki</format>");
		writer.println("  <text xml:space=\"preserve\"><![CDATA[" + textBufferString + "]]></text>");
		writer.println("  <sha1>" + sha1String + "</sha1>");
		writer.println(" </revision>");
		writer.println("</page>");
	}
	
	//This process is to get all information from TextImager-Client about the document, paragraphs, sentences and words
	// All this information is in jCas 
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		
		DocumentMetaData meta = DocumentMetaData.get(jCas);		 
//		String lang = jCas.getDocumentLanguage();
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
		//Generative information about the document
		//TODO get DDCs and add text to ddcTexts
		
		TreeSet<String> ddcs = new TreeSet<String>();
		for (CategoryCoveredTagged cat : JCasUtil.selectCovered(CategoryCoveredTagged.class, meta)) { // FIXME this creates no hits
			System.out.println("=> Got category from meta: " + cat.getValue()); // FIXME
			ddcs.add(cat.getValue().replaceAll("__label_ddc__", ""));
		}
		for (Document doc : JCasUtil.select(jCas, Document.class)) { // FIXME this creates too many hits
			System.out.println("=> Document: " + doc.getStart() + ":" + doc.getEnd());
			for (CategoryCoveredTagged cat : JCasUtil.selectCovered(CategoryCoveredTagged.class, doc)) {
				System.out.println("=> Got category from doc: " + cat.getValue()); // FIXME
				ddcs.add(cat.getValue().replaceAll("__label_ddc__", ""));
			}
		}
//		pageBuilder.append("\n\n");
//		pageBuilder.append("{{#textinfo: ").append(" }}");
//		pageBuilder.append("\n\n");
		pageBuilder.append("{{#textinfo: DDC");
		for (String cat : ddcs) {
			pageBuilder.append(":DDC").append(cat).append(" ").append(MediawikiDDCHelper.getDDCClassName(cat).replace(";", ",").replace(":", " -")).append(";");
			addToMappedList(ddcTexts, cat, pageTitle);
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
				ArrayList<CategoryCoveredTagged> paragraphCats = new ArrayList<CategoryCoveredTagged>();
				paragraphCats.addAll(JCasUtil.selectCovered(CategoryCoveredTagged.class, paragraph));
				if (!paragraphCats.isEmpty()) {
					Collections.sort(paragraphCats, (r1, r2) -> ((r1.getScore() > r2.getScore()) ? -1 : ((r1.getScore() < r2.getScore()) ? 1 : 0)));
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
				ArrayList<String> sentenceLemmas = new ArrayList<String>();

				StringBuilder namedEntityBuilder = new StringBuilder();
				int firstNamedEntityToken = -1;

				int tokenN = 0;
				boolean inLink = false, closeLink = false;
				for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
					System.out.println("Token: " + token.getCoveredText() + " (" + token.getStart() + "-" + token.getEnd());
					StringBuilder tokenBuilder = new StringBuilder();
					if (wikipediaLinks.size() > 0) {
						if (!inLink && wikipediaLinks.get(0).getStart() == token.getStart()) {
							tokenBuilder.append("[").append("https://").append(lang).append(".wikipedia.org/wiki/").append(wikipediaLinks.get(0).getTarget());
							inLink = true;
							System.out.println("WikiLink: " + wikipediaLinks.get(0).getCoveredText() + " (" + wikipediaLinks.get(0).getStart() + "-" + wikipediaLinks.get(0).getEnd());
							if (token.getEnd() >= wikipediaLinks.get(0).getEnd()) {
								closeLink = true;
							}
						} else if (inLink && token.getEnd() >= wikipediaLinks.get(0).getEnd()) {
							closeLink = true;
						}
					}
					String text = token.getCoveredText();
					String lemma = token.getLemma().getValue();
					String pos = token.getPos().getPosValue();
					LemmaPos lemmapos = new LemmaPos(token);

					// collect morphological features
					//addToMappedSet(lemmaMorphologicalFeatures, lemmapos, token.getMorphologicalFeatures());
					
					// count lemma frequency
					if(lemmaFrequencies.get(lemma + "_" + pos) == null) {
						lemmaFrequencies.put(lemma + "_" + pos, new LemmaFrequency());
					} else {
						lemmaFrequencies.get(lemma + "_" + pos).frequency++;
					}
					lemmaFrequencies.get(lemma + "_" + pos).texts.add(pageTitle);
					
					tokenBuilder.append("{{#word: ").append(text)
						.append(" |lemma:").append(lemma)
						.append(",pos:").append(pos);
					try{
						tokenBuilder.append(",morph:"+token.getMorph().getValue());
					}catch (NullPointerException e) {}
						
						// {{#tip-text:  findest |lemma:finden }}
						//textBuffer.append("{{#tip-text: ").append(token.getCoveredText())
						//	.append(" |lemma:").append(token.getLemma().getValue())
						//	.append(",pos:").append(token.getPos().getPosValue());
									
				    for (NamedEntity ne : JCasUtil.selectCovered(NamedEntity.class, token)) {
	                    tokenBuilder.append(",NE:").append(ne.getValue());
	                }
				    
					tokenBuilder.append("}}");
					if (closeLink) {
						tokenBuilder.append("]");
						inLink = closeLink = false;
						wikipediaLinks.remove(0);
					}
					tokenBuilder.append(" ");

					String tokenString = tokenBuilder.toString();
					sentenceLemmas.add(lemma + "_" + pos);
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
					addToMappedList(lemmaFolders, sentenceLemmas.get(i), new LemmaInText(pageTitle, sentenceN, leftContext.toString(), sentenceTokens.get(i), rightContext.toString()));
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
					addToMappedList(ddcSentences, sentenceCategory, new OccuranceInText(pageTitle, paragraphN, sentenceN, sentenceString));
				}
				paragraphBuilder.append(sentenceString);
				sentenceN++;
			}
			
			paragraphBuilder.append("\n")
				.append("{{#paragraph: ").append(paragraphN).append(" | END }} ");
			String paragraphString = paragraphBuilder.toString();
			if (paragraphCategory != null) {
				addToMappedList(ddcParagraphs, paragraphCategory, new OccuranceInText(pageTitle, paragraphN, -1, paragraphString));
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
