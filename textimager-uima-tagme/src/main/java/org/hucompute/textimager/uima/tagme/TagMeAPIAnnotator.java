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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;

/**
 * Stanford Part-of-Speech tagger component.
 *
 */
@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"})
public class TagMeAPIAnnotator
extends JCasAnnotator_ImplBase
{
	/**
	 * gcube-token
	 */
	public static final String PARAM_GCUBE_TOKEN = "gcube-token";
	@ConfigurationParameter(name = PARAM_GCUBE_TOKEN, mandatory = true)
	protected String gcube_token;
	
	/**
	 * Threshold
	 */
	public static final String PARAM_RHO = "rho";
	@ConfigurationParameter(name = PARAM_RHO, mandatory = false, defaultValue = "0.10f")
	protected float rho;

	@Override
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException
	{
		try{
			try {
				JSONObject doc = new JSONObject(Jsoup.connect("https://tagme.d4science.org/tagme/tag")
						.data("lang",aJCas.getDocumentLanguage())
						.data("gcube-token",gcube_token)
						.data("text", aJCas.getDocumentText())
						.ignoreContentType(true)
						.validateTLSCertificates(false)
						.timeout(0)
						.post().text());
				for (Object iterable_element : doc.getJSONArray("annotations")) {
					JSONObject object = (JSONObject)iterable_element;
					System.out.println(object.toString(4));
					if(object.getDouble("rho") > rho && JCasUtil.selectCovered(aJCas, WikipediaLink.class, object.getInt("start"), object.getInt("end")).isEmpty()){
						WikipediaLink wiki = new WikipediaLink(aJCas, object.getInt("start"), object.getInt("end"));
						if(object.has("title")){
							wiki.setTarget(object.getString("title").replace(" ", "_"));
							wiki.setLinkType("internal");
							wiki.addToIndexes();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				for (Paragraph paragraph: JCasUtil.select(aJCas, Paragraph.class)) {
					if(JCasUtil.selectCovered(Sentence.class, paragraph).size() > 0)
						try {
							JSONObject doc = new JSONObject(Jsoup.connect("https://tagme.d4science.org/tagme/tag")
									.data("lang",aJCas.getDocumentLanguage())
									.data("gcube-token",gcube_token)
									.data("text", paragraph.getCoveredText())
									.ignoreContentType(true)
									.validateTLSCertificates(false)
									.timeout(0)
									.post().text());
							for (Object iterable_element : doc.getJSONArray("annotations")) {
								JSONObject object = (JSONObject)iterable_element;
								if(object.getDouble("rho") > rho && JCasUtil.selectCovered(aJCas, WikipediaLink.class, paragraph.getBegin() + object.getInt("start"), paragraph.getBegin() + object.getInt("end")).isEmpty()){
									WikipediaLink wiki = new WikipediaLink(aJCas, paragraph.getBegin() + object.getInt("start"), paragraph.getBegin() + object.getInt("end"));
									wiki.setTarget(object.getString("title").replace(" ", "_"));
									wiki.setLinkType("internal");
									wiki.addToIndexes();
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}	
				}

			}	
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
