package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.transformers.BaseTransformers;
import org.hucompute.textimager.uima.transformers.NERTransformers;
import org.hucompute.textimager.uima.transformers.SentAnaTransformers;
import org.hucompute.textimager.uima.transformers.TextSumTransformers;
import org.hucompute.textimager.uima.util.XmlFormatter;


import jep.JepException;

public class SimpleTestTransformers {

	public static void main(String[] args) throws UIMAException, JepException {


			JCas cas = JCasFactory.createText("Abu'l-Walid Ismail I ibn Faraj (Arabic: 3 March 1279 – 8 July 1325) was the fifth Nasrid ruler of the Emirate of Granada on the Iberian Peninsula from 1314 to 1325. A grandson of Muhammad II on the side of his mother Fatima, he was the first of the lineage of sultans now known as the al-dawla al-isma'iliyya al-nasriyya (the Nasrid dynasty of Ismail). Historians characterise him as an effective ruler who improved the emirate's position with military victories during his reign.\r\n" + 
					"\r\n" + 
					"He claimed the throne during the reign of his maternal uncle, Sultan Nasr, after a rebellion started by his father Abu Said Faraj. Their forces defeated the unpopular Nasr and Ismail was proclaimed sultan in the Alhambra in February 1314. He spent the early years of his reign fighting Nasr, who attempted to regain the throne from his base in Guadix, where he was initially allowed to rule as governor. Nasr enlisted the help of Castile, which then secured a papal authorisation for a crusade against Ismail. The war continued with intermittent truces and reached its climax in the Battle of the Vega on 25 June 1319, which resulted in a complete victory for Ismail's forces, led by Uthman ibn Abi al-Ula, over Castile. The deaths in the battle of Infante Peter and Infante John, the two regents for the infant King Alfonso XI, left Castile leaderless and forced it to end support for Nasr. ","en");

			AggregateBuilder builder = new AggregateBuilder();

			//builder.add(createEngineDescription(NERTransformers.class,NERTransformers.PARAM_PYTHON_HOME,"C:\\Users\\PC\\AppData\\Local\\Programs\\Python\\Python38"));
			//builder.add(createEngineDescription(SentAnaTransformers.class,SentAnaTransformers.PARAM_PYTHON_HOME,"C:\\Users\\PC\\AppData\\Local\\Programs\\Python\\Python38"));
			builder.add(createEngineDescription(TextSumTransformers.class,TextSumTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
			SimplePipeline.runPipeline(cas,builder.createAggregate());

			System.out.println(XmlFormatter.getPrettyString(cas.getCas()));



		}
}
