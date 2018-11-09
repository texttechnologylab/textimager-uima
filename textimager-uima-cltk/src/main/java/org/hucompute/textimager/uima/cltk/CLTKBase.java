package org.hucompute.textimager.uima.cltk;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;

public abstract class CLTKBase extends DockerRestAnnotator {
    /**
     * The docker image for the CLTK server
     */
    /*public static final String PARAM_DOCKER_IMAGE = "dockerImage";
    @ConfigurationParameter(name = PARAM_DOCKER_IMAGE, mandatory = true, defaultValue = "texttechnologylab/textimager-cltk:1")
    protected String dockerImage;*/
}
