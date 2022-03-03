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
package org.hucompute.textimage.uima.tagme;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.tagme.TagMeAPIAnnotator;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TagMeAPIAnnotatorTest{
	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Mona Lisa ist ein weltberühmtes Ölgemälde von Leonardo da Vinci aus der Hochphase der italienischen Renaissance Anfang des 16. Jahrhunderts.", "de");
		Paragraph paragraph = new Paragraph(cas,0,cas.getDocumentText().length());
		paragraph.addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(
				TagMeAPIAnnotator.class,
				TagMeAPIAnnotator.PARAM_GCUBE_TOKEN,"685b6106-bba0-43e2-87b6-ad8ea0c8f9e2-843339462",
				TagMeAPIAnnotator.PARAM_RHO,0.01f));
		SimplePipeline.runPipeline(cas,builder.createAggregate());


		ArrayList<WikipediaLink>links = new ArrayList<>(JCasUtil.select(cas, WikipediaLink.class));
		assertEquals(links.size(),5);

		assertEquals(links.get(0).getTarget(), "Mona_Lisa");
		assertEquals(links.get(0).getBegin(), 0);
		assertEquals(links.get(0).getEnd(), 9);

		assertEquals(links.get(1).getTarget(), "Ölmalerei");
		assertEquals(links.get(1).getBegin(), 32);
		assertEquals(links.get(1).getEnd(), 41);

		assertEquals(links.get(2).getTarget(), "Leonardo_da_Vinci");
		assertEquals(links.get(3).getTarget(), "Italienische_Sprache");
		assertEquals(links.get(4).getTarget(), "Renaissance");
	}
}
