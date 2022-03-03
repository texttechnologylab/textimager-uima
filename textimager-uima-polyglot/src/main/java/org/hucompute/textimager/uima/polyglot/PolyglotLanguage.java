package org.hucompute.textimager.uima.polyglot;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

/**
* PolyglotLanguage
*
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide language recognition for different languages.
* Language code is used for JCas document language.
*/
public class PolyglotLanguage  extends SegmenterBase {

	/**
     * Load the PythonPATH
     */
    public static final String PARAM_PYTHON_PATH = "PythonPathPolyglot";
    @ConfigurationParameter(name = PARAM_PYTHON_PATH, mandatory = false)
    protected String PythonPATH;

    public static final String PARAM_POLYGLOT_PATH = "PolyglotPath";
    @ConfigurationParameter(name = PARAM_POLYGLOT_PATH, mandatory = false)
    protected String POLYGLOT_LOCATION;

	/**
	 * Analyze the text and recognize language. After successfully recognition, add language code to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if(POLYGLOT_LOCATION == null) {
			POLYGLOT_LOCATION = "src/main/resources/org/hucompute/textimager/uima/polyglot/python/";
		}

		String inputText = aJCas.getDocumentText();

		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "language", inputText);
        pb.redirectError(Redirect.INHERIT);

        boolean success = false;
        Process proc = null;

        try {
	    	// Start Process
	        proc = pb.start();

	        // IN, ERROR Streams
	        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

	        StringBuilder builder = new StringBuilder();
					String line = null;
					while ( (line = in.readLine()) != null) {
					   builder.append(line);
					   builder.append(System.getProperty("line.separator"));
					}
			String result = builder.toString();
			String[] resultInParts = result.split("code: ");
			resultInParts = resultInParts[1].split("       ");

			aJCas.setDocumentLanguage(resultInParts[0]);

	        // Get Errors
             String errorString = "";
			 line = "";
			 try {
				while ((line = error.readLine()) != null) {
					errorString += line+"\n";
				}
			 } catch (IOException e) {
				e.printStackTrace();
			 }

			 // Log Error
			 if(errorString != "")
			 getLogger().error(errorString);

             success = true;
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

        finally {
            if (!success) {

            }

            if (proc != null) {
                proc.destroy();
            }
        }
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {

	}

}
