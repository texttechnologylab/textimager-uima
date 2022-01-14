/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.json;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.*;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.tweet.POS_NPV;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//import org.hucompute.services.type.html.li;
//import org.hucompute.services.type.html.ul;
//import org.hucompute.services.type.segmentation.Div;




/**
 * <h2>CAS serializer for JSON formats.</h2>
 * <p>Writes a CAS in a JSON format.</p>
 *
 * <p>To use,</p>
 * <ul>
 *   <li>create an instance of this class,</li>
 *   <li>(optionally) configure the instance, and then</li>
 *   <li>call serialize on the instance, optionally passing in additional parameters.</li></ul>
 *
 * <p>After the 1st 2 steps, the serializer instance may be used for multiple calls (on multiple threads) to
 * the 3rd serialize step, if all calls use the same configuration.</p>
 *
 * <p>There are "convenience" static serialize methods that do these three steps for common configurations.</p>
 *
 * <p>Parameters can be configured in this instance (I), and/or as part of the serialize(S) call.</p>
 *
 * <p>The parameters that can be configured are:</p>
 * <ul>
 *   <li>(S) The CAS to serialize
 *   <li>(S) where to put the output - an OutputStream, Writer, or File</li>
 *   <li>(I,S) a type system - (default null) if supplied, it is used to "filter" types and features that are serialized.  If provided, only
 *   those that exist in the passed in type system are included in the serialization</li>
 *   <li>(I,S) a flag for prettyprinting - default false (no prettyprinting)</li>
 * </ul>
 *
 * <p>For Json serialization, additional configuration from the Jackson implementation can be configured</p>
 * on 2 associated Jackson instances:
 *   <ul><li>JsonFactory</li>
 *       <li>JsonGenerator</li></ul>
 * using the standard Jackson methods on the associated JsonFactory instance;
 * see the Jackson JsonFactory and JsonGenerator javadocs for details.
 *
 * <p>These 2 Jackson objects are settable/gettable from an instance of this class.
 * They are created if not supplied by the caller.</p>
 *
 * <p>Once this instance is configured, the serialize method is called
 * to serialized a CAS to an output.</p>
 *
 * <p>Instances of this class must be used on only one thread while configuration is being done;
 * afterwards, multiple threads may use the configured instance, to call serialize.</p>
 */
public class JsonCasDeserializer {
	//	JSONObject context;

	public JsonCasDeserializer(){
		//		try {
		//			context = new JSONObject(FileUtils.readFileToString(new File("/home/ahemati/workspace/services/services-io/src/main/resources/context")));
		//		} catch (JSONException | IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	HashMap<Integer, AnnotationBase> annotations = new HashMap<>();
	ArrayList<JSONObject>postProcessItems = new ArrayList<>();

	public void deserialize(JCas cas, JSONObject json){

		//		DocElement element = new DocElement(cas);
		//		element.setName("xyz");
		//		element.setBegin(0);
		//		element.setEnd(5000);
		//		element.addToIndexes();

		JSONObject initialView = json.getJSONObject("_views").getJSONObject("_InitialView");
		for (String key : initialView.keySet()) {
			for (Object object : initialView.getJSONArray(key)) {
				//				if(context.getJSONObject("_context").getJSONObject("_types").has(key)){
				Annotation anno = createAnnotation(cas, (JSONObject)object, key);
				if(anno != null)
					anno.addToIndexes();
				//				}
			};
		}

		JSONObject refered = json.getJSONObject("_referenced_fss");
		for (String key : refered.keySet()) {
			JSONObject object = refered.getJSONObject(key);
			if(((JSONObject)object).getString("_type").equals("Sofa")){
				cas.setDocumentText(object.getString("sofaString"));
				//					createAnnotation(cas, (JSONObject)object, context.getJSONObject("_context").getJSONObject("_types").getJSONObject(key).getString("_id")).addToIndexes();;
			}
		}

		for (JSONObject jsonObject : postProcessItems) {
			if(jsonObject.has("DependencyType")){
				((Dependency)annotations.get(jsonObject.getInt("xid"))).setGovernor((Token)annotations.get(jsonObject.getInt("Governor")));
				((Dependency)annotations.get(jsonObject.getInt("xid"))).setDependent((Token)annotations.get(jsonObject.getInt("Dependent")));
			}
			if(jsonObject.has("lemma")){
				((Token)annotations.get(jsonObject.getInt("xid"))).setLemma((Lemma)annotations.get(jsonObject.getInt("lemma")));
			}

			if(jsonObject.has("pos"))
				((Token)annotations.get(jsonObject.getInt("xid"))).setPos((POS)annotations.get(jsonObject.getInt("pos")));
			if(jsonObject.has("morph"))
				((Token)annotations.get(jsonObject.getInt("xid"))).setMorph((MorphologicalFeatures)annotations.get(jsonObject.getInt("morph")));
		}

		/* TODO
		for (WikipediaLink wiki : JCasUtil.select(cas, WikipediaLink.class)) {
			if(!wiki.getTarget().contains("wikipedia.org"))
				wiki.setTarget("http://" +DocumentMetaData.get(cas).getLanguage() + ".wikipedia.org/w/index.php?title=" + wiki.getTarget());
		}*/
	}


	private Annotation createAnnotation(JCas cas, JSONObject object, String name){
		if(!object.has("b") || object.getInt("b") < 0)
			object.put("b", 0);
		switch (name) {
		case "DocumentMetaData":

			DocumentMetaData meta = DocumentMetaData.create(cas);
			meta.setBegin( object.has("b")?object.getInt("b"):0);
			meta.setEnd( object.has("e")?object.getInt("e"):0);
			meta.setLanguage(object.getString("language"));
			meta.setDocumentTitle(object.getString("documentTitle"));
			meta.setDocumentId(object.getString("documentId"));
			meta.setDocumentUri(object.getString("documentUri"));
			meta.setCollectionId(object.getString("collectionId"));
			meta.setDocumentBaseUri(object.getString("documentBaseUri"));
			meta.setIsLastSegment(object.getBoolean("isLastSegment"));
			annotations.put(object.getInt("xid"), meta);
			return meta;
			//		case "NN":
			//			NN nn = new NN(cas, object.getInt("b"), object.getInt("e"));
			//			nn.setPosValue(object.getString("PosValue"));
			//			return nn;
		case "MorphologicalFeatures":
			MorphologicalFeatures morph = new MorphologicalFeatures(cas, object.getInt("b"), object.getInt("e"));
			morph.setValue(object.getString("value"));
			annotations.put(object.getInt("xid"), morph);
			return morph;
		case "Token":
			Token tk = new Token(cas, object.getInt("b"), object.getInt("e"));
			annotations.put(object.getInt("xid"), tk);
			postProcessItems.add(object);
			return tk;
		case "Lemma":
			Lemma lemma = new Lemma(cas, object.getInt("b"), object.getInt("e"));
			lemma.setValue(object.getString("value"));
			annotations.put(object.getInt("xid"), lemma);
			return lemma;
		case "TagsetDescription":
			TagsetDescription tagsetDescription = new TagsetDescription(cas);
			tagsetDescription.setLayer(object.getString("layer"));
			tagsetDescription.setName(object.getString("name"));
			annotations.put(object.getInt("xid"), tagsetDescription);
			return tagsetDescription;
		case "Sentence":
			Sentence sentence = new Sentence(cas, object.getInt("b"), object.getInt("e"));
			annotations.put(object.getInt("xid"), sentence);
			return sentence;
		case "Paragraph":
			if(object.has("b")&& object.has("e")){
				Paragraph paragraph = new Paragraph(cas, object.getInt("b"), object.getInt("e"));
				annotations.put(object.getInt("xid"), paragraph);
				return paragraph;
			}
			else
				return null;
			/*
			 * POS Types
			 */


		case "POS_ADJ":
			return getPos(new POS_ADJ(cas), object);
		case "POS_ADP":
			return getPos(new POS(cas), object);
		case "POS_ADV":
			return getPos(new POS_ADV(cas), object);
		case "POS_CONJ":
			return getPos(new POS_CONJ(cas), object);
		case "POS_DET":
			return getPos(new POS_DET(cas), object);
		case "POS_NOUN":
			return getPos(new POS_NOUN(cas), object);
		case "POS_NUM":
			return getPos(new POS_NUM(cas), object);
		case "POS_PRON":
			return getPos(new POS_PRON(cas), object);
		case "POS_PROPN":
			return getPos(new POS_PROPN(cas), object);
		case "POS_PUNCT":
			return getPos(new POS_PUNCT(cas), object);
		case "POS_VERB":
			return getPos(new POS_VERB(cas), object);
		case "POS_NPV":
			return getPos(new POS_NPV(cas), object);
		case "POS_X":
			return getPos(new POS_X(cas), object);
		case "POS":
			return getPos(new POS(cas), object);
			/*
			 * Dependency Types
			 */
		case "Dependency":
			return getDep(new Dependency(cas), object);
		case "ABBREV":
			return getDep(new ABBREV(cas), object);
		case "ACOMP":
			return getDep(new ACOMP(cas), object);
		case "ADVCL":
			return getDep(new ADVCL(cas), object);
		case "ADVMOD":
			return getDep(new ADVMOD(cas), object);
		case "AGENT":
			return getDep(new AGENT(cas), object);
		case "AMOD":
			return getDep(new AMOD(cas), object);
		case "APPOS":
			return getDep(new APPOS(cas), object);
		case "ATTR":
			return getDep(new ATTR(cas), object);
		case "AUX0":
			return getDep(new AUX0(cas), object);
		case "AUXPASS":
			return getDep(new AUXPASS(cas), object);
		case "CC":
			return getDep(new CC(cas), object);
		case "CCOMP":
			return getDep(new CCOMP(cas), object);
		case "COMPLM":
			return getDep(new COMPLM(cas), object);
		case "dependency:CONJ":
			return getDep(new CONJ(cas), object);
		case "dependency2:CONJ":
			return getDep(new CONJ(cas), object);
		case "CONJ":
			if(object.has("DependencyType"))
				return getDep(new CONJ(cas), object);
			else
				return getPos(new POS_CONJ(cas), object);
		case "CONJ_YET":
			return getDep(new CONJ_YET(cas), object);
		case "CONJP":
			return getDep(new CONJP(cas), object);
		case "COP":
			return getDep(new COP(cas), object);
		case "CSUBJ":
			return getDep(new CSUBJ(cas), object);
		case "CSUBJPASS":
			return getDep(new CSUBJPASS(cas), object);
		case "DEP":
			return getDep(new DEP(cas), object);
		case "DET":
			return getDep(new DET(cas), object);
		case "DOBJ":
			return getDep(new DOBJ(cas), object);
		case "EXPL":
			return getDep(new EXPL(cas), object);
		case "INFMOD":
			return getDep(new INFMOD(cas), object);
		case "IOBJ":
			return getDep(new IOBJ(cas), object);
		case "MARK":
			return getDep(new MARK(cas), object);
		case "MEASURE":
			return getDep(new MEASURE(cas), object);
		case "MWE":
			return getDep(new MWE(cas), object);
		case "NEG":
			return getDep(new NEG(cas), object);
		case "dependency2:NN":
			return getDep(new de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.NN(cas), object);
		case "dependency:NN":
			return getDep(new de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.NN(cas), object);
		case "NN":
			if(object.has("DependencyType"))
				return getDep(new de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.NN(cas), object);
			else
				return getPos(new POS_NOUN(cas), object);
		case "NPADVMOD":
			return getDep(new NPADVMOD(cas), object);
		case "NSUBJ":
			return getDep(new NSUBJ(cas), object);
		case "NSUBJPASS":
			return getDep(new NSUBJPASS(cas), object);
		case "NUM":
			return getDep(new NUM(cas), object);
		case "NUMBER":
			return getDep(new NUMBER(cas), object);
		case "PARATAXIS":
			return getDep(new PARATAXIS(cas), object);
		case "PARTMOD":
			return getDep(new PARTMOD(cas), object);
		case "PCOMP":
			return getDep(new PCOMP(cas), object);
		case "POBJ":
			return getDep(new POBJ(cas), object);
		case "POSS":
			return getDep(new POSS(cas), object);
		case "POSSESSIVE":
			return getDep(new POSSESSIVE(cas), object);
		case "PRECONJ":
			return getDep(new PRECONJ(cas), object);
		case "PRED":
			return getDep(new PRED(cas), object);
		case "PREDET":
			return getDep(new PREDET(cas), object);
		case "PREP":
			return getDep(new PREP(cas), object);
		case "PREPC":
			return getDep(new PREPC(cas), object);
		case "PRT":
			if(object.has("DependencyType"))
				return getDep(new PRT(cas), object);
			else
				return getPos(new POS_PART(cas), object);
		case "PUNCT":
			return getDep(new PUNCT(cas), object);
		case "PURPCL":
			return getDep(new PURPCL(cas), object);
		case "QUANTMOD":
			return getDep(new QUANTMOD(cas), object);
		case "RCMOD":
			return getDep(new RCMOD(cas), object);
		case "REF":
			return getDep(new REF(cas), object);
		case "REL":
			return getDep(new REL(cas), object);
		case "ROOT":
			return getDep(new ROOT(cas), object);
		case "TMOD":
			return getDep(new TMOD(cas), object);
		case "XCOMP":
			return getDep(new XCOMP(cas), object);
		case "XSUBJ":
			return getDep(new XSUBJ(cas), object);
			/* TODO
		case "Wikify":
			//			Wikipedia
			WikipediaLink wiki = new WikipediaLink(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
			wiki.setLinkType("internal");
			wiki.setTarget(object.getString("link"));
			annotations.put(object.getInt("xid"), wiki);
			return wiki;
		case "WikipediaLink":
			//			Wikipedia
			try{
				org.hucompute.textimager.uima.type.wikipedia.WikipediaLink WikipediaLink = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
				WikipediaLink.setLinkType(object.getString("LinkType"));
				WikipediaLink.setTarget(object.getString("Target"));
				if(object.has("WikiData")){
					WikipediaLink.setWikiData(object.getString("WikiData"));
					JSONArray wikidataHyponyms = null;
					if(object.has("WikiDataHyponyms"))
						wikidataHyponyms = object.getJSONArray("WikiDataHyponyms");
					else{
						try {
							List<String> wikidatas = new WikidataHyponyms().wikidataHyponyms(object.getString("WikiData"));
							wikidataHyponyms = new JSONArray(wikidatas.toString());
						} catch (JSONException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					WikipediaLink.setWikiDataHyponyms(new StringArray(cas, wikidataHyponyms.length()));
					for (int i = 0; i < wikidataHyponyms.length(); i++) {
						if(wikidataHyponyms.get(i) instanceof String)
							WikipediaLink.setWikiDataHyponyms(i, wikidataHyponyms.getString(i));
					}
				}
				annotations.put(object.getInt("xid"), WikipediaLink);
				return WikipediaLink;
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
			 */
			//		case "ul":
			//			ul ul= new ul(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
			//			annotations.put(object.getInt("xid"), ul);
			//			return ul;
			//		case "li":
			//			li li= new li(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
			//			annotations.put(object.getInt("xid"), li);
			//			return li;

		case "Div":
			Div div = new Div(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
			if(object.has("id"))
				div.setId( object.getString("id"));
			if(object.has("divType"))
				div.setDivType(object.getString("divType"));
			annotations.put(object.getInt("xid"), div);
			return div;
		case "CategoryCoveredTagged":
			CategoryCoveredTagged categoryCoveredTagged = new CategoryCoveredTagged(cas, object.getInt("b"), object.has("e")?object.getInt("e"):0);
			if(object.has("value"))
				categoryCoveredTagged.setValue(object.getString("value"));
			if(object.has("score"))
				categoryCoveredTagged.setScore(object.getDouble("score"));
			if(object.has("tags"))
				categoryCoveredTagged.setTags(object.getString("tags"));
			return categoryCoveredTagged;
		default:
			return null;
		}
	}

	public <T extends POS> T getPos(T pos, JSONObject object){
		pos.setBegin(object.getInt("b"));
		pos.setEnd(object.getInt("e"));
		pos.setPosValue(object.getString("PosValue"));
		annotations.put(object.getInt("xid"), pos);
		return pos;
	}

	public <T extends Dependency> T getDep(T dep, JSONObject object){
		postProcessItems.add(object);
		dep.setBegin(object.getInt("b"));
		dep.setEnd(object.getInt("e"));
		dep.setDependencyType(object.getString("DependencyType"));
		annotations.put(object.getInt("xid"), dep);
		return dep;
	}

}
