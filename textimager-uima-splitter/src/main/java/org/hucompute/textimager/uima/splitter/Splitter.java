package org.hucompute.textimager.uima.splitter;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.util.XMLSerializer;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.api.resources.CompressionUtils;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.AnnotatorMetaData;

import javax.xml.transform.OutputKeys;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

public class Splitter extends JCasConsumer_ImplBase {
	public static final String PARAM_TARGET_LOCATION = "targetLocation";
	@ConfigurationParameter(name = PARAM_TARGET_LOCATION)
	protected File targetLocation;

	public static final String PARAM_COMPRESSION = "compression";
	@ConfigurationParameter(name = PARAM_COMPRESSION)
	protected CompressionMethod compression;

	// XMI/XML
	protected boolean prettyPrint = true;
	protected String version = "1.0";

	// TODO make dynamic...

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// look for this tool and model
		String toolName = "org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3";
		String modelName = "";

		// keep this dependencies (ROOT is subclass...)
		Set<Dependency> dependencies = new HashSet<>();

		// keep mapping of all meta to remove later
		Map<TOP, List<TOP>> dependenciesMeta = new HashMap<>();

		{
			Set<Dependency> dependenciesModel = new HashSet<>();
			for (AnnotationComment comment : JCasUtil.select(aJCas, AnnotationComment.class)) {
				if (!modelName.isEmpty()) {
					if (comment.getKey().equals("model")) {
						if (comment.getValue().equals(modelName)) {
							Dependency ref = (Dependency) comment.getReference();
							dependenciesModel.add(ref);
						}
					}
				}

				if (!dependenciesMeta.containsKey(comment.getReference())) {
					dependenciesMeta.put(comment.getReference(), new ArrayList<>());
				}
				dependenciesMeta.get(comment.getReference()).add(comment);
			}

			Set<Dependency> dependenciesTool = new HashSet<>();
			for (AnnotatorMetaData meta : JCasUtil.select(aJCas, AnnotatorMetaData.class)) {
				if (meta.getName().equals(toolName)) {
					try {
						Dependency ref = (Dependency) meta.getReference();
						dependenciesTool.add(ref);
					}
					catch (Exception ex) {
						// ignored
					}
				}

				if (!dependenciesMeta.containsKey(meta.getReference())) {
					dependenciesMeta.put(meta.getReference(), new ArrayList<>());
				}
				dependenciesMeta.get(meta.getReference()).add(meta);
			}

			// only keep if model name and tool name matches
			// except if model is empty, keep always
			for (Dependency dep : dependenciesTool) {
				if (modelName.isEmpty() || dependenciesModel.contains(dep)) {
					dependencies.add(dep);
				}
			}
		}

		// remove all dependencies not in set
		for (Dependency dep : JCasUtil.select(aJCas, Dependency.class)) {
			if (!dependencies.contains(dep)) {
				// remove
				for (TOP ref : dependenciesMeta.get(dep)) {
					ref.removeFromIndexes();
				}

				dep.removeFromIndexes();
			}
		}

		// save cleaned doc
		DocumentMetaData metaData = DocumentMetaData.get(aJCas);
		String[] splits = toolName.split("\\.", -1);
		String toolShortName = splits[splits.length-1];

		String filename = targetLocation.getAbsolutePath() + "/" + metaData.getDocumentId() + "_tool_" + toolShortName + "_model_" + modelName.replaceAll("/", "_") + ".xmi" + compression.getExtension();
		File file = new File(filename);

		// TODO integrate in JCasFileWriter_ImplBase?
		try (OutputStream singularTargetStream = CompressionUtils.getOutputStream(file)) {
			JCasFileWriter_ImplBase.NamedOutputStream out = new JCasFileWriter_ImplBase.NamedOutputStream(file.getAbsolutePath(), new CloseShieldOutputStream(singularTargetStream));
			XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
			XMLSerializer sax2xml = new XMLSerializer(out, prettyPrint);
			sax2xml.setOutputProperty(OutputKeys.VERSION, version);
			xmiCasSerializer.serialize(aJCas.getCas(), sax2xml.getContentHandler(), null, null, null);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
