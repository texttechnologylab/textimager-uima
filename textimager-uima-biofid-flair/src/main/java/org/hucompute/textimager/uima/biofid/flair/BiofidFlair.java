package org.hucompute.textimager.uima.biofid.flair;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.NamedEntity;
import org.texttechnologylab.annotation.type.Taxon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class BiofidFlair extends RestAnnotator {
	@Override
	protected String getRestRoute() {
		return "/tag";
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		// Format:
		// {"sentences":["Das ist ein Test am 08.12.2020, einem Dienstag."]}

		// Collect all sentences texts from CAS in JSON array
		JSONArray sentences = new JSONArray();
		JCasUtil.select(aJCas, Sentence.class)
				.stream()
				.map(Annotation::getCoveredText)
				.forEach(sentences::put);

		// Pack in "sentences" object
		JSONObject payload = new JSONObject();
		payload.put("sentences", sentences);

		return payload;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		// Add taxon info to all taxons
		Collection<Taxon> taxons = JCasUtil.select(aJCas, Taxon.class);
		for (Taxon taxon : taxons) {
			taxon.setValue("Taxon;Gazetteer;" + taxon.getValue());
		}

		// merge flair results in cas
		// make sure to only add if no overlapping taxon is found
		for (Object sentenceObj : jsonResult.getJSONArray("results")) {
			JSONObject sentence = (JSONObject) sentenceObj;

			String sentenceText = sentence.getString("text");

			// Find this sentence in cas
			int textBegin = -1;
			for (Sentence casSentence : JCasUtil.select(aJCas, Sentence.class)) {
				if (casSentence.getCoveredText().equals(sentenceText)) {
					textBegin = casSentence.getBegin();
					break;
				}
			}

			if (textBegin < 0) {
				// this should not happen!
				throw new AnalysisEngineProcessException(new Exception("could not match text begin, this should not happen!"));
			}

			// add flair biofid annotations
			for (Object spanObj : sentence.getJSONArray("spans")) {
				JSONObject span = (JSONObject) spanObj;

				int spanBegin = textBegin + span.getInt("begin");
				int spanEnd = textBegin + span.getInt("end");
				String spanText = span.getString("text");

				// check for overlapping taxons...
				boolean ok = true;
				for (Taxon taxon : taxons) {
					if ((spanBegin >= taxon.getBegin() && spanBegin <= taxon.getEnd())
							|| (spanEnd >= taxon.getBegin() && spanEnd <= taxon.getEnd())) {
						// do not add flair result
						System.out.println("not adding flair span: " + spanBegin + "," + spanEnd + ": " + spanText);
						ok = false;
						break;
					}
				}

				for (Object labelObj : span.getJSONArray("labels")) {
					JSONObject label = (JSONObject) labelObj;

					double labelScore = label.getDouble("score");
					String classValue = label.getString("value");

					// get class from label
					String className = "org.texttechnologylab.annotation.type." + classValue;
					try {
						Class<? extends NamedEntity> annoClass = Class.forName(className).asSubclass(NamedEntity.class);
						Constructor<?> annoClassCtor = annoClass.getConstructor(JCas.class, int.class, int.class);
						NamedEntity anno = (NamedEntity) annoClassCtor.newInstance(aJCas, spanBegin, spanEnd);
						anno.setValue(classValue + ";Flair" + (ok ? "" : "-REMOVED") + ";score=" + labelScore);
						anno.addToIndexes();
					} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
