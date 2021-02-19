package org.hucompute.textimager.uima.gazetteer.models;

import org.hucompute.textimager.uima.gazetteer.tree.ITreeNode;

public interface ITreeGazetteerModel extends IGazetteerModel {
	
	ITreeNode getTree();
}
