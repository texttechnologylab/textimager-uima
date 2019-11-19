//package textimager.uima.io.abby;
//
//import com.google.common.io.Files;
//import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.Anomaly;
//import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SuggestedAction;
//import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
//import org.apache.commons.lang3.NotImplementedException;
//import org.apache.commons.lang3.StringEscapeUtils;
//import org.apache.uima.UIMAException;
//import org.apache.uima.UIMA_UnsupportedOperationException;
//import org.apache.uima.UimaContext;
//import org.apache.uima.collection.CollectionException;
//import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
//import org.apache.uima.fit.descriptor.ConfigurationParameter;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.jcas.JCas;
//import org.apache.uima.jcas.cas.FSArray;
//import org.apache.uima.resource.ResourceInitializationException;
//import org.apache.uima.util.Progress;
//import org.apache.uima.util.ProgressImpl;
//import org.texttechnologylab.annotation.ocr.OCRDocument;
//import org.texttechnologylab.annotation.ocr.OCRLine;
//import org.texttechnologylab.annotation.ocr.OCRPage;
//import org.texttechnologylab.annotation.ocr.OCRToken;
//import org.xml.sax.SAXException;
//import textimager.uima.io.abby.annotation.*;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static textimager.uima.io.abby.utility.Util.*;
//
///**
// * JCasCollectionReader to read a single article from multiple XML files.
// */
//public class CollectionReader extends JCasCollectionReader_ImplBase {
//	/**
//	 * Folder path containing all article pages as ABBYY FineReader XML files.
//	 */
//	public static final String PARAM_SOURCE_LOCATION = "pCollectionRootDir";
//	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION)
//	private String pCollectionRootDir;
//
//	public static final String PARAM_DICT_PATH = "pDictPath";
//	@ConfigurationParameter(name = PARAM_DICT_PATH, mandatory = false, defaultValue = "")
//	protected String pDictPath;
//	public static final String PARAM_MIN_TOKEN_CONFIDENCE = "pMinTokenConfidence";
//	@ConfigurationParameter(name = PARAM_MIN_TOKEN_CONFIDENCE, mandatory = false, defaultValue = "80")
//	protected Integer pMinTokenConfidence;
//	public static final String PARAM_USE_LANGUAGE_TOOL = "pUseLanguageTool";
//	@ConfigurationParameter(name = PARAM_USE_LANGUAGE_TOOL, mandatory = false, defaultValue = "false")
//	protected Boolean pUseLanguageTool;
//	public static final String PARAM_CHAR_LEFT_MAX = "pCharLeftMax";
//	@ConfigurationParameter(name = PARAM_CHAR_LEFT_MAX, mandatory = false, defaultValue = "99999")
//	protected Integer pCharLeftMax;
//	public static final String PARAM_BLOCK_TOP_MIN = "pBlockTopMin";
//	@ConfigurationParameter(name = PARAM_BLOCK_TOP_MIN, mandatory = false, defaultValue = "0")
//	protected Integer pBlockTopMin;
//	public static final String PARAM_MIN_LINE_LETTER_RATIO = "pMinLineLetterRatio";
//	@ConfigurationParameter(name = PARAM_MIN_LINE_LETTER_RATIO, mandatory = false, defaultValue = "1")
//	protected Double pMinLineLetterRatio;
//	public static final String PARAM_MIN_LINE_ALNUM_RATIO = "pMinLineAlnumRatio";
//	@ConfigurationParameter(name = PARAM_MIN_LINE_ALNUM_RATIO, mandatory = false, defaultValue = "2")
//	protected Double pMinLineAlnumRatio;
//	public static final String PARAM_MIN_CHARACTERS_PER_TOKEN = "pMinCharactersPerToken";
//	@ConfigurationParameter(name = PARAM_MIN_CHARACTERS_PER_TOKEN, mandatory = false, defaultValue = "3")
//	protected Double pMinCharactersPerToken;
//	public static final String PARAM_MULTI_DOC = "pMultiDoc";
//	@ConfigurationParameter(name = PARAM_MULTI_DOC, mandatory = false, defaultValue = "false")
//	protected Boolean pMultiDoc;
//	public static final String PARAM_USE_OLD_GARBAGE_DETECTION = "pUseOldGarbageDetection";
//	@ConfigurationParameter(name = PARAM_USE_OLD_GARBAGE_DETECTION, mandatory = false, defaultValue = "false")
//	protected Boolean pUseOldGarbageDetection;
//	public static final String PARAM_UNESCAPE_HTML = "pUnescapeHTML";
//	@ConfigurationParameter(name = PARAM_UNESCAPE_HTML, mandatory = false, defaultValue = "true")
//	protected Boolean pUnescapeHTML;
//	private HashSet<String> dict;
//	private ProgressImpl progress;
//
//
//	public void initialize(UimaContext context) throws ResourceInitializationException {
//		super.initialize(context);
//
//		ArrayList<String> inputFiles = new ArrayList<>();
//		try {
//			Files.fileTraverser().depthFirstPreOrder(new File(pCollectionRootDir))
//					.forEach(f -> inputFiles.add(f.toString()));
//			progress = new ProgressImpl(0, inputFiles.size(), "XMLs");
//			process(inputFiles);
//		} catch (Exception e) {
//			throw new ResourceInitializationException(e);
//		}
//	}
//
//	public void process(ArrayList<String> pInputPaths) throws UIMAException {
//		try {
//			if (!pDictPath.isEmpty()) {
//				dict = loadDict(pDictPath);
//			}
////			JLanguageTool langTool = new JLanguageTool(new org.languagetool.language.GermanyGerman()); // FIXME: LanguageTool error
//			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//			SAXParser saxParser = saxParserFactory.newSAXParser();
//
//			final HashMap<String, FineReaderExportHandler> pages = new HashMap<>(pInputPaths.size());
//			boolean lastTokenWasSpace = false;
//
//			for (String pagePath : pInputPaths) {
//				FineReaderExportHandler fineReaderExportHandler = getExportHandler(saxParser, pagePath, pCharLeftMax, pBlockTopMin, lastTokenWasSpace);
//				pages.put(pagePath, fineReaderExportHandler);
//				lastTokenWasSpace = fineReaderExportHandler.lastTokenWasSpace;
//			}
//
//			// Check if any of the files contains more than one document. TODO: implement multi page documents
//			if (pages.values().stream().anyMatch(page -> page.pages.size() > 1)) {
//				String invalidPages = pages.entrySet().stream().filter(entry -> entry.getValue().pages.size() > 1).map(Map.Entry::getKey).collect(Collectors.joining("; "));
//				throw new UIMA_UnsupportedOperationException(new NotImplementedException("Input documents may not contain more than one page.\nDocuments in question: " + invalidPages));
//			}
//
//			// build collection SOFA string from individual pages
//			final StringBuilder textBuilder = new StringBuilder();
//			for (String pagePath : pInputPaths) {
//				textBuilder.append(pages.get(pagePath).tokens.stream().map(Token::getTokenString).collect(Collectors.joining("")));
//			}
//			String text = textBuilder.toString();
//
//			// Remove HTML escapes
//			if (pUnescapeHTML) {
//				text = StringEscapeUtils.unescapeHtml4(text);
//			}
//
//			// Set SOFA string
//			JCas jCas = JCasFactory.createText(text);
//
//			int lastOffset = 0;
//			HashMap<String, OCRDocument> documentLookup = new HashMap<>();
//			OCRDocument lastDocument = null;
//			String lastDocumentParent = null;
//
//			for (int i = 0; i < pInputPaths.size(); i++) {
//				String pageInputPath = pInputPaths.get(i);
//				FineReaderExportHandler fineReaderExportHandler = pages.get(pageInputPath);
//				String pageId = Paths.get(pageInputPath).getFileName().toString();
//
//				Page page = fineReaderExportHandler.pages.get(0);
//				page.pageId = pageId;
//				page.pageNumber = i;
//				OCRPage ocrPage = page.wrap(jCas, lastOffset);
//				jCas.addFsToIndexes(ocrPage);
//
//				for (Block block : fineReaderExportHandler.blocks) {
//					jCas.addFsToIndexes(block.wrap(jCas, lastOffset));
//				}
//				for (Paragraph paragraph : fineReaderExportHandler.paragraphs) {
//					jCas.addFsToIndexes(paragraph.wrap(jCas, lastOffset));
//				}
//				for (Line line : fineReaderExportHandler.lines) {
//					OCRLine ocrLine = line.wrap(jCas, lastOffset);
//					jCas.addFsToIndexes(ocrLine);
//					detectGarbageLine(jCas, ocrLine);
//				}
//				for (Token token : fineReaderExportHandler.tokens) {
//					if (token.isSpace())
//						continue;
//
//					OCRToken ocrToken = token.wrap(jCas, lastOffset);
//					jCas.addFsToIndexes(ocrToken);
//
//					for (OCRToken subtoken : token.wrapSubtokens(jCas, lastOffset)) {
//						jCas.addFsToIndexes(subtoken);
//					}
//
//					if (dict != null) {
//						boolean inDict = inDict(token.getTokenString(), dict);
//						if (!inDict && (token.getAverageCharConfidence() < pMinTokenConfidence || !(token.isWordNormal || token.isWordFromDictionary || token.isWordNumeric))) {
//							tagGarbageLine(jCas, String.format("AvgTokenConfidence:%f, isWordNormal:%b, isWordFromDictionary:%b, inDict:%b, isWordNumeric:%b, suspiciousChars:%d",
//									token.getAverageCharConfidence(), token.isWordNormal, token.isWordFromDictionary, inDict, token.isWordNumeric, token.suspiciousChars), token.start, token.end, "BioFID_Abby_Token_Heuristic", token.getTokenString());
//						}
//					}
////					else if (false && token.containsHyphen() || token.subTokenStrings().size() > 1) { // FIXME
////						NamedEntity annotation = new NamedEntity(jCas, token.start, token.end);
////						annotation.setValue(String.format("AvgTokenConfidence:%f, isWordNormal:%b, isWordFromDictionary:%b, inDict:%b, isWordNumeric:%b, suspiciousChars:%d, containsHyphen:%b, subTokens:%s",
////								token.getAverageCharConfidence(), token.isWordNormal, token.isWordFromDictionary, inDict, token.isWordNumeric, token.suspiciousChars, token.containsHyphen(), token.subTokenStrings()));
////						jCas.addFsToIndexes(annotation);
////					}
//				}
//
//				/* Every parent directory denotes its own Document annotation, recurring directories will get expanded each time */
//				String currentDocumentPath = Paths.get(pageInputPath).getParent().toString();
//				String currentDocumentName = Paths.get(pageInputPath).getParent().getFileName().toString();
//				if (pMultiDoc) {
//					if (Objects.nonNull(lastDocument)) {
//						endDocuments(jCas, ocrPage.getEnd(), documentLookup, currentDocumentPath);
//					}
//					if (documentLookup.containsKey(currentDocumentPath)) {
//						lastDocument = documentLookup.get(currentDocumentPath);
//					} else {
//						lastDocument = new OCRDocument(jCas);
//						lastDocument.setBegin(lastOffset);
//						lastDocument.setDocumentname(currentDocumentName);
//						documentLookup.put(currentDocumentPath, lastDocument);
//					}
//				}
//				lastDocumentParent = currentDocumentPath;
//				lastOffset = ocrPage.getEnd();
//				progress.increment(1);
//			}
//			if (Objects.nonNull(lastDocument)) {
//				endDocuments(jCas, lastOffset, documentLookup, lastDocumentParent);
//			}
//
//			// FIXME: LanguageTool
//			if (pUseLanguageTool) {
////				languageToolSpellcheck(jCas, langTool, text);
//			}
//
//		} catch (SAXException | ParserConfigurationException | IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void endDocuments(JCas aJCas, int lastOffset, HashMap<String, OCRDocument> documentLookup, String lastDocumentParent) {
//		Path tempPath = Paths.get(lastDocumentParent);
//		while (!tempPath.equals(Paths.get(pCollectionRootDir).toAbsolutePath())) {
//			if (documentLookup.containsKey(tempPath.toString())) {
//				OCRDocument tempDocument = documentLookup.get(tempPath.toString());
//
//				aJCas.removeFsFromIndexes(tempDocument);
//				tempDocument.setEnd(lastOffset);
//				aJCas.addFsToIndexes(tempDocument);
//			}
//			tempPath = tempPath.getParent();
//		}
//	}
//
//	private void tagGarbageLine(JCas jCas, String description, int begin, int end, String anomalyType, String replacement) {
//		Anomaly anomaly = new Anomaly(jCas, begin, end);
//		anomaly.setCategory(anomalyType);
//		anomaly.setDescription(description);
//		SuggestedAction suggestedAction = new SuggestedAction(jCas);
//		suggestedAction.setReplacement(replacement);
//		FSArray fsArray = new FSArray(jCas, 1);
//		fsArray.set(0, suggestedAction);
//		anomaly.setSuggestions(fsArray);
//		jCas.addFsToIndexes(anomaly);
//	}
//
//	private void detectGarbageLine(JCas jCas, OCRLine ocrLine) {
//		boolean bool;
//		int textLength = jCas.getDocumentText().length();
//
//		if (textLength < ocrLine.getBegin() || textLength < ocrLine.getEnd()) {
//			System.err.printf("[%s] Annotation '%s' exceeds SOFA string length of %d with begin/end %d/%d!\n",
//					DocumentMetaData.get(jCas).getDocumentId(), ocrLine.toString(), textLength, ocrLine.getBegin(), ocrLine.getEnd());
//			return;
//		}
//		try {
//			String coveredText = ocrLine.getCoveredText();
//
//			boolean numberTable = weirdNumberTable.matcher(coveredText).matches();
//			bool = !numberTable || yearPattern.matcher(coveredText).matches();
//
//			boolean letterTable = weirdLetterTable.matcher(coveredText).matches();
//			bool &= !letterTable;
//
////			int letterCount = countMatches(letterPattern.matcher(coveredText));
////			int otherCount = countMatches(otherPattern.matcher(coveredText));
////			double letterRatio = letterCount / (1d * otherCount);
////			bool &= letterRatio >= pMinLineLetterRatio;
////
////			double charactersPerToken = coveredText.length() / (1d * coveredText.split("\\s+").length);
////			bool &= charactersPerToken >= pMinCharactersPerToken;
////
////			int alnumCount = countMatches(alnumPattern.matcher(coveredText));
////			int nonAlnumCount = countMatches(nonAlnumPattern.matcher(coveredText));
////			double alnumRatio = alnumCount * 1d / nonAlnumCount;
////			bool &= alnumRatio >= pMinLineAlnumRatio;
//
//			if (!bool) {
////				String description = String.format("letterRatio:%03f, charactersPerToken:%03f, alnumRatio:%03f, weirdNumberTable:%b", letterRatio, charactersPerToken, alnumRatio, !(numberTable && letterRatio >= pMinLineLetterRatio * 2));
//				String description = String.format("weirdNumberTable:%b, weirdLetterTable:%b", numberTable, letterTable);
//				tagGarbageLine(jCas, description, ocrLine.getBegin(), ocrLine.getEnd(), "BioFID_Garbage_Line_Anomaly", "");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void getNext(JCas jCas) throws IOException, CollectionException {
//
//	}
//
//	@Override
//	public boolean hasNext() throws IOException, CollectionException {
//		return false;
//	}
//
//	@Override
//	public Progress[] getProgress() {
//		return new Progress[]{progress};
//	}
//}
