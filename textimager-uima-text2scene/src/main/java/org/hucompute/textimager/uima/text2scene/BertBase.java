package org.hucompute.textimager.uima.text2scene;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.MappingProvider;
import org.hucompute.textimager.uima.base.JepAnnotator;
import org.springframework.util.FileSystemUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class BertBase extends JepAnnotator {

    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    protected MappingProvider mappingProvider;
    private String[] resourceFiles = new String[] {"bert.py"};


    private Path tempFolder;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        if (envName == null || envName.isEmpty()) {
            envName = "textimager_bert_py38";
        }
        if (envPythonVersion == null || envPythonVersion.isEmpty()) {
            envPythonVersion = "3.8";
        }
        if (envDepsConda == null || envDepsConda.isEmpty()) {
            envDepsConda = "uarray=0.6.0 -c conda-forge";
        }
        if (envDepsPip == null || envDepsPip.isEmpty()) {
            envDepsPip = "nltk==3.5 transformers==3.4.0 torch==1.7.0+cpu torchaudio==0.7.0 -f https://download.pytorch.org/whl/torch_stable.html";
        }
        if (condaVersion == null || condaVersion.isEmpty()) {
            condaVersion = "py38_4.8.3";
        }
        condaVersion = "py38_4.8.3";

        initConda();

        try {
            tempFolder = Files.createTempDirectory(this.getClass().getSimpleName());
            for (String fileName : resourceFiles) {
                Path outPath = Paths.get(tempFolder.toString(), fileName);
                Files.createDirectories(outPath.getParent());
                try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(resourceAsStream), outPath.toFile());
                } catch (Exception e) {
                    throw new Exception("File 'bert.py' not found.");
                }
            }

            System.out.println("-------------------- Interpreter start --------------------");

            String tempFolderPath = tempFolder.toAbsolutePath().toString();
            System.out.println("os.chdir('" + tempFolderPath + "')");
            interpreter.exec("import os");
            interpreter.exec("import sys");

            interpreter.exec("import nltk");
            interpreter.exec("nltk.download('wordnet')");
            interpreter.exec("nltk.download('punkt')");
            interpreter.exec("nltk.download('averaged_perceptron_tagger')");
            interpreter.exec("nltk.download('universal_tagset')");

            interpreter.exec("os.chdir('" + tempFolderPath + "')");
            interpreter.exec("sys.path = ['" + tempFolderPath + "/'] + sys.path");
            interpreter.exec("from bert import Bert");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceInitializationException(e);
        }
        System.out.println("-------------------- Init done --------------------");
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
