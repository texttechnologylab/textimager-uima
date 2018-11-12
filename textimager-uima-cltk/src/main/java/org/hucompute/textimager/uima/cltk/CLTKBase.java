package org.hucompute.textimager.uima.cltk;

import org.hucompute.textimager.uima.base.DockerRestAnnotator;

public abstract class CLTKBase extends DockerRestAnnotator {    
    @Override
    protected String getDefaultDockerImage() {
    	return "texttechnologylab/textimager-cltk:1.0.1";
    }
}
