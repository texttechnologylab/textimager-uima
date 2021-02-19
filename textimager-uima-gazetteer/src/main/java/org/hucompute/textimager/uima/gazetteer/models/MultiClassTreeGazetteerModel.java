package org.hucompute.textimager.uima.gazetteer.models;

import org.apache.commons.collections4.SetUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MultiClassTreeGazetteerModel extends TreeGazetteerModel {
	private HashMap<String, Integer> fileLocationSourceMapping;
	private HashMap<String, Integer> taxonSourceMapping;
	
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
	public MultiClassTreeGazetteerModel(String[] aSourceLocations, Boolean bUseLowercase, String sLanguage, double dMinLength, boolean bAllSkips, boolean bSplitHyphen, boolean bAddAbbreviatedTaxa, int iMinWordCountForSkipGrams, String tokenBoundaryRegex, HashSet<String> pFilterSet, String gazetteerName) throws IOException {
		super(aSourceLocations, bUseLowercase, sLanguage, dMinLength, bAllSkips, bSplitHyphen, bAddAbbreviatedTaxa, iMinWordCountForSkipGrams, tokenBoundaryRegex, pFilterSet, gazetteerName);
	}
	
	@Override
	protected ArrayList<String> getTaxaFiles(String[] aSourceLocations) throws IOException {
		fileLocationSourceMapping = new HashMap<>(10, 1);
		taxonSourceMapping = new HashMap<>();
		ArrayList<String> fileLocations = new ArrayList<>();
		for (int i = 0; i < aSourceLocations.length; i++) {
			String sourcePath = aSourceLocations[i];
			// If sourcePath is a valid URL, download the given file
			sourcePath = downloadTaxaFiles(sourcePath);
			
			// If zipped extract taxa files to temp folder
			if (sourcePath.endsWith(".zip")) {
				fileLocations.addAll(extractTaxaFiles(sourcePath));
			} else {
				File sourceLocationFile = new File(sourcePath);
				if (sourceLocationFile.isDirectory()) {
					String[] list = sourceLocationFile.list();
					if (Objects.isNull(list)) {
						continue;
					}
					for (String fileName : list) {
						String sourceLocationPath = Paths.get(sourcePath, fileName).toAbsolutePath().toString();
						fileLocations.add(sourceLocationPath);
						fileLocationSourceMapping.put(sourceLocationPath, i);
					}
				} else {
					fileLocations.add(sourcePath);
					fileLocationSourceMapping.put(sourcePath, i);
				}
			}
			
		}
		return fileLocations;
	}
	
	@Override
	protected LinkedHashMap<String, HashSet<Object>> buildTaxaUriMap() throws IOException {
		final AtomicInteger duplicateKeys = new AtomicInteger(0);
		final LinkedHashMap<String, HashSet<Object>> lTaxonUriMap = new LinkedHashMap<>();
		
		logger.info(String.format("Loading entries from %d files", sourceLocations.size()));
		for (int i = 0; i < sourceLocations.size(); i++) {
			String sourceLocation = sourceLocations.get(i);
			logger.info(String.format("[%d/%d] Loading file %s", i + 1, sourceLocations.size(), sourceLocation));
			loadTaxaMap(sourceLocation, useLowercase, language).forEach((taxon, uri) ->
					{
						lTaxonUriMap.merge(taxon, uri, (uUri, vUri) -> {
							duplicateKeys.incrementAndGet();
							return new HashSet<>(SetUtils.union(uUri, vUri));
						});
						taxonSourceMapping.put(taxon, fileLocationSourceMapping.get(sourceLocation));
					}
			);
		}
		logger.info(String.format("Loaded %d entries from %d files.", lTaxonUriMap.size(), sourceLocations.size()));
		
		if (duplicateKeys.get() > 0)
			logger.warn(String.format("Merged %d duplicate entries!", duplicateKeys.get()));
		
		return lTaxonUriMap;
	}
	
	public Integer getClassIdFromTaxon(String taxon) {
		return this.taxonSourceMapping.get(taxon);
	}
}
