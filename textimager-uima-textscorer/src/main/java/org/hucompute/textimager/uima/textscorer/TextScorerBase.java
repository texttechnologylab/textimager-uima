package org.hucompute.textimager.uima.textscorer;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class TextScorerBase extends JepAnnotator {
	
    private String[] resourceFiles = new String[] {"scorer.py"};
    private Path tempFolder;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		System.out.println("initializing textscorer base class...");

		// set defaults
		// TODO schönerer Weg?
		if (condaBashScript == null || condaBashScript.isEmpty()) {
			condaBashScript = "ta_setup.sh";
		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "cffi>=1.14.5 dcor>=0.5.2 hdbscan>=0.8.27 networkx>=2.5 nltk>=3.5 nolds>=0.5.2 numpy>=1.20.1" +
					" spacy>=3.0.0 polyglot>=16.7.4 transformers>=4.3.2 statsmodels>=0.11.0 pyts>=0.11.0 sklearn>=0.0" +
					" uarray>=0.6.0";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.8";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_ta_py38";
		}
		if (condaVersion == null || condaVersion.isEmpty()) {
			condaVersion = "py38_4.9.2";
		}
		
		System.out.println("initializing ta base class: conda");
		
		initConda();
		
		
		 try {
			tempFolder = Files.createTempDirectory(this.getClass().getSimpleName());
			
			for (String fileName : resourceFiles) {
			    Path outPath = Paths.get(tempFolder.toString(), fileName);
			    Files.createDirectories(outPath.getParent());
			    try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
			        FileUtils.copyInputStreamToFile(Objects.requireNonNull(resourceAsStream), outPath.toFile());
			    } catch (Exception e) {
			    	String exceptionText = fileName + " not found.";
			        throw new Exception(exceptionText);
			    }
			}
			
			System.out.println("-------------------- Interpreter start --------------------");
			
			String tempFolderPath = tempFolder.toAbsolutePath().toString();
			System.out.println("os.chdir('" + tempFolderPath + "')");
			interpreter.exec("import os");
			interpreter.exec("import sys");
			
			interpreter.exec("pwd = os.getcwd()");
			String pwDir = interpreter.getValue("pwd", String.class);
		
			interpreter.exec("os.chdir('" + tempFolderPath + "')");
			interpreter.exec("sys.path = ['" + tempFolderPath + "/'] + sys.path");
			
			interpreter.exec("import scorer");
			} catch (Exception e) {
			    e.printStackTrace();
			    throw new ResourceInitializationException(e);
			}
			System.out.println("-------------------- Init done --------------------");
		
	}
}
