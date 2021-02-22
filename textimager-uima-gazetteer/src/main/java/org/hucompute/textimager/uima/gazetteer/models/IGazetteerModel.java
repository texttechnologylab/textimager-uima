package org.hucompute.textimager.uima.gazetteer.models;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface IGazetteerModel {
	Map<String, String> getSkipGramTaxonLookup();
	
	Set<String> getSortedSkipGramSet();
	
	Map<String, HashSet<Object>> getTaxonUriMap();
}
