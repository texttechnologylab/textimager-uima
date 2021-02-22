package org.hucompute.textimager.uima.gazetteer.tree;

import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nonnull;
import java.util.List;

public interface ITreeNode {
	boolean hasValue();
	
	boolean isLeaf();
	
	void insert(String value);
	
	int size();
	
	int leafs();
	
	int nodesWithValue();
	
	ImmutablePair<String, Integer> traverse(@Nonnull List<String> subString);
	
	@Override
	String toString();
	
	String getValue();
	
	int depth();
}
