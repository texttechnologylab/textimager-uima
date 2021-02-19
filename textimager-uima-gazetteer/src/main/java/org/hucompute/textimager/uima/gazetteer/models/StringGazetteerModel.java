package org.hucompute.textimager.uima.gazetteer.models;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.Pair;
import org.apache.log4j.Logger;
import org.apache.uima.util.UriUtils;
import org.hucompute.textimager.uima.gazetteer.tree.StringTreeNode;
import org.texttechnologylab.utilities.helper.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StringGazetteerModel implements IGazetteerModel {
	
	public static final Pattern nonTokenCharacterClass = Pattern.compile("[^\\p{Alpha}\\- ]+", Pattern.UNICODE_CHARACTER_CLASS);
	
	protected static final Logger logger = Logger.getLogger(StringGazetteerModel.class);
	protected static Path tempPath;
	protected static Path cachePath;
	protected final ArrayList<String> sourceLocations;
	protected final Boolean useLowercase;
	protected final String language;
	protected final double minLength;
	protected final boolean getAllSkips;
	protected final boolean splitHyphen;
	protected final boolean addAbbreviatedTaxa;
	protected final HashSet<String> filterSet;
	protected final int minWordCountForSkipGrams;
	
	Map<String, String> skipGramTaxonLookup;
	Set<String> sortedSkipGramSet;
	Map<String, HashSet<Object>> taxonUriMap;
	
	/**
	 * Create 1-skip-n-grams from each taxon in a file from a given list of files.
	 *
	 * @param aSourceLocations          An array of UTF-8 file locations containing a list of one taxon and any number
	 *                                  of URIs (comma or space separated) per line.
	 * @param bUseLowercase             If true, use lower cased skip-grams.
	 * @param sLanguage                 The language to be used as locale for lower casing.
	 * @param dMinLength                The minimum skip-gram length. All skip-grams (and taxa) with a length lower than
	 *                                  this will be omitted.
	 * @param bAllSkips                 If true, get all m-skip-n-grams of length n > 2.
	 * @param bSplitHyphen              If true, taxon tokens will be split at hyphens.
	 * @param bAddAbbreviatedTaxa       If true, additionally add taxa with the first token abbreviated.
	 * @param iMinWordCountForSkipGrams The lower bound token count for the skip-gram creation.
	 * @param tokenBoundaryRegex
	 * @param pFilterSet
	 * @throws IOException
	 */
	public StringGazetteerModel(
			String[] aSourceLocations,
			Boolean bUseLowercase,
			String sLanguage,
			double dMinLength,
			boolean bAllSkips,
			boolean bSplitHyphen,
			boolean bAddAbbreviatedTaxa,
			int iMinWordCountForSkipGrams,
			String tokenBoundaryRegex,
			HashSet<String> pFilterSet,
			String gazetteerName
	) throws IOException {
		tempPath = Paths.get("/tmp/" + gazetteerName + "/");
		cachePath = Paths.get(System.getenv("HOME"), ".textimager/gazetteer/" + gazetteerName + "/").toAbsolutePath();

		sourceLocations = getTaxaFiles(aSourceLocations);
		useLowercase = bUseLowercase;
		language = sLanguage;
		minLength = dMinLength;
		getAllSkips = bAllSkips;
		splitHyphen = bSplitHyphen;
		addAbbreviatedTaxa = bAddAbbreviatedTaxa;
		minWordCountForSkipGrams = iMinWordCountForSkipGrams;
		filterSet = pFilterSet;
		
		long startTime = System.currentTimeMillis();
		
		// Map: Taxon -> {URI}
		taxonUriMap = buildTaxaUriMap();
		
		// Map: {Skip-Grams} -> Taxon
		skipGramTaxonLookup = buildSkipGramTaxonLookup();
		
		// Set: {Skip-Gram}
		sortedSkipGramSet = buildSortedSkipGramSet();
		
		logger.info(String.format("Finished loading %d skip-grams from %d taxa in %dms.",
				sortedSkipGramSet.size(), taxonUriMap.size(), System.currentTimeMillis() - startTime)
		);
	}
	
	protected LinkedHashMap<String, HashSet<Object>> buildTaxaUriMap() throws IOException {
		final AtomicInteger duplicateKeys = new AtomicInteger(0);
		final LinkedHashMap<String, HashSet<Object>> lTaxonUriMap = new LinkedHashMap<>();
		
		logger.info(String.format("Loading entries from %d files..", sourceLocations.size()));
		for (int i = 0; i < sourceLocations.size(); i++) {
			String sourceLocation = sourceLocations.get(i);
			logger.info(String.format("[%d/%d] Loading file %s", i + 1, sourceLocations.size(), sourceLocation));
			loadTaxaMap(sourceLocation, useLowercase, language).forEach((taxon, uri) ->
					lTaxonUriMap.merge(taxon, uri, (uUri, vUri) -> {
						duplicateKeys.incrementAndGet();
						return new HashSet<>(SetUtils.union(uUri, vUri));
					}));
		}
		logger.info(String.format("Loaded %d entries from %d files.", lTaxonUriMap.size(), sourceLocations.size()));
		
		if (duplicateKeys.get() > 0)
			logger.warn(String.format("Merged %d duplicate entries!", duplicateKeys.get()));
		
		return lTaxonUriMap;
	}
	
	protected LinkedHashMap<String, String> buildSkipGramTaxonLookup() {
		AtomicInteger duplicateKeys = new AtomicInteger(0);
		final LinkedHashMap<String, String> lSkipGramTaxonLookup = taxonUriMap.keySet().stream()
				.flatMap(s -> getSkipGramsFromTaxon(s, this.addAbbreviatedTaxa, this.minWordCountForSkipGrams, this.getAllSkips, this.splitHyphen)
						.stream().map(val -> new Pair<>(s, val)))
				.collect(Collectors.toMap(
						Pair::getSecond,    // the skip-gram
						Pair::getFirst,     // the corresponding taxon
						(u, v) -> {
							// Drop duplicate skip-grams to ensure bijective skip-gram <-> taxon mapping.
							duplicateKeys.incrementAndGet();
							return null;
						},
						LinkedHashMap::new));
		logger.info(String.format("Ignoring %d duplicate skip-grams!", duplicateKeys.get()));
		
		// Ensure actual taxa are contained in lSkipGramTaxonLookup
		taxonUriMap.keySet().forEach(tax -> lSkipGramTaxonLookup.put(tax, tax));
		
		return lSkipGramTaxonLookup;
	}
	
	protected LinkedHashSet<String> buildSortedSkipGramSet() {
		return skipGramTaxonLookup.keySet().stream()
				.filter(s -> !Strings.isNullOrEmpty(s))
				.filter(s -> s.length() >= this.minLength)
				.sorted(Comparator.comparingInt(String::length).reversed())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	/**
	 * Find this Skip-Grams taxon return its respective URI.
	 *
	 * @param skipGram the target Skip-Gram
	 * @return taxonUriMap.get(skipGramTaxonLookup.get ( skipGram))
	 */
	public Set<Object> getUriFromSkipGram(String skipGram) {
		return taxonUriMap.get(skipGramTaxonLookup.get(skipGram));
	}
	
	protected ArrayList<String> getTaxaFiles(String[] aSourceLocations) throws IOException {
		ArrayList<String> lSourceLocations = new ArrayList<>();
		for (String sourceLocation : aSourceLocations) {
			// If sourceLocation is a valid URL, download the given file
			sourceLocation = downloadTaxaFiles(sourceLocation);
			
			// If zipped extract taxa files to temp folder
			if (sourceLocation.endsWith(".zip")) {
				lSourceLocations.addAll(extractTaxaFiles(sourceLocation));
			} else {
				File sourceLocationFile = new File(sourceLocation);
				if (sourceLocationFile.isDirectory()) {
					String[] list = sourceLocationFile.list();
					if (Objects.isNull(list)) {
						continue;
					}
					for (String file_name : list) {
						lSourceLocations.add(Paths.get(sourceLocation, file_name).toAbsolutePath().toString());
					}
				} else {
					lSourceLocations.add(sourceLocation);
				}
			}
		}
		return lSourceLocations;
	}
	
	/**
	 * Load taxa from UTF-8 file, one taxon per line.
	 *
	 * @return ArrayList of taxa.
	 * @throws IOException if file is not found or an error occurs.
	 */
	protected static LinkedHashMap<String, HashSet<Object>> loadTaxaMap(String sourceLocation, Boolean pUseLowercase, String language) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(sourceLocation)), StandardCharsets.UTF_8))) {
			return bufferedReader.lines()
					.filter(s -> !Strings.isNullOrEmpty(s))
					.collect(Collectors.toMap(
							s -> {
								String taxon = nonTokenCharacterClass.matcher(s.split("\t", 2)[0]).replaceAll("").trim();
								return pUseLowercase ? taxon.toLowerCase(Locale.forLanguageTag(language)) : taxon;
							},
							s -> Arrays.stream(s.split("\t", 2)[1].split("[ ,]")).map(UriUtils::create).collect(Collectors.toCollection(HashSet::new)),
							(u, v) -> new HashSet<>(SetUtils.union(u, v)),
							LinkedHashMap::new)
					);
		}
	}
	
	/**
	 * Attempt to download the taxa files, if the location parameter is a valid URL
	 *
	 * @param sourceLocation
	 * @return
	 * @throws IOException
	 */
	protected String downloadTaxaFiles(String sourceLocation) throws IOException {
		try {
			URL url = new URL(sourceLocation);
			String taxaLocation = getTaxaLocation().toString();
			logger.info(String.format("Downloading '%s'..", sourceLocation));
			sourceLocation = FileUtils.downloadFile(taxaLocation, url.toString(), false).toString();
			logger.info(String.format("Finished download of '%s'.", sourceLocation));
		} catch (MalformedURLException ignored) {
		}
		return sourceLocation;
	}
	
	protected static ArrayList<String> extractTaxaFiles(String sourceLocation) throws IOException {
		logger.info(String.format("Extracting taxa files from '%s'..", sourceLocation));
		
		File gazetteerFolder = getTaxaLocation().toFile();
		ArrayList<String> extractedFiles = new ArrayList<>();
		try (ZipFile zipFile = new ZipFile(sourceLocation)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(gazetteerFolder, entry.getName());
				extractedFiles.add(entryDestination.toString());
				InputStream in = zipFile.getInputStream(entry);
				if (!entryDestination.exists()) {
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					out.close();
				} else {
					logger.info(String.format("File '%s' exists, skipping extraction.", entryDestination));
				}
				in.close();
			}
		}
		logger.info(String.format("Extracted %d taxa files to '%s'.", extractedFiles.size(), sourceLocation));
		return extractedFiles;
	}
	
	/**
	 * Check the possible paths for automatic download and extraction for read/write access and create folders if
	 * necessary.
	 *
	 * @return A valid writable Path.
	 * @throws IOException If neither {@link TreeGazetteerModel#cachePath cachePath} nor {@link
	 *                     TreeGazetteerModel#tempPath tempPath} are writable.
	 */
	protected static Path getTaxaLocation() throws IOException {
		Path gazetteerFolder;
		if (cachePath.toFile().mkdirs() || (Files.isReadable(cachePath) && Files.isWritable(cachePath))) {
			// Check if we have read/write access to the ~/.cache path
			gazetteerFolder = cachePath;
		} else if (tempPath.toFile().mkdirs() || (Files.isReadable(tempPath) && Files.isWritable(tempPath))) {
			// Check if we have read/write access to the /tmp/ path
			gazetteerFolder = tempPath;
		} else {
			throw new IOException(String.format("Could not access output folders!\n" +
							"Please download the taxa files your self and put them in a readable directory.\n" +
							"Access denied: '%s'\nAccess denied: '%s'\n",
					cachePath, tempPath));
		}
		gazetteerFolder.toFile().mkdirs();
		return gazetteerFolder;
	}
	
	/**
	 * Calls {@link #getSkipGramsFromTaxonAsStream getSkipGramsFromTaxonAsStream(String)} and collects the result in a
	 * Set.
	 *
	 * @param pString                  the target String.
	 * @param addAbbreviatedTaxa
	 * @param minWordCountForSkipGrams
	 * @param getAllSkips
	 * @param splitHyphen
	 * @return a List of Strings.
	 */
	public static Set<String> getSkipGramsFromTaxon(String pString, boolean addAbbreviatedTaxa, int minWordCountForSkipGrams, boolean getAllSkips, boolean splitHyphen) {
		HashSet<String> basicSkipGrams = getSkipGramsFromTaxonAsStream(pString, minWordCountForSkipGrams, getAllSkips, splitHyphen, 5);
		
		if (addAbbreviatedTaxa) {
			ArrayList<String> words = getWords(pString, splitHyphen);
			if (words.size() > 1) {
				words.set(0, pString.charAt(0) + ".");
				String abbreviatedString = String.join(" ", words);
				basicSkipGrams.add(abbreviatedString);
				if (words.size() > 2) {
					basicSkipGrams.addAll(getSkipGramsFromTaxonAsStream(abbreviatedString, minWordCountForSkipGrams, getAllSkips, splitHyphen, 5));
				}
			}
		}
		return basicSkipGrams;
	}
	
	/**
	 * Get a List of 1-skip-n-grams for the given string. The string itself is always the first element of the list. The
	 * string is split by whitespaces and all n over n-1 combinations are computed and added to the list. If there is
	 * only a single word in the given string, a singleton list with that word is returned.
	 *
	 * @param pString                  the target String.
	 * @param minWordCountForSkipGrams
	 * @param getAllSkips
	 * @param splitHyphen
	 * @param maxWordCountForSkipGrams
	 * @return a Stream of Strings.
	 */
	protected static HashSet<String> getSkipGramsFromTaxonAsStream(String pString, int minWordCountForSkipGrams, boolean getAllSkips, boolean splitHyphen, int maxWordCountForSkipGrams) {
		ArrayList<String> words = getWords(pString, splitHyphen);
		if (words.size() < minWordCountForSkipGrams | words.size() > maxWordCountForSkipGrams) {
			return Sets.newHashSet(pString);
		} else {
			IntStream combinationRange;
			if (getAllSkips && words.size() > 3) {
				combinationRange = IntStream.range(2, words.size());
			} else {
				combinationRange = IntStream.of(words.size() - 1);
			}
			
			Stream<Integer[]> combinationsArraysStream = combinationRange
					.boxed()
					.map(i -> new Combinations(words.size(), i).iterator())
					.flatMap(Streams::stream)
					.map(ArrayUtils::toObject);
			
			return combinationsArraysStream
					.parallel()
					.map(combination -> {
						ArrayList<String> strings = new ArrayList<>();
						for (int index : combination) {
							strings.add(words.get(index));
						}
						return String.join(" ", strings);
					}).collect(Collectors.toCollection(HashSet::new));
		}
	}
	
	protected static ArrayList<String> getWords(String pString, boolean splitHyphen) {
		ArrayList<String> words;
		if (splitHyphen) {
			words = Lists.newArrayList(pString.split("[\\s\n\\-]+"));
		} else {
			words = Lists.newArrayList(pString.split("[\\s\n]+"));
		}
		return words;
	}
	
	protected StringTreeNode buildTree(Boolean bUseLowercase, String tokenBoundaryRegex) {
		logger.info("Building tree..");
		StringTreeNode tree = new StringTreeNode(tokenBoundaryRegex, bUseLowercase);
		sortedSkipGramSet.stream()
				.parallel()
				.filter(entry -> !filterSet.contains(entry.toLowerCase()))
				.forEach(tree::insert);
		return tree;
	}
	
	/**
	 * Stream all previously created skip-grams sorted .
	 *
	 * @return A stream of strings by calling: this.skipGramSet.stream().
	 */
	public Stream<String> stream() {
		return this.sortedSkipGramSet.stream();
	}
	
	@Override
	public Map<String, String> getSkipGramTaxonLookup() {
		return skipGramTaxonLookup;
	}
	
	@Override
	public Set<String> getSortedSkipGramSet() {
		return sortedSkipGramSet;
	}
	
	@Override
	public Map<String, HashSet<Object>> getTaxonUriMap() {
		return taxonUriMap;
	}
	
}
