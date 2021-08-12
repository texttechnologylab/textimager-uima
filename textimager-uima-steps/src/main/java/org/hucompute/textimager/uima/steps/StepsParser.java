package org.hucompute.textimager.uima.steps;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.MetaDataStringField;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class StepsParser extends RestAnnotator {
	private static class ConllLine {
		int tokenIndex;
		int begin;
		int end;
		int governorIndex;
		String dep;

		public ConllLine(int tokenIndex, int begin, int end, int governorIndex, String dep) {
			this.tokenIndex = tokenIndex;
			this.begin = begin;
			this.end = end;
			this.governorIndex = governorIndex;
			this.dep = dep;
		}
	}

	@Override
	protected String getRestRoute() {
		return "/parse";
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	/**
	 * The unique key to identify the annotation of the dynamic batch size inside of the jCas Object
	 */
	static public String DYNAMIC_CONFIGURATION_BATCH_SIZE_KEY = "steps_parser.dynamic_configuration.batch_size";

	/**
	 * Sets the batch size as annotation inside of the jCAS
	 * @param aJCAS The jCAS Object to annotate
	 * @param batch_size The batch size to write into the jCas Object
	 */
	public static void set_batch_size(JCas aJCAS, int batch_size) {
		MetaDataStringField field = new MetaDataStringField(aJCAS);
		field.setValue(Integer.toString(batch_size));
		//Set the key to something constant
		field.setKey(StepsParser.DYNAMIC_CONFIGURATION_BATCH_SIZE_KEY);
		field.addToIndexes();
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		// Format:
		// CONLL Line:
		// List ['1', 'This', '_', '_', '_', '_', '0', '_', '_', 'start_char=0|end_char=4']
		// CONLL Sentence:
		// List of CONLL Lines
		// CONLL Corpus:
		// List of CONLL Sentences

		//Default batch size is 16
		int batch_size = 16;

		// Extract the batch size from the jCas if there is any
		for (MetaDataStringField i : JCasUtil.select(aJCas, MetaDataStringField.class)) {
			if (i.getKey().trim().equals(StepsParser.DYNAMIC_CONFIGURATION_BATCH_SIZE_KEY.trim())) {
				batch_size = parseInt(i.getValue().trim());
				if(batch_size > 128) {
					batch_size = 128;
				}
			}
		}

		JSONArray jsonSentences = new JSONArray();
		for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
			JSONArray jsonSentence = new JSONArray();

			int tokenIndex = 0;
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				JSONArray jsonToken = new JSONArray();

				// token index, 1-based
				jsonToken.put(String.valueOf(tokenIndex+1));

				// token text
				jsonToken.put(token.getCoveredText());

				// empty
				jsonToken.put("_");
				jsonToken.put("_");
				jsonToken.put("_");
				jsonToken.put("_");

				// token index, 0-based
				jsonToken.put(String.valueOf(tokenIndex));

				// emtpy
				jsonToken.put("_");
				jsonToken.put("_");

				// start end pos
				String startEnd = String.format("start_char=%d|end_char=%d", token.getBegin(), token.getEnd());
				jsonToken.put(startEnd);

				jsonSentence.put(jsonToken);
				tokenIndex++;
			}

			jsonSentences.put(jsonSentence);
		}

		JSONObject payload = new JSONObject();
		payload.put("sentences", jsonSentences);
		//Put the batch size in as well
		payload.put("batch_size",batch_size);
		return payload;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		if (!jsonResult.has("sentences")) {
			System.out.println("no sentences in response?!");
			System.out.println(jsonResult.toString());
			return;
		}

		JSONArray jsonSentences = jsonResult.getJSONArray("sentences");
		for (Object jsonSentence : jsonSentences) {
			String sentenceStr = (String) jsonSentence;

			// build mapping from token index (1-based) to token data
			Map<Integer, ConllLine> tokenMap = new HashMap<>();

			for (String line : sentenceStr.split("\n", -1)) {
				if (line.trim().isEmpty()) {
					System.out.println("skipping empty line...");
					continue;
				}

				String[] fields = line.trim().split("\t", 10);
				if (fields.length == 10) {
					// token id
					int tokenIndex = Integer.parseInt(fields[0].trim());

					// dependency string
					String dep = fields[7].trim();

					// token id
					int governorIndex = Integer.parseInt(fields[6].trim());

					// extract start/end char
					String[] startEndInfo = fields[9].trim().split("\\|", 2);
					int begin = Integer.parseInt(startEndInfo[0].trim().replace("start_char=", ""));
					int end = Integer.parseInt(startEndInfo[1].trim().replace("end_char=", ""));

					tokenMap.put(tokenIndex, new ConllLine(tokenIndex, begin, end, governorIndex, dep));
				}
				else {
					System.out.println("line malformed");
					System.out.println(line);
				}
			}

			System.out.println("found dep for token: " + tokenMap.size());

			// add annotations
			for (Map.Entry<Integer, ConllLine> entry : tokenMap.entrySet()) {
				ConllLine line = entry.getValue();

				// get dependent
				Token dependent = JCasUtil.selectSingleAt(aJCas, Token.class, line.begin, line.end);

				// set depending on dep
				Token governor = null;

				// add to cas
				Dependency depAnno;
				if (line.dep.equals("root")) {
					depAnno = new ROOT(aJCas, line.begin, line.end);
					depAnno.setDependencyType("--");

					// governor = dependent
					governor = dependent;
				} else {
					depAnno = new Dependency(aJCas, line.begin, line.end);
					depAnno.setDependencyType(line.dep);

					// get governor from specified line
					ConllLine governorLine = tokenMap.get(line.governorIndex);
					governor = JCasUtil.selectSingleAt(aJCas, Token.class, governorLine.begin, governorLine.end);
				}
				depAnno.setDependent(dependent);
				depAnno.setGovernor(governor);
				depAnno.setFlavor(DependencyFlavor.BASIC);
				depAnno.addToIndexes();
			}
		}
	}
}
