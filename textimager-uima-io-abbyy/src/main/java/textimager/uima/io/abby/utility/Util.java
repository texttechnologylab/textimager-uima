package textimager.uima.io.abby.utility;

import com.google.common.base.Strings;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;
import org.apache.uima.jcas.JCas;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.texttechnologylab.annotation.ocr.OCRDocument;
import org.texttechnologylab.annotation.ocr.OCRToken;
import org.xml.sax.SAXException;
import textimager.uima.io.abby.FineReaderExportHandler;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.SAXParser;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

abstract public class Util {
	
	public static final Pattern weirdNumberTable = Pattern.compile("^[\\t \\d\\pP\\pS]+$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern weirdLetterTable = Pattern.compile("^(\\S{1,2} )+$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern yearPattern = Pattern.compile("^[ \\t]*.?\\pN{4}.?[ \\t]*$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern letterPattern = Pattern.compile("[\\p{Alpha} ,.\\-]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern otherPattern = Pattern.compile("[^\\p{Alpha} ,.\\-]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern alnumPattern = Pattern.compile("[\\p{Alnum} ,.\\-]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern nonAlnumPattern = Pattern.compile("[^\\p{Alnum} ,.\\-]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern wordPattern = Pattern.compile("(?:[\\p{Z}\\-_]|^)(?:[\\p{L}]+|[\\p{Nd}]+)|(?:[\\p{L}]+|[\\p{Nd}]+)(?:[\\p{Zs}\\-_]|[\\n\\r\\f]$)", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern spacePattern = Pattern.compile("[ \\t]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern tokenPattern = Pattern.compile("(?:[\\p{Z}]|^)[\\p{L}\\p{P}\\p{Sm}\\p{N}\\p{Sc}♂♀¬°½±^]+|[\\p{L}\\p{P}\\p{Sm}\\p{N}\\p{Sc}♂♀¬°½±^]+(?:[\\p{Zs}]|[\\n\\r\\f]$)",
			Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern allNonSpacePattern = Pattern.compile("[^\\p{Z}]", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern nonGarbageLine = Pattern.compile("^[\\w\\p{Z}♂♀¬°½±]{3,}$|^[\\w\\p{Z}\\p{P}\\p{Sm}\\p{N}\\p{Sc}♂♀¬°½±^]{5,}$|^[\\p{Z}]*$|^[\\p{N}\\p{Punct}\\p{Z}]+$", Pattern.UNICODE_CHARACTER_CLASS);
	
	public static final Predicate<File> isLeafDir = dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles())).noneMatch(File::isDirectory);
	
	public static int parseInt(String s) {
		return Strings.isNullOrEmpty(s) ? 0 : Integer.parseInt(s);
	}
	
	public static float parseFloat(String s) {
		return Strings.isNullOrEmpty(s) ? 0f : Float.parseFloat(s);
	}
	
	public static boolean parseBoolean(String s) {
		return !Strings.isNullOrEmpty(s) && Boolean.parseBoolean(s);
	}
	
	@NotNull
	public static FineReaderExportHandler getExportHandler(SAXParser saxParser, String pagePath, Integer pCharLeftMax, Integer pBlockTopMin, boolean pLastTokenWasSpace) throws SAXException, IOException {
		FineReaderExportHandler fineReaderExportHandler = new FineReaderExportHandler();
		fineReaderExportHandler.lastTokenWasSpace = pLastTokenWasSpace;
		fineReaderExportHandler.charLeftMax = pCharLeftMax;
		fineReaderExportHandler.blockTopMin = pBlockTopMin;
		InputStream inputStream = Files.newInputStream(Paths.get(pagePath), StandardOpenOption.READ);
		saxParser.parse(inputStream, fineReaderExportHandler);
		return fineReaderExportHandler;
	}
	
	public static void languageToolSpellcheck(JCas aJCas, JLanguageTool langTool, StringBuilder text) throws IOException {
		List<RuleMatch> ruleMatches = langTool.check(text.toString(), false, JLanguageTool.ParagraphHandling.NORMAL);
		for (RuleMatch ruleMatch : ruleMatches) {
			SpellingAnomaly spellingAnomaly = new SpellingAnomaly(aJCas, ruleMatch.getFromPos(), ruleMatch.getToPos());
			spellingAnomaly.setDescription(String.format("Message:%s, SuggestedReplacements:%s",
					ruleMatch.getMessage(), ruleMatch.getSuggestedReplacements()));
			aJCas.addFsToIndexes(spellingAnomaly);
		}
	}
	
	
	public static HashSet<String> loadDict(String pDictPath) throws IOException {
		HashSet<String> dict = new HashSet<>();
		if (pDictPath != null) {
			try (BufferedReader br = new BufferedReader(new FileReader(new File(pDictPath)))) {
				dict = br.lines().map(String::trim).collect(Collectors.toCollection(HashSet::new));
			}
		}
		return dict;
	}
	
	public static boolean inDict(String token, HashSet<String> dict) {
		return inDict(token, dict, true);
	}
	
	public static boolean inDict(String token, HashSet<String> dict, boolean lowerCase) {
		Pattern pattern = Pattern.compile("[^-\\p{Alnum}]", Pattern.UNICODE_CHARACTER_CLASS);
		String word = pattern.matcher(token).replaceAll("");
		word = lowerCase ? word.toLowerCase() : word;
		return dict != null && !word.isEmpty() && dict.contains(word);
	}
	
	public static void writeToFile(Path targetFilePath, String content) {
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(targetFilePath), StandardCharsets.UTF_8))) {
			pw.print(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(Path targetFilePath, Iterable<String> lines) {
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(targetFilePath), StandardCharsets.UTF_8))) {
			for (String line : lines) {
				pw.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@NotNull
	private static <T, S> HashMap<T, Collection<S>> revertMapOfCollections(Map<S, Collection<T>> sCollectionTMap) {
		HashMap<T, Collection<S>> hashMap = new HashMap<>();
		sCollectionTMap.forEach((s, ts) -> ts.forEach(t -> {
			Collection<S> sCollection = hashMap.getOrDefault(t, new ArrayList<>());
			sCollection.add(s);
			hashMap.put(t, sCollection);
		}));
		return hashMap;
	}
	
	public static int countMatches(Matcher matcher) {
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
	
	public static void processDocument(OCRDocument ocrDocument, final Map<OCRDocument, Collection<OCRToken>> documentMap, final Set<OCRToken> tokenCovering, final Set<OCRToken> anomalies, final StringBuilder retStringBuilder) {
		for (OCRToken ocrToken : documentMap.get(ocrDocument)) {
			if (tokenCovering.contains(ocrToken) || anomalies.contains(ocrToken)) {
				continue;
			}
			retStringBuilder.append(ocrToken.getCoveredText()).append(" ");
		}
	}
	
	public static int getRelativeDepth(File root, File file) {
		return getRelativeDepth(root.toPath(), file.toPath());
	}
	
	public static int getRelativeDepth(String root, File file) {
		return getRelativeDepth(Paths.get(root), file.toPath());
	}
	
	/**
	 * Convinience method to compute the length of the path between two paths.
	 *
	 * @param stem the stem path.
	 * @param leaf the leaf path.
	 * @return the length of the path stem->leaf.
	 */
	public static int getRelativeDepth(Path stem, Path leaf) {
		return leaf.relativize(stem).getNameCount();
	}
}