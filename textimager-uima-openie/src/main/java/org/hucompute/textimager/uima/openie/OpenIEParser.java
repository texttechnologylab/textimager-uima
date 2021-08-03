package org.hucompute.textimager.uima.openie;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.ModelProviderBase;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import edu.knowitall.openie.Argument;
import scala.collection.Seq;
import scala.collection.JavaConversions;


public class OpenIEParser extends JCasAnnotator_ImplBase {

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;

	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;

	protected OpenIE openie;


	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		this.openie = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer())), new ClearSrl(), false, false);
	}


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println(aJCas.getDocumentText());

		Seq<Instance> extractions = this.openie.extract(aJCas.getDocumentText());
		List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);

		for (Instance instance : list_extractions) {
			List<Argument> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());
			for (Argument argument : list_arg2s) {
				System.out.print(instance.extr().arg1().displayText() + "\t");
				System.out.print(instance.extr().arg1().offsets() + "\t");
				System.out.print(instance.extr().rel().text() + "\t");
				System.out.print(instance.extr().rel().offsets() + "\t");
				System.out.print(argument.text() + "\t");
				System.out.print(argument.offsets() + "\t");
				System.out.println(instance.confidence() + "\t");
			}
		}

		/*
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			System.out.println(sentence.getCoveredText());
		}
		*/
	}

	@Override
	public void destroy() {
		super.destroy();
		// destroy openie
	}
}
