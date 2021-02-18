package org.hucompute.textimager.uima.biofid.gazetteer.models;

import org.hucompute.textimager.uima.biofid.gazetteer.tree.ITreeNode;

public interface ITreeGazetteerModel extends IGazetteerModel {
	
	ITreeNode getTree();
}
