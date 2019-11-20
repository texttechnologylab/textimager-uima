package textimager.uima.io.abby;

import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.Anomaly;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SuggestedAction;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.uima.UIMA_UnsupportedOperationException;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.texttechnologylab.annotation.ocr.OCRDocument;
import org.texttechnologylab.annotation.ocr.OCRLine;
import org.texttechnologylab.annotation.ocr.OCRPage;
import org.texttechnologylab.annotation.ocr.OCRToken;
import org.xml.sax.SAXException;
import textimager.uima.io.abby.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static textimager.uima.io.abby.utility.Util.*;

/**
 * JCasCollectionReader to read a single article from multiple XML files.
 */
public class DocumentReader extends JCasCollectionReader_ImplBase {
	/**
	 * Folder path containing all article pages as ABBYY FineReader XML files.
	 */
	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION)
	private String sourceLocation;
	
	/**
	 * Set this as the language of the produced documents. Default "de".
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "de")
	private String language;
	
	/**
	 * Path to a dictionary of common words.
	 */
	public static final String PARAM_DICT_PATH = "pDictPath";
	@ConfigurationParameter(name = PARAM_DICT_PATH, mandatory = false, defaultValue = "")
	protected String pDictPath;
	
	/**
	 * Minimum average confidence for a Token. Tokens with less confidence are tagged as anomalies.
	 */
	public static final String PARAM_MIN_TOKEN_CONFIDENCE = "pMinTokenConfidence";
	@ConfigurationParameter(name = PARAM_MIN_TOKEN_CONFIDENCE, mandatory = false, defaultValue = "80")
	protected Integer pMinTokenConfidence;
	
	/**
	 * If true, use LanguageTool spellcheck for anomaly detection. Currently broken so this parameter does nothing.
	 */
	public static final String PARAM_USE_LANGUAGE_TOOL = "pUseLanguageTool";
	@ConfigurationParameter(name = PARAM_USE_LANGUAGE_TOOL, mandatory = false, defaultValue = "false")
	protected Boolean pUseLanguageTool;
	
	/**
	 * Maximum pixel offset for a characters left boundary.
	 */
	public static final String PARAM_CHAR_LEFT_MAX = "pCharLeftMax";
	@ConfigurationParameter(name = PARAM_CHAR_LEFT_MAX, mandatory = false, defaultValue = "99999")
	protected Integer pCharLeftMax;
	
	/**
	 * Minimum pixel offset for a blocks upper boundary.
	 */
	public static final String PARAM_BLOCK_TOP_MIN = "pBlockTopMin";
	@ConfigurationParameter(name = PARAM_BLOCK_TOP_MIN, mandatory = false, defaultValue = "0")
	protected Integer pBlockTopMin;
	
	@Deprecated
	public static final String PARAM_MIN_LINE_LETTER_RATIO = "pMinLineLetterRatio";
	@ConfigurationParameter(name = PARAM_MIN_LINE_LETTER_RATIO, mandatory = false, defaultValue = "1")
	protected Double pMinLineLetterRatio;
	
	@Deprecated
	public static final String PARAM_MIN_LINE_ALNUM_RATIO = "pMinLineAlnumRatio";
	@ConfigurationParameter(name = PARAM_MIN_LINE_ALNUM_RATIO, mandatory = false, defaultValue = "2")
	protected Double pMinLineAlnumRatio;
	
	@Deprecated
	public static final String PARAM_MIN_CHARACTERS_PER_TOKEN = "pMinCharactersPerToken";
	@ConfigurationParameter(name = PARAM_MIN_CHARACTERS_PER_TOKEN, mandatory = false, defaultValue = "3")
	protected Double pMinCharactersPerToken;
	
	/**
	 * Some characters may be escaped HTML sequences. Set this parameter to true to unescape them during parsing.
	 */
	public static final String PARAM_UNESCAPE_HTML = "pUnescapeHTML";
	@ConfigurationParameter(name = PARAM_UNESCAPE_HTML, mandatory = false, defaultValue = "true")
	protected Boolean pUnescapeHTML;
	
	/**
	 * Word dictionary to extend the isWordFromDictionary annotation
	 */
	private HashSet<String> dict;
	private ProgressImpl progress;
	private String documentId;
	private ArrayList<String> inputFiles;
	private boolean stillHasNext = true;
	
	
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		
		inputFiles = new ArrayList<>();
		try (Stream<Path> files = Files.list(Paths.get(sourceLocation))) {
			files.forEachOrdered(f -> inputFiles.add(f.toString()));
			progress = new ProgressImpl(0, inputFiles.size(), "XMLs");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	public void process(JCas jCas, ArrayList<String> inputFiles, String documentId) throws IOException, ParserConfigurationException, SAXException {
		if (!pDictPath.isEmpty()) {
			dict = loadDict(pDictPath);
		}
//			JLanguageTool langTool = new JLanguageTool(new org.languagetool.language.GermanyGerman()); // FIXME: LanguageTool error
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		
		final HashMap<String, FineReaderExportHandler> pages = new HashMap<>(inputFiles.size());
		boolean lastTokenWasSpace = false;
		
		for (String pagePath : inputFiles) {
			FineReaderExportHandler fineReaderExportHandler = getExportHandler(saxParser, pagePath, pCharLeftMax, pBlockTopMin, lastTokenWasSpace);
			pages.put(pagePath, fineReaderExportHandler);
			lastTokenWasSpace = fineReaderExportHandler.lastTokenWasSpace;
		}
		
		// Check if any of the files contains more than one document. TODO: implement multi page documents
		if (pages.values().stream().anyMatch(page -> page.pages.size() > 1)) {
			String invalidPages = pages.entrySet().stream().filter(entry -> entry.getValue().pages.size() > 1).map(Map.Entry::getKey).collect(Collectors.joining("; "));
			throw new UIMA_UnsupportedOperationException(new NotImplementedException("Input documents may not contain more than one page.\nDocuments in question: " + invalidPages));
		}
		
		// build collection SOFA string from individual pages
		final StringBuilder textBuilder = new StringBuilder();
		for (String pagePath : inputFiles) {
			textBuilder.append(pages.get(pagePath).tokens.stream().map(Token::getTokenString).collect(Collectors.joining("")));
		}
		String text = textBuilder.toString();
		
		// Remove HTML escapes
		if (pUnescapeHTML) {
			text = StringEscapeUtils.unescapeHtml4(text);
		}
		
		// Set SOFA string
		jCas.setDocumentText(text);
		
		// Iterate over pages
		int lastOffset = 0;
		for (int i = 0; i < inputFiles.size(); i++) {
			String pageInputPath = inputFiles.get(i);
			FineReaderExportHandler fineReaderExportHandler = pages.get(pageInputPath);
			String pageId = Paths.get(pageInputPath).getFileName().toString();
			
			Page page = fineReaderExportHandler.pages.get(0);
			page.pageId = pageId;
			page.pageNumber = i;
			OCRPage ocrPage = page.wrap(jCas, lastOffset);
			jCas.addFsToIndexes(ocrPage);
			
			for (Block block : fineReaderExportHandler.blocks) {
				jCas.addFsToIndexes(block.wrap(jCas, lastOffset));
			}
			for (Paragraph paragraph : fineReaderExportHandler.paragraphs) {
				jCas.addFsToIndexes(paragraph.wrap(jCas, lastOffset));
			}
			for (Line line : fineReaderExportHandler.lines) {
				OCRLine ocrLine = line.wrap(jCas, lastOffset);
				jCas.addFsToIndexes(ocrLine);
				detectGarbageLine(jCas, ocrLine);
			}
			for (Token token : fineReaderExportHandler.tokens) {
				if (token.isSpace())
					continue;
				
				OCRToken ocrToken = token.wrap(jCas, lastOffset);
				jCas.addFsToIndexes(ocrToken);
				
				for (OCRToken subtoken : token.wrapSubtokens(jCas, lastOffset)) {
					jCas.addFsToIndexes(subtoken);
				}
				
				if (dict != null) {
					boolean inDict = inDict(token.getTokenString(), dict);
					if (!inDict && (token.getAverageCharConfidence() < pMinTokenConfidence || !(token.isWordNormal || token.isWordFromDictionary || token.isWordNumeric))) {
						tagGarbageLine(jCas, String.format("AvgTokenConfidence:%f, isWordNormal:%b, isWordFromDictionary:%b, inDict:%b, isWordNumeric:%b, suspiciousChars:%d",
								token.getAverageCharConfidence(), token.isWordNormal, token.isWordFromDictionary, inDict, token.isWordNumeric, token.suspiciousChars), token.start, token.end, "BioFID_Abby_Token_Heuristic", token.getTokenString());
					}
				}
			}
			lastOffset = ocrPage.getEnd();
			progress.increment(1);
		}
		OCRDocument ocrDocument = new OCRDocument(jCas);
		ocrDocument.setBegin(0);
		ocrDocument.setEnd(jCas.getDocumentText().length());
		ocrDocument.setDocumentname(documentId);
		jCas.addFsToIndexes(ocrDocument);
		
		// FIXME: LanguageTool
		if (pUseLanguageTool) {
//				languageToolSpellcheck(jCas, langTool, text);
		}
	}
	
	private void tagGarbageLine(JCas jCas, String description, int begin, int end, String anomalyType, String replacement) {
		Anomaly anomaly = new Anomaly(jCas, begin, end);
		anomaly.setCategory(anomalyType);
		anomaly.setDescription(description);
		SuggestedAction suggestedAction = new SuggestedAction(jCas);
		suggestedAction.setReplacement(replacement);
		FSArray fsArray = new FSArray(jCas, 1);
		fsArray.set(0, suggestedAction);
		anomaly.setSuggestions(fsArray);
		jCas.addFsToIndexes(anomaly);
	}
	
	private void detectGarbageLine(JCas jCas, OCRLine ocrLine) {
		boolean bool;
		int textLength = jCas.getDocumentText().length();
		
		if (textLength < ocrLine.getBegin() || textLength < ocrLine.getEnd()) {
			System.err.printf("[%s] Annotation '%s' exceeds SOFA string length of %d with begin/end %d/%d!\n",
					DocumentMetaData.get(jCas).getDocumentId(), ocrLine.toString(), textLength, ocrLine.getBegin(), ocrLine.getEnd());
			return;
		}
		try {
			String coveredText = ocrLine.getCoveredText();
			
			boolean numberTable = weirdNumberTable.matcher(coveredText).matches();
			bool = !numberTable || yearPattern.matcher(coveredText).matches();
			
			boolean letterTable = weirdLetterTable.matcher(coveredText).matches();
			bool &= !letterTable;

			if (!bool) {
//				String description = String.format("letterRatio:%03f, charactersPerToken:%03f, alnumRatio:%03f, weirdNumberTable:%b", letterRatio, charactersPerToken, alnumRatio, !(numberTable && letterRatio >= pMinLineLetterRatio * 2));
				String description = String.format("weirdNumberTable:%b, weirdLetterTable:%b", numberTable, letterTable);
				tagGarbageLine(jCas, description, ocrLine.getBegin(), ocrLine.getEnd(), "BioFID_Garbage_Line_Anomaly", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void getNext(JCas jCAS) throws IOException, CollectionException {
		try {
			DocumentMetaData documentMetaData = DocumentMetaData.create(jCAS);
			documentMetaData.setDocumentId(documentId);
			jCAS.setDocumentLanguage(language);
			process(jCAS, inputFiles, documentId);
			stillHasNext = false;
		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
	}
	
	
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return stillHasNext;
	}
	
	@Override
	public Progress[] getProgress() {
		return new Progress[]{progress};
	}
}
