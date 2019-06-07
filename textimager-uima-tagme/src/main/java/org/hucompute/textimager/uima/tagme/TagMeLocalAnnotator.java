/**
 * Copyright 2007-2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.hucompute.textimager.uima.tagme;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;
import it.acubelab.tagme.AnnotatedText;
import it.acubelab.tagme.Annotation;
import it.acubelab.tagme.Disambiguator;
import it.acubelab.tagme.RelatednessMeasure;
import it.acubelab.tagme.RhoMeasure;
import it.acubelab.tagme.Segmentation;
import it.acubelab.tagme.TagmeParser;
import it.acubelab.tagme.config.TagmeConfig;
import it.acubelab.tagme.preprocessing.TopicSearcher;

/**
 * TagMe tagger component.
 *
 */
@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph"},
		outputs = {"de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink"})
public class TagMeLocalAnnotator
extends JCasAnnotator_ImplBase
{
	/**
	 * Number of Threads for wiki service call.
	 */
	public static final String PARAM_CONFIG_PATH = "config_path";
	@ConfigurationParameter(name = PARAM_CONFIG_PATH, mandatory = true)
	protected String config_path;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		TagmeConfig.init(config_path);
	}

	@Override
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException
	{
		for (Paragraph paragraph : JCasUtil.select(aJCas, Paragraph.class)) {
			RelatednessMeasure rel;
			Disambiguator disamb; 
			Segmentation segmentation;
			RhoMeasure rho;
			TagmeParser parser = null;

			String lang = aJCas.getDocumentLanguage();

			rel = RelatednessMeasure.create(lang);
			try {
				parser = new TagmeParser(lang, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			disamb = new Disambiguator(lang);
			segmentation = new Segmentation();
			rho = new RhoMeasure();
			AnnotatedText ann_text = new AnnotatedText(paragraph.getCoveredText());
			
			parser.parse(ann_text);
			if(ann_text.getAnnotations().size()>1000)
				continue;
			segmentation.segment(ann_text);
			disamb.disambiguate(ann_text, rel);
			rho.calc(ann_text, rel);

			List<Annotation> annots = ann_text.getAnnotations();
			TopicSearcher searcher;
			try {
				searcher = new TopicSearcher(lang);
				for (Annotation a : annots) {
					if (a.isDisambiguated() && a.getRho() >= 0.1) {
						int begin = ann_text.getOriginalTextStart(a) + paragraph.getBegin();
						int end = ann_text.getOriginalTextEnd(a) + paragraph.getBegin();
						if(JCasUtil.selectCovered(aJCas, WikipediaLink.class, begin, end).size()==0 && searcher.getTitle(a.getTopic()) != null){
							WikipediaLink wiki = new WikipediaLink(aJCas, begin , end);
							wiki.setTarget(searcher.getTitle(a.getTopic()).replace(" ", "_"));
							wiki.setLinkType("internal");
							wiki.addToIndexes();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
