package org.hucompute.textimager.uima.flair;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.MappingProvider;
import org.hucompute.textimager.uima.base.JepAnnotator;
import org.springframework.util.FileSystemUtils;

public abstract class FlairBase extends JepAnnotator {

	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = true)
	protected String modelLocation;

	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION = ComponentParameters.PARAM_NAMED_ENTITY_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION, mandatory = false)
	protected String pMappingProviderLocation;

	protected MappingProvider mappingProvider;
	private String[] resourceFiles = new String[] { "python/__init__.py", "python/model_flair.py",
			"python/embeddings.py" };
	private Path tempFolder;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		if (envName == null || envName.isEmpty()) {
			envName = "textimager_flair_py37";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.7";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "uarray=0.6.0 -c conda-forge";
		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "--upgrade git+https://github.com/flairNLP/flair.git@v0.5.1";
		}
		if (condaVersion == null || condaVersion.isEmpty()) {
			condaVersion = "py37_4.8.3";
		}

		initConda();

		try {
			tempFolder = Files.createTempDirectory(this.getClass().getSimpleName());
			for (String fileName : resourceFiles) {
				Path outPath = Paths.get(tempFolder.toString(), fileName);
				Files.createDirectories(outPath.getParent());
				try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
					FileUtils.copyInputStreamToFile(Objects.requireNonNull(resourceAsStream), outPath.toFile());
				}
			}

			interpreter.exec("import os");
			interpreter.exec("import sys");

			interpreter.exec("sys.path = ['" + tempFolder.toAbsolutePath().toString() + "/python/'] + sys.path");
			interpreter.exec("from embeddings import WordToVecFormatEmbeddings");
			interpreter.exec("from model_flair import SpanModel, TokenModel, MultiModel, CachedMultiModel");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			if (tempFolder != null) {
				FileSystemUtils.deleteRecursively(tempFolder.toFile());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.destroy();
	}

}
