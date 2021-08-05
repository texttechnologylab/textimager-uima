package org.hucompute.textimager.uima.openie;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.knowitall.collection.immutable.Interval;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import edu.knowitall.openie.Argument;
import scala.collection.Seq;
import scala.collection.JavaConversions;

import org.hucompute.textimager.uima.type.OpenIERelation;

public class OpenIEParser extends JCasAnnotator_ImplBase {

	protected OpenIE openie;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		this.openie = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer())), new ClearSrl(), false, false);
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		Seq<Instance> extractions = this.openie.extract(aJCas.getDocumentText());
		List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);

		for (Instance instance : list_extractions) {
			List<Argument> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());
			for (Argument arg2 : list_arg2s) {
				OpenIERelation relation = new OpenIERelation(aJCas);
				relation.setConfidence(instance.confidence());

				List<Interval> arg1Offsets = JavaConversions.seqAsJavaList(instance.extr().arg1().offsets());
				relation.setBeginArg1(arg1Offsets.get(0).start());
				relation.setEndArg1(arg1Offsets.get(0).end());
				relation.setValueArg1(instance.extr().arg1().displayText());

				List<Interval> relOffsets = JavaConversions.seqAsJavaList(instance.extr().rel().offsets());
				relation.setBeginRel(relOffsets.get(0).start());
				relation.setEndRel(relOffsets.get(0).end());
				relation.setValueRel(instance.extr().rel().displayText());

				List<Interval> arg2Offsets = JavaConversions.seqAsJavaList(arg2.offsets());
				relation.setBeginArg2(arg2Offsets.get(0).start());
				relation.setEndArg2(arg2Offsets.get(0).end());
				relation.setValueArg2(arg2.displayText());

				relation.setBegin(arg1Offsets.get(0).start());
				relation.setEnd(arg2Offsets.get(0).end());

				aJCas.addFsToIndexes(relation);
			}
		}
	}
}
