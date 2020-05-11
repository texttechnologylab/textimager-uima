package org.biofid.gazetteer.Models;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.Pair;
import org.apache.uima.util.UriUtils;
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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SkipGramGazetteerModel {
	
	public static Pattern nonTokenCharacterClass = Pattern.compile("[^\\p{Alpha}\\- ]+", Pattern.UNICODE_CHARACTER_CLASS);
	private static final Path tempPath = Paths.get("/tmp/biofid-gazetteer/");
	private static final Path cachePath = Paths.get(System.getenv("HOME"), ".cache/biofid-gazetteer/").toAbsolutePath();
	public LinkedHashSet<String> skipGramSet;
	public LinkedHashMap<String, HashSet<URI>> taxonUriMap;
	public LinkedHashMap<String, String> skipGramTaxonLookup;
	public HashMap<String, HashSet<String>> taxonSkipGramMap;
	public static boolean getAllSkips;
	public static boolean splitHyphen;
	
	/**
	 * Create 1-skip-n-grams from each taxon in a file from a given list of files.
	 * Constructor overload for default language="de" and bAllSkips=false.
	 *
	 * @param aSourceLocations An array of UTF-8 file locations containing a list of one taxon and any number of URIs (comma
	 *                         or space separated) per line.
	 * @param bUseLowercase    If true, use lower cased skip-grams.
	 * @param dMinLength       The minimum skip-gram length. All skip-grams (and taxa) with a length lower than this will be
	 *                         omitted.
	 * @throws IOException
	 */
	public SkipGramGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, double dMinLength) throws IOException {
		this(aSourceLocations, bUseLowercase, "de", dMinLength, false, true);
	}
	
	/**
	 * Create 1-skip-n-grams from each taxon in a file from a given list of files.
	 *
	 * @param aSourceLocations An array of UTF-8 file locations containing a list of one taxon and any number of URIs (comma
	 *                         or space separated) per line.
	 * @param bUseLowercase    If true, use lower cased skip-grams.
	 * @param sLanguage        The language to be used as locale for lower casing.
	 * @param dMinLength       The minimum skip-gram length. All skip-grams (and taxa) with a length lower than this will be
	 *                         omitted.
	 * @param bAllSkips        If true, get all m-skip-n-grams of length n > 2.
	 * @param bSplitHyphen     If true, taxon tokens will be split at hyphens.
	 * @throws IOException
	 */
	public SkipGramGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, String sLanguage, double dMinLength, boolean bAllSkips, boolean bSplitHyphen) throws IOException {
		getAllSkips = bAllSkips;
		splitHyphen = bSplitHyphen;
		
		ArrayList<String> sourceLocations = new ArrayList<>();
		for (String sourceLocation : aSourceLocations) {
			// If sourceLocation is a valid URL, download the given file
			sourceLocation = downloadTaxaFiles(sourceLocation);
			
			// If zipped extract taxa files to temp folder
			if (sourceLocation.endsWith(".zip")) {
				sourceLocations.addAll(extractTaxaFiles(sourceLocation));
			} else {
				sourceLocations.add(sourceLocation);
			}
		}
		
		System.out.printf("%s: Loading taxa from %d files..\n", this.getClass().getSimpleName(), sourceLocations.size());
		long startTime = System.currentTimeMillis();
		AtomicInteger duplicateKeys = new AtomicInteger(0);
		
		// Map: Taxon -> {URI}
		taxonUriMap = new LinkedHashMap<>();
		for (String sourceLocation : sourceLocations) {
			SkipGramGazetteerModel.loadTaxaMap(sourceLocation, bUseLowercase, sLanguage).forEach((taxon, uri) ->
					taxonUriMap.merge(taxon, uri, (uUri, vUri) -> {
						duplicateKeys.incrementAndGet();
						return new HashSet<>(SetUtils.union(uUri, vUri));
					}));
		}
		System.out.printf("%s: Loaded %d taxa from %d files.\n", this.getClass().getSimpleName(), taxonUriMap.size(), sourceLocations.size());
		if (duplicateKeys.get() > 0)
			System.err.printf("%s: Merged %d duplicate taxa!\n", this.getClass().getSimpleName(), duplicateKeys.get());
		duplicateKeys.set(0);
		
		// Map: Taxon -> {Skip-Grams}
		taxonSkipGramMap = taxonUriMap.keySet().stream()
				.collect(Collectors.toMap(
						Function.identity(),
						SkipGramGazetteerModel::getSkipGrams,
						(u, v) -> v,
						HashMap::new));
		
		// Map: Skip-Gram -> Taxon
		skipGramTaxonLookup = taxonSkipGramMap.entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream().map(val -> new Pair<>(entry.getKey(), val)))
				.collect(Collectors.toMap(
						Pair::getSecond,    // the skip-gram
						Pair::getFirst,     // the corresponding taxon
						(u, v) -> {
							// Drop duplicate skip-grams to ensure bijective skip-gram <-> taxon mapping.
							duplicateKeys.incrementAndGet();
							return null;
						},
						LinkedHashMap::new));
		System.err.printf("%s: Ignoring %d duplicate skip-grams!\n", this.getClass().getSimpleName(), duplicateKeys.get());
		
		// Ensure actual taxa are contained in skipGramTaxonLookup
		taxonUriMap.keySet().forEach(tax -> skipGramTaxonLookup.put(tax, tax));
		
		// Set: {Skip-Gram}
		skipGramSet = skipGramTaxonLookup.keySet().stream()
				.filter(s -> !Strings.isNullOrEmpty(s))
				.filter(s -> s.length() >= dMinLength)
				.sorted(Comparator.comparingInt(String::length).reversed())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		
		System.out.printf("%s: Finished loading %d skip-grams from %d taxa in %dms.\n",
				this.getClass().getSimpleName(), skipGramSet.size(), taxonUriMap.size(), System.currentTimeMillis() - startTime);
	}
	
	/**
	 * Find this Skip-Grams taxon return its respective URI.
	 *
	 * @param skipGram the target Skip-Gram
	 * @return taxonUriMap.get(skipGramTaxonLookup.get ( skipGram))
	 */
	public HashSet<URI> getUriFromSkipGram(String skipGram) {
		return taxonUriMap.get(skipGramTaxonLookup.get(skipGram));
	}
	
	/**
	 * Get all skip-grams of the given taxon.
	 *
	 * @param taxon The taxon to get the skip-grams from
	 * @return A list of skip-grams.
	 */
	public HashSet<String> getSkipGramsFromTaxon(String taxon) {
		return taxonSkipGramMap.get(taxon);
	}
	
	/**
	 * Load taxa from UTF-8 file, one taxon per line.
	 *
	 * @return ArrayList of taxa.
	 * @throws IOException if file is not found or an error occurs.
	 */
	private static LinkedHashMap<String, HashSet<URI>> loadTaxaMap(String sourceLocation, Boolean pUseLowercase, String language) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(sourceLocation)), StandardCharsets.UTF_8))) {
			return bufferedReader.lines()
					.filter(s -> !Strings.isNullOrEmpty(s))
					.map(s -> pUseLowercase ? s.toLowerCase(Locale.forLanguageTag(language)) : s)
					.collect(Collectors.toMap(
							s -> nonTokenCharacterClass.matcher(s.split("\t", 2)[0]).replaceAll("").trim(),
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
	public String downloadTaxaFiles(String sourceLocation) throws IOException {
		try {
			URL url = new URL(sourceLocation);
			String taxaLocation = getTaxaLocation().toString();
			System.out.println(String.format("%s: Downloading '%s'..", this.getClass().getSimpleName(), sourceLocation));
			sourceLocation = FileUtils.downloadFile(taxaLocation, url.toString(), false).toString();
			System.out.println(String.format("%s: Finished download of '%s'.", this.getClass().getSimpleName(), sourceLocation));
		} catch (MalformedURLException ignored) {
		}
		return sourceLocation;
	}
	
	private static ArrayList<String> extractTaxaFiles(String sourceLocation) throws IOException {
		System.out.println(String.format("%s: Extracting taxa files from '%s'..", SkipGramGazetteerModel.class.getSimpleName(), sourceLocation));
		
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
					System.out.printf("File '%s' exists, skipping extraction.\n", entryDestination);
				}
				in.close();
			}
		}
		System.out.println(String.format("%s: Extracted %d taxa files to '%s'.", SkipGramGazetteerModel.class.getSimpleName(), extractedFiles.size(), sourceLocation));
		return extractedFiles;
	}
	
	/**
	 * Check the possible paths for automatic download and extraction for read/write access
	 * and create folders if necessary.
	 *
	 * @return A valid writable Path.
	 * @throws IOException If neither {@link SkipGramGazetteerModel#cachePath cachePath} nor
	 *                     {@link SkipGramGazetteerModel#tempPath tempPath} are writable.
	 */
	private static Path getTaxaLocation() throws IOException {
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
	 * Calls {@link this.getSkipGrams getSkipGrams(String)} and collects the result in a list.
	 *
	 * @param pString the target String.
	 * @return a List of Strings.
	 */
	private static HashSet<String> getSkipGrams(String pString) {
		HashSet<String> basicSkipGrams = getSkipGramsStream(pString).collect(Collectors.toCollection(HashSet::new));
		ArrayList<String> words = getWords(pString);
		if (words.size() > 1) {
			words.set(0, pString.charAt(0) + ".");
			String abbreviatedString = String.join(" ", words);
			basicSkipGrams.add(abbreviatedString);
			if (words.size() > 2) {
				basicSkipGrams.addAll(getSkipGramsStream(abbreviatedString).collect(Collectors.toCollection(HashSet::new)));
			}
		}
		return basicSkipGrams;
	}
	
	/**
	 * Get a List of 1-skip-n-grams for the given string.
	 * The string itself is always the first element of the list.
	 * The string is split by whitespaces and all n over n-1 combinations are computed and added to the list.
	 * If there is only a single word in the given string, a singleton list with that word is returned.
	 *
	 * @param pString the target String.
	 * @return a Stream of Strings.
	 */
	private static Stream<String> getSkipGramsStream(String pString) {
		ArrayList<String> words = getWords(pString);
		if (words.size() < 3) {
			return Stream.of(pString);
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
					});
		}
	}
	
	private static ArrayList<String> getWords(String pString) {
		ArrayList<String> words;
		if (splitHyphen) {
			words = Lists.newArrayList(pString.split("[\\s\n\\-]+"));
		} else {
			words = Lists.newArrayList(pString.split("[\\s\n]+"));
		}
		return words;
	}
	
	/**
	 * Stream all previously created skip-grams sorted .
	 *
	 * @return A stream of strings by calling: this.skipGramSet.stream().
	 */
	public Stream<String> stream() {
		return this.skipGramSet.stream();
	}
}
