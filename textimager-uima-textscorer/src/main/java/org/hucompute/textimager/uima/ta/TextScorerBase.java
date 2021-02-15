package org.hucompute.textimager.uima.ta;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;

public abstract class TextScorerBase extends JepAnnotator {
	
    private String[] resourceFiles = new String[] {"scorer.py"};
    private Path tempFolder;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		System.out.println("initializing spacy base class...");

		// set defaults
		// TODO schönerer Weg?
		if (condaBashScript == null || condaBashScript.isEmpty()) {
			condaBashScript = "ta_setup.sh";
		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "spacy>=3.0.0 polyglot>=16.7.4 transformers>=3.0.2";
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

	// Adds the "words" and "spaces" arrays for spaCy to the JSON object
	protected void jsonAddWordsAndSpaces(JCas aJCas, HashMap<String, Object> json) {
		ArrayList<String> jsonWords = new ArrayList<>();
		ArrayList<Boolean> jsonSpaces = new ArrayList<>();

		Token lastToken = null;
		for (Token token : JCasUtil.select(aJCas, Token.class)) {
			// Recreate spaCy Doc Text: Add "space" token if more than 1 space between words
			if (lastToken != null) {
				if (lastToken.getEnd() == token.getBegin()) {
					// No space
					jsonWords.add(token.getCoveredText());
					jsonSpaces.add(false);
				} else {
					int num = token.getBegin() - lastToken.getEnd();
					if (num > 1) {
						// Add space to last word
						jsonSpaces.add(true);
						// Add "space" token with num-1 spaces
						jsonWords.add(new String(new char[num-1]).replace("\0", " "));
						// ... followed by no space and the next word
						jsonSpaces.add(false);
						jsonWords.add(token.getCoveredText());
					} else {
						jsonWords.add(token.getCoveredText());
						jsonSpaces.add(true);
					}
				}
			} else {
				jsonWords.add(token.getCoveredText());
			}

			lastToken = token;
		}

		// Handle last token
		if (lastToken != null) {
			if (aJCas.getDocumentText().length() == lastToken.getEnd())	{
				jsonSpaces.add(false);
			} else {
				int num = aJCas.getDocumentText().length() - lastToken.getEnd();
				if (num > 1) {
					jsonSpaces.add(true);
					jsonWords.add(new String(new char[num-1]).replace("\0", " "));
					jsonSpaces.add(false);
				} else {
					jsonSpaces.add(true);
				}
			}
		}
		json.put("words", jsonWords);
		json.put("spaces", jsonSpaces);
	}

	protected HashMap<String, Object>  buildJSON(JCas aJCas) {
		HashMap<String, Object> json = new HashMap<>();
		json.put("lang", aJCas.getDocumentLanguage());
		jsonAddWordsAndSpaces(aJCas, json);
		return json;
	}
}
