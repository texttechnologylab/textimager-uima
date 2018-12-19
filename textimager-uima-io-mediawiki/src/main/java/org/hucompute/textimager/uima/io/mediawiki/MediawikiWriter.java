package org.hucompute.textimager.uima.io.mediawiki;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

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
	
	
	private MessageDigest md;
	private long pageIdGlobal = 0;
	private PrintWriter writer;
	private HashMap<String, HashSet<String>> folderPages;
	
	private static final String generatorVersion = "org.hucompute.textimager.uima.io.mediawiki.MediawikiWriter 1.1";
	
	private static final String nsPage = "0";
	private static final String nsCategory = "14";
	
	private static final String categoryPrefix = "Category:";
	
	@Override
	public void destroy() {
		// Unterordner Seiten aufbauen
		StringBuilder corpusTextBuilder = new StringBuilder();
		for (HashMap.Entry<String, HashSet<String>> entry : folderPages.entrySet()) {
			StringBuilder textBuilder = new StringBuilder();
			
			for (String page : entry.getValue()) {
				textBuilder.append("* [[").append(page).append("]]\n");
			}
			
			String comment = "Generated from directory " + entry.getKey();
			
			writePage(entry.getKey(), comment, textBuilder.toString(), nsPage);
			
			if (!entry.getKey().isEmpty()) {
				corpusTextBuilder.append("'''").append(entry.getKey()).append("'''").append("\n\n");
			}
			corpusTextBuilder.append(textBuilder).append("\n\n");
		}
		writePage("Corpus", "Corpus overview", corpusTextBuilder.toString(), nsPage);
				
		// Alle DDC Kategorieseiten erstellen
		for (HashMap.Entry<String, String> entry : MediawikiDDCHelper.getAllDDCClasses().entrySet()) {
			StringBuilder text = new StringBuilder();
			if (!entry.getKey().endsWith("0")) {
				text.append(MediawikiDDCHelper.getDDCClassName(entry.getKey().substring(0, 2) + "0")).append(": ");
			}
			text.append(entry.getValue());
			writePage(categoryPrefix + "DDC" + entry.getKey(), "Generated DDC categoy", text.toString(), nsCategory);
		}
				
		writer.println("</mediawiki>");

		writer.flush();
		writer.close();
		
		super.destroy();
	}
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
				
		File outputDir = new File(targetLocation);
		outputDir.mkdirs();
		File outputDumpFile = new File(targetLocation + "/output.wiki.xml");
		
		String sitename = outputDir.getName();
		String dbname = outputDir.getName().toLowerCase().replaceAll(" ", "_");
		String base = outputDir.getAbsolutePath().replaceAll(" ", "_");
		
		folderPages = new HashMap<String, HashSet<String>>();
		
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
	
	private void writePage(String pageTitle, String comment, String textBufferString, String pageNs) {
		
		String pageId = String.valueOf(pageIdGlobal);
		pageIdGlobal++;
		
		String revisionId = "1";
		String revParentId = "";	// Gibt keine Revision...

		String revTimestamp = Instant.now().toString();
		
		// TODO SHA1 vom Text? Oder von allem?
		String sha1String = Base64.getEncoder().encodeToString((md.digest(textBufferString.getBytes())));

		writer.println("<page>");
		writer.println("<title>" + pageTitle + "</title>");
		writer.println("<ns>" + pageNs + "</ns>");
		writer.println("<id>" + pageId + "</id>");
		writer.println("<revision>");
		writer.println("<id>" + revisionId + "</id>");
		writer.println("<parentid>" + revParentId + "</parentid>");
		writer.println("<timestamp>" + revTimestamp + "</timestamp>");
		writer.println("<contributor>");
		writer.println("<username>" + username + "</username>");
		writer.println("<id>" + userid + "</id>");
		writer.println("</contributor>");
		writer.println("<minor />");
		writer.println("<comment>" + comment + "</comment>");
		writer.println("<model>wikitext</model>");
		writer.println("<format>text/x-wiki</format>");
		writer.println("<text xml:space=\"preserve\">" + textBufferString + "</text>");
		writer.println("<sha1>" + sha1String + "</sha1>");
		writer.println("</revision>");
		writer.println("</page>");
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		
		DocumentMetaData meta = DocumentMetaData.get(jCas);		 

		String pageTitle = meta.getDocumentId().replaceAll(" ", "_").replaceAll("%20", "_");

		// Unterordner trennen und merken um später die Übersichtsseiten zu erstellen
		String[] split = pageTitle.split("/", -1);
		// Wenn mehr als 1, dann haben wir Unterordner
		if (split.length > 1) {
			String level = "";
			// Letztes ist der Seitenname...
			for (int ind = 0; ind < split.length-1; ++ind) {
				String s = level + split[ind];
				
				// Nächste Tiefere Ebene hinzufügen
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
		
		StringBuffer textBuffer = new StringBuffer();
		StringBuffer catBuffer = new StringBuffer();
		
		// Inhalt: Paragraphenweise alle Token + Lemma als Tooltip
		for (Paragraph paragraph : JCasUtil.select(jCas, Paragraph.class)) {
			// DDC Kategorien: Von jedem Paragraphen den besten
			// TODO Seperate Typen für DDC Kategorien und Wikipedia Disambiguation
			// TODO Disambiguation Links
			{
				ArrayList<CategoryCoveredTagged> paragraphCats = new ArrayList<CategoryCoveredTagged>();
				paragraphCats.addAll(JCasUtil.selectCovered(CategoryCoveredTagged.class, paragraph));
				if (!paragraphCats.isEmpty()) {
					Collections.sort(paragraphCats, (r1, r2) -> ((r1.getScore() > r2.getScore()) ? -1 : ((r1.getScore() < r2.getScore()) ? 1 : 0)));
					CategoryCoveredTagged cat = paragraphCats.get(0);
					catBuffer.append("[[").append(categoryPrefix).append("DDC").append(cat.getValue().replaceAll("__label_ddc__", "")).append("]]\n");
				}
			}
			
			for (Sentence sentence : JCasUtil.selectCovered(Sentence.class, paragraph)) {

				textBuffer.append("<span class=\"sentence\">");
				
				for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
										
					// {{#tip-text:  findest |lemma:finden }}
					textBuffer.append("{{#tip-text: ").append(token.getCoveredText())
						.append(" |lemma:").append(token.getLemma().getValue())
						.append(",pos:").append(token.getPos().getPosValue())
						.append("}}");
					
					textBuffer.append(" ");
				}
				
				textBuffer.append("</span>");
			}

			textBuffer.append("\n\n");
		}
		
		textBuffer.append(catBuffer.toString());
		
		writePage(pageTitle, comment, textBuffer.toString(), nsPage);
	}
}
