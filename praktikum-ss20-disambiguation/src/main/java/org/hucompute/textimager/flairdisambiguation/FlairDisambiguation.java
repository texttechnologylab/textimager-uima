package org.hucompute.textimager.flairdisambiguation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;
import org.springframework.util.FileSystemUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

/**
 * <p>This class implements the interface with the flair disambiguation python script.</\p>
 *
 * @author Tim Rosenkranz
 * @author Dirk Neuhäuser
 */

@TypeCapability(
        inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"},
        outputs = {"de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense"}
)
public class FlairDisambiguation extends JepAnnotator {

    // Python scripts
    private final String[] resourceFiles = new String[]{"flair_disambiguation.py"};
    private Path tempFolder;
    // Path to classifier model (should be located in src/main/resources)
    private Path modelPath = Paths.get("praktikum.ss20.disambiguation/src/main/resources/final-model.pt");

    /**
     * Initialized the python conda environment, temp Folder and the interface with the python script.
     *
     * @param aContext Object with access to all external resources
     * @throws ResourceInitializationException if the initialisation fails
     */
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        System.out.println("-------------------- Init start --------------------");

        /*
         * Initialise Conda environment
         *
         * Python environment to execute the interpreter statements
         */

        // Environment name
        if (envName == null || envName.isEmpty()) {
            envName = "textimager_flair_py37";
        }
        // Version of Python
        if (envPythonVersion == null || envPythonVersion.isEmpty()) {
            envPythonVersion = "3.7";
        }
        // Deps Conda
        if (envDepsConda == null || envDepsConda.isEmpty()) {
            envDepsConda = "uarray=0.6.0 -c conda-forge";
        }
        // Dependencies
        if (envDepsPip == null || envDepsPip.isEmpty()) {
            envDepsPip = "--upgrade git+https://github.com/flairNLP/flair.git@v0.5.1";
        }
        // Version of conda
        if (condaVersion == null || condaVersion.isEmpty()) {
            condaVersion = "py37_4.8.3";
        }

        initConda();

        try {
            // Create temporary folder where python scripts will be copied into
            tempFolder = Files.createTempDirectory(this.getClass().getSimpleName());
            for (String fileName : resourceFiles) {
                Path outPath = Paths.get(tempFolder.toString(), fileName);
                Files.createDirectories(outPath.getParent());
                // URL url  = this.getClass().getClassLoader().getResource(fileName);
                try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(resourceAsStream), outPath.toFile());
                } catch (Exception e) {
                    throw new Exception("File 'flair_disambiguation.py' not found.");
                }
            }

            System.out.println("-------------------- Interpreter start --------------------");

            String tempFolderPath = tempFolder.toAbsolutePath().toString();
            // Initialise python script
            // Import needed python libraries
            interpreter.exec("import sys, os");
            // Add Path to tempFolder to sys path
            interpreter.exec("sys.path = ['" + tempFolderPath + "/'] + sys.path");
            interpreter.exec("os.chdir('" + tempFolderPath + "')");
            // Import flair model
            interpreter.exec("from flair_disambiguation import BaseModel");
            // Instantiate an object
            interpreter.exec("model = BaseModel('" + modelPath.toAbsolutePath().toString() + "')");

        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }

        System.out.println("-------------------- Init done --------------------");
    }


    /**
     * Processes the text in the JCas
     *
     * @param jCas JCas Object containing the data to be processed
     * @throws AnalysisEngineProcessException if process fails
     */
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {

        // Collect and iterate through the sentences from JCas
        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        for (Sentence sentence : sentences) {

            String coveredText = sentence.getCoveredText();

            // Extract every Token from the sentence
            for (Token t : JCasUtil.selectCovered(Token.class, sentence)) {
                String tokenText = t.getText();
                // Check token for preposition
                // Every preposition in a sentence will be predicted separately
                if (t.getPos().getPosValue().equals("IN")) {
                    int tokenBegin = t.getBegin() - sentence.getBegin();
                    int tokenEnd = t.getEnd() - sentence.getBegin();

                    // Mark the preposition with the html-tags in the sentence
                    String adjustedSentence = coveredText.substring(0, tokenBegin) + "<head>" + tokenText + "</head>" + coveredText.substring(tokenEnd, sentence.getEnd() - sentence.getBegin());
                    System.out.println("Sentence: " + adjustedSentence);

                    try {
                        // Predict the sentence with marked preposition
                        String labelId = interpreter.getValue("model.predict(sentence='" + adjustedSentence + "')", String.class);
                        // Convert the output (sense ID) to type WordSense and include in JCas
                        WordSense wordSense = new WordSense(jCas, tokenBegin, tokenEnd);
                        wordSense.setValue(labelId);
                        wordSense.addToIndexes();

                        System.out.println("LabelID: " + labelId);
                    } catch (Exception e) {
                        throw new AnalysisEngineProcessException(e);
                    }

                }

            }
        }

        System.out.println("-------------------- Process done --------------------");
    }

    /**
     * Destroys the temp Folder to clear memory
     */
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
