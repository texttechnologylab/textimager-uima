package org.hucompute.textimager.uima.gazetteer.models;

import org.hucompute.textimager.uima.gazetteer.tree.ITreeNode;
import org.hucompute.textimager.uima.gazetteer.tree.StringTreeNode;

import java.io.IOException;
import java.util.HashSet;


public class TreeGazetteerModel extends StringGazetteerModel implements ITreeGazetteerModel {
	
	private final StringTreeNode tree;
	
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
	public TreeGazetteerModel(
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
		super(aSourceLocations, bUseLowercase, sLanguage, dMinLength, bAllSkips, bSplitHyphen, bAddAbbreviatedTaxa, iMinWordCountForSkipGrams, tokenBoundaryRegex, pFilterSet, gazetteerName);
		long startTime = System.currentTimeMillis();
		tree = buildTree(bUseLowercase, tokenBoundaryRegex);
		
		logger.info(String.format("Finished building tree with %d nodes from %d skip-grams in %dms.",
				tree.size(), sortedSkipGramSet.size(), System.currentTimeMillis() - startTime
		));
	}
	
	@Override
	public ITreeNode getTree() {
		return this.tree;
	}
}
