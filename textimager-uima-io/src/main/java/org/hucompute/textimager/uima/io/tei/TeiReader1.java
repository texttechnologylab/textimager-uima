package org.hucompute.textimager.uima.io.tei;


import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.Logger;
import org.dkpro.core.api.resources.MappingProvider;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
//import org.jaxen.XPath;
//import org.jaxen.dom4j.Dom4jXPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Reader for the TEI XML.
 *
 * @author Richard Eckart de Castilho
 */
public class TeiReader1
{


	/**
	 * (character) contains a significant punctuation mark as identified by the CLAWS tagger.
	 */
	private static final String TAG_CHARACTER = "c";

	/**
	 * (word) represents a grammatical (not necessarily orthographic) word.
	 */
	private static final String TAG_WORD = "w";

	/**
	 * (s-unit) contains a sentence-like division of a text.
	 */
	private static final String TAG_SUNIT = "s";
	private static final String TAG_DIVUNIT = "div";
	private static final String TAG_HEADUNIT = "head";

	/**
	 * contains a single text of any kind, whether unitary or composite, for example a poem or
	 * drama, a collection of essays, a novel, a dictionary, or a corpus sample.
	 */
	private static final String TAG_TEXT = "text";

	/**
	 * contains the full title of a work of any kind.
	 */
	private static final String TAG_TITLE = "title";

	/**
	 * (TEI document) contains a single TEI-conformant document, comprising a TEI header and a text,
	 * either in isolation or as part of a teiCorpus element.
	 */
	private static final String TAG_TEI_DOC = "TEI";

	private static final String ATTR_TYPE = "type";

	private static final String ATTR_LEMMA = "lemma";
	private static final String ATTR_CASE = "case";
	private static final String ATTR_NUMBER = "number";
	private static final String ATTR_GENDER = "gender";
	private static final String ATTR_PERSON= "person";
	private static final String ATTR_TENSE = "tense";
	private static final String ATTR_MOOD = "mood";
	private static final String ATTR_DEGREE = "degree";
	private static final String ATTR_VOICE = "voice";


	private Iterator<Element> teiElementIterator;
	private Element currentTeiElement;
	private int currentTeiElementNumber;

	private MappingProvider posMappingProvider;


	private boolean useXmlId = false;
	private boolean useFilenameId = false;
	private Node currentResource = null;
	private boolean writeSentences = true;
	private boolean writePOS = true;
	private boolean writeLemma = true;
	private boolean writeTokens = true;
	private boolean omitIgnorableWhitespace = true;
	//	private int offset = 0;

	public JCas init(String input) throws IOException, SAXException, UIMAException
	{
		//		this.offset = offset;
		try {
			posMappingProvider = new MappingProvider();
			posMappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/" +
					"core/api/lexmorph/tagset/${language}-${pos.tagset}-pos.map");
			posMappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
			posMappingProvider.setDefault("pos.tagset", "default");

			SAXReader reader = new SAXReader();
			Document xml =  reader.read(new StringReader(input));

			final XPath teiPath = new Dom4jXPath("//teicorpus/TEI");
			//			teiPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");

			List<Element> teiElements = teiPath.selectNodes(xml);
			System.out.println(teiElements.size());

			teiElementIterator = teiElements.iterator();
			currentTeiElementNumber = 0;
		}
		catch (DocumentException e) {
			throw new IOException(e);
		}
		catch (JaxenException e) {
			throw new IOException(e);
		}

		currentTeiElement = teiElementIterator.hasNext() ? teiElementIterator.next() : null;

		JCas jcas = JCasFactory.createJCas();
		DocumentMetaData meta = DocumentMetaData.create(jcas);
		meta.setDocumentId("1");
		//		meta.addToIndexes();

		// Create handler
		Handler handler = newSaxHandler();
		handler.setJCas(jcas);


		// Parse TEI text
		SAXWriter writer = new SAXWriter(handler);
		writer.write(currentTeiElement);
		handler.endDocument();

		jcas.setDocumentLanguage("x-unspecified");
		return jcas;


	}

	protected Handler newSaxHandler()
	{
		return new TeiHandler();
	}

	/**
	 * @author Richard Eckart de Castilho
	 */
	public abstract static class Handler
	extends DefaultHandler
	{
		private JCas jcas;
		private Logger logger;

		public void setJCas(final JCas aJCas)
		{
			jcas = aJCas;
		}

		protected JCas getJCas()
		{
			return jcas;
		}

		public void setLogger(Logger aLogger)
		{
			logger = aLogger;
		}

		public Logger getLogger()
		{
			return logger;
		}
	}

	public class TeiHandler
	extends Handler
	{
		private String documentId = null;
		private boolean inTextElement = false;
		private boolean captureText = false;
		private int sentenceStart = -1;
		private int tokenStart = -1;
		
		private int divStart = -1;
		private String divTyp = null;
		
		private int headStart = -1;
		private String headTyp = null;
		
		private String posTag = null;
		private String lemma = null;

		private String casee = null;
		private String number = null;
		private String gender = null;
		private String person = null;
		private String tense = null;
		private String mood = null;
		private String degree = null;
		private String voice = null;


		private final StringBuilder buffer = new StringBuilder();

		private int skipS = 0;

		@Override
		public void endDocument()
				throws SAXException
		{
			getJCas().setDocumentText(buffer.toString());

		}

		protected StringBuilder getBuffer()
		{
			return buffer;
		}

		@Override
		public void startElement(String aUri, String aLocalName, String aName,
				Attributes aAttributes)
						throws SAXException
		{

			//			System.out.println(aName);
			//			System.out.printf("%b START %s %n", captureText, aLocalName);
			if (!inTextElement && TAG_TEI_DOC.equals(aName)) {
				if (useXmlId) {
					documentId = aAttributes.getValue("xml:id");
				}
				else if (useFilenameId) {
					documentId = FilenameUtils.getName(currentResource.getPath()) + "#"
							+ currentTeiElementNumber;
				}
				else {
					//					documentId = currentResource.getPath()+"#"+currentTeiElementNumber;
				}
			}
			else if (!inTextElement && TAG_TITLE.equals(aName)) {
				captureText = true;
			}
			else if (TAG_TEXT.equals(aName)) {
				captureText = true;
				inTextElement = true;
			}
			else if(TAG_DIVUNIT.equals(aName)){
				divStart = getBuffer().length();
				divTyp = aAttributes.getValue(ATTR_TYPE);
			}
			else if(TAG_HEADUNIT.equals(aName)){
				headStart = getBuffer().length();
				headTyp = aAttributes.getValue(ATTR_TYPE);
			}
			else if (TAG_SUNIT.equals(aName)) {
				try{
					if(aAttributes.getValue("n").equals("1")){
						sentenceStart = getBuffer().length();
					}else{
						skipS++;
					}
				}catch(NullPointerException e){
					sentenceStart = getBuffer().length();
				}
			}
			else if (TAG_WORD.equals(aName) || TAG_CHARACTER.equals(aName)) {
				tokenStart = getBuffer().length();
				posTag = aAttributes.getValue(ATTR_TYPE);
				lemma = aAttributes.getValue(ATTR_LEMMA);



				casee = aAttributes.getValue(ATTR_CASE);
				number = aAttributes.getValue(ATTR_NUMBER);
				gender = aAttributes.getValue(ATTR_GENDER);
				person = aAttributes.getValue(ATTR_PERSON);
				tense = aAttributes.getValue(ATTR_TENSE);
				mood = aAttributes.getValue(ATTR_MOOD);
				degree = aAttributes.getValue(ATTR_DEGREE);
				voice = aAttributes.getValue(ATTR_VOICE);

			}
		}

		@Override
		public void endElement(String aUri, String aLocalName, String aName)
				throws SAXException
		{
			//			System.out.printf("%b END %s %n", captureText, aLocalName);
			if (!inTextElement && TAG_TITLE.equals(aName)) {
				DocumentMetaData.get(getJCas()).setDocumentTitle(getBuffer().toString().trim());
				DocumentMetaData.get(getJCas()).setDocumentId(documentId);
				getBuffer().setLength(0);
				captureText = false;
			}
			else if (TAG_TEXT.equals(aName)) {
				captureText = false;
				inTextElement = false;
			}
			else if (TAG_DIVUNIT.equals(aName)) {
				Div div = new Div(getJCas(), divStart, getBuffer().length());
				div.setDivType(divTyp);
				div.addToIndexes();
				divStart = -1;
			}
			else if (TAG_HEADUNIT.equals(aName)) {
				Div div = new Div(getJCas(), headStart, getBuffer().length());
				div.setDivType(headTyp);
				div.addToIndexes();
				headStart = -1;
			}
			else if (TAG_SUNIT.equals(aName)) {
				if(skipS > 0){
					skipS--;
				}
				else{
					if (writeSentences) {
						new Sentence(getJCas(), sentenceStart, getBuffer().length()).addToIndexes();
					}
					sentenceStart = -1;
				}
			}
			else if (TAG_WORD.equals(aName) || TAG_CHARACTER.equals(aName)) {
				if (isNotBlank(getBuffer().substring(tokenStart, getBuffer().length()))) {
					Token token = new Token(getJCas(), tokenStart, getBuffer().length());
					trim(token);

					if (posTag != null && writePOS) {
						//												Type posTagType = posMappingProvider.getTagType(posTag);
						POS pos = new POS(getJCas(),token.getBegin(),token.getEnd());
						//						Type posTagType = posMappingProvider.getTagType(posTag);
						//						POS pos = (POS) getJCas().getCas().createAnnotation(posTagType, 
						//								token.getBegin(), token.getEnd());
						pos.setPosValue(posTag);
						pos.addToIndexes(getJCas());
						token.setPos(pos);
					}

					if (lemma != null && writeLemma) {
						Lemma l = new Lemma(getJCas(), token.getBegin(), token.getEnd());
						l.setValue(lemma);
						l.addToIndexes(getJCas());
						token.setLemma(l);
					}

					if (casee != null || number != null || gender != null || person != null || tense != null || mood != null || 
							degree != null || voice != null ) {
						MorphologicalFeatures cat = new MorphologicalFeatures(getJCas(),token.getBegin(),token.getEnd());
						cat.setDegree(degree);
						cat.setCase(casee);
						cat.setNumber(number);
						cat.setGender(gender);
						cat.setPerson(person);
						cat.setTense(tense);
						cat.setMood(mood);
						cat.setVoice(voice);
						cat.addToIndexes();

						casee = null;
						number = null;
						gender = null;
						person = null;
						tense = null;
						mood = null;
						degree = null;
						voice = null;
					}

					// FIXME: if writeTokens is disabled, the JCas wrapper should not be generated
					// at all!
					if (writeTokens) {
						token.addToIndexes();
					}
				}

				tokenStart = -1;
			}
		}

		@Override
		public void characters(char[] aCh, int aStart, int aLength)
				throws SAXException
		{
			if (captureText) {
				buffer.append(aCh, aStart, aLength);
			}
		}

		@Override
		public void ignorableWhitespace(char[] aCh, int aStart, int aLength)
				throws SAXException
		{
			if (captureText && !omitIgnorableWhitespace) {
				buffer.append(aCh, aStart, aLength);
			}
		}

		private void trim(Annotation aAnnotation)
		{
			StringBuilder buffer = getBuffer();
			int s = aAnnotation.getBegin();
			int e = aAnnotation.getEnd();
			while (Character.isWhitespace(buffer.charAt(s))) {
				s++;
			}
			while ((e > s+1) && Character.isWhitespace(buffer.charAt(e-1))) {
				e--;
			}
			aAnnotation.setBegin(s);
			aAnnotation.setEnd(e);
		}
	}
}