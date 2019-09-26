package org.hucompute.textimager.fasttext.linesannotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

// Fügt DIV hinzu die fastText als "Zeilen" erkennen kann
public class LinesAnnotator extends JCasAnnotator_ImplBase {
	/**
	 * Newline separator
	 */
	public static final String PARAM_NEWLINE_SEP = "newlineSep";
	@ConfigurationParameter(name = PARAM_NEWLINE_SEP, mandatory = true, defaultValue = "\n")
	protected String newlineSep;

	/**
	 * Div type
	 */
	public static final String PARAM_DIV_TYPE = "divType";
	@ConfigurationParameter(name = PARAM_DIV_TYPE, mandatory = true, defaultValue = "newline")
	protected String divType;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// Add new lines as Div
		String docTest = jCas.getDocumentText();
		int pos = docTest.indexOf(newlineSep);
		int begin = 0;
		while (pos >= 0) {
			if (begin != pos) {
				Div newDiv = new Div(jCas, begin, pos);
				newDiv.setDivType(divType);
				newDiv.addToIndexes();
			}
			begin = pos + 1;
			pos = docTest.indexOf(newlineSep, begin);
		}
	}
}
