package org.hucompute.textimager.uima.io.tei;

import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.ATTR_FUNCTION;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.ATTR_POS;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.ATTR_TYPE;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_CHARACTER;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_MULTIWORD;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_PARAGRAPH;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_PHRASE;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_RS;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_SUNIT;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_TEI_DOC;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_TEXT;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_TITLE;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_U;
import static de.tudarmstadt.ukp.dkpro.core.io.tei.internal.TeiConstants.TAG_WORD;
import static java.util.Arrays.asList;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.hucompute.textimager.uima.type.Wikify;
import org.hucompute.textimager.uima.type.WikipediaInformation;
import org.hucompute.textimager.uima.type.segmentation.Head;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.ROOT;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

/**
 * Reader for the TEI XML.
 */
@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent",
		"de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity"})
public class TeiReader
extends ResourceCollectionReaderBase
{
	/**
	 * Write token annotations to the CAS.
	 */
	public static final String PARAM_READ_TOKEN = ComponentParameters.PARAM_READ_TOKEN;
	@ConfigurationParameter(name = PARAM_READ_TOKEN, mandatory = true, defaultValue = "true")
	private boolean readToken;

	/**
	 * Write part-of-speech annotations to the CAS.
	 */
	public static final String PARAM_READ_POS = ComponentParameters.PARAM_READ_POS;
	@ConfigurationParameter(name = PARAM_READ_POS, mandatory = true, defaultValue = "true")
	private boolean readPOS;

	/**
	 * Write lemma annotations to the CAS.
	 */
	public static final String PARAM_READ_LEMMA = ComponentParameters.PARAM_READ_LEMMA;
	@ConfigurationParameter(name = PARAM_READ_LEMMA, mandatory = true, defaultValue = "true")
	private boolean readLemma;

	/**
	 * Write sentence annotations to the CAS.
	 */
	public static final String PARAM_READ_SENTENCE = ComponentParameters.PARAM_READ_SENTENCE;
	@ConfigurationParameter(name = PARAM_READ_SENTENCE, mandatory = true, defaultValue = "true")
	private boolean readSentence;

	/**
	 * Write constituent annotations to the CAS.
	 */
	public static final String PARAM_READ_CONSTITUENT = ComponentParameters.PARAM_READ_CONSTITUENT;
	@ConfigurationParameter(name = PARAM_READ_CONSTITUENT, mandatory = true, defaultValue = "true")
	private boolean readConstituent;

	/**
	 * Write named entity annotations to the CAS.
	 */
	public static final String PARAM_READ_NAMED_ENTITY = ComponentParameters.PARAM_READ_NAMED_ENTITY;
	@ConfigurationParameter(name = PARAM_READ_NAMED_ENTITY, mandatory = true, defaultValue = "true")
	private boolean readNamedEntity;

	/**
	 * Write paragraphs annotations to the CAS.
	 */
	public static final String PARAM_READ_PARAGRAPH = "readParagraph";
	@ConfigurationParameter(name = PARAM_READ_PARAGRAPH, mandatory = true, defaultValue = "true")
	private boolean readParagraph;

	/**
	 * Use the xml:id attribute on the TEI elements as document ID. Mind that many TEI files
	 * may not have this attribute on all TEI elements and you may end up with no document ID
	 * at all. Also mind that the IDs should be unique.
	 */
	public static final String PARAM_USE_XML_ID = "useXmlId";
	@ConfigurationParameter(name = PARAM_USE_XML_ID, mandatory = true, defaultValue = "false")
	private boolean useXmlId;

	/**
	 * When not using the XML ID, use only the filename instead of the whole URL as ID. Mind that
	 * the filenames should be unique in this case.
	 */
	public static final String PARAM_USE_FILENAME_ID = "useFilenameId";
	@ConfigurationParameter(name = PARAM_USE_FILENAME_ID, mandatory = true, defaultValue = "false")
	private boolean useFilenameId;

	/**
	 * Do not write <em>ignoreable whitespace</em> from the XML file to the CAS.
	 */
	// REC: This does not seem to work. Maybe because SAXWriter does not generate this event?
	public static final String PARAM_OMIT_IGNORABLE_WHITESPACE = "omitIgnorableWhitespace";
	@ConfigurationParameter(name = PARAM_OMIT_IGNORABLE_WHITESPACE, mandatory = true, defaultValue = "true")
	private boolean omitIgnorableWhitespace;

	/**
	 * Location of the mapping file for part-of-speech tags to UIMA types.
	 */
	public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
	protected String mappingPosLocation;

	/**
	 * Use this part-of-speech tag set to use to resolve the tag set mapping instead of using the
	 * tag set defined as part of the model meta data. This can be useful if a custom model is
	 * specified which does not have such meta data, or it can be used in readers.
	 */
	public static final String PARAM_POS_TAG_SET = ComponentParameters.PARAM_POS_TAG_SET;
	@ConfigurationParameter(name = PARAM_POS_TAG_SET, mandatory = false)
	protected String posTagset;

	/**
	 * Interpret utterances "u" as sentenes "s". (EXPERIMENTAL)
	 */
	public static final String PARAM_UTTERANCES_AS_SENTENCES = "utterancesAsSentences";
	@ConfigurationParameter(name = PARAM_UTTERANCES_AS_SENTENCES, mandatory = true, defaultValue = "false")
	private boolean utterancesAsSentences;

	/**
	 * Interpret utterances "u" as sentenes "s". (EXPERIMENTAL)
	 */
	public static final String PARAM_USE_TITLE_AS_DOCUMENT_ID= "useTitleAsDocumentId";
	@ConfigurationParameter(name = PARAM_USE_TITLE_AS_DOCUMENT_ID, mandatory = false, defaultValue = "false")
	private boolean useTitleAsDocumentId;

	/**
	 * Interpret utterances "u" as sentenes "s". (EXPERIMENTAL)
	 */
	public static final String PARAM_TITEL_AS_DOCUMENT_ID = "titleAsDocumentId";
	@ConfigurationParameter(name = PARAM_TITEL_AS_DOCUMENT_ID, mandatory = true, defaultValue = "false")
	private boolean titleAsDocumentId;

	public static final String PARAM_TAKE_LITERATUR = "PARAM_TAKE_LITERATUR";
	@ConfigurationParameter(name = PARAM_TAKE_LITERATUR, mandatory = true, defaultValue = "true")
	private boolean takeLiteratur;

	public static final String PARAM_TAKE_TABLES = "PARAM_TAKE_TABLES";
	@ConfigurationParameter(name = PARAM_TAKE_TABLES, mandatory = true, defaultValue = "true")
	private boolean takeTables;

	public static final String PARAM_TAKE_LISTS = "PARAM_TAKE_LISTS";
	@ConfigurationParameter(name = PARAM_TAKE_LISTS, mandatory = true, defaultValue = "true")
	private boolean takeList;

	private Iterator<Element> teiElementIterator;
	private Element currentTeiElement;
	private Resource currentResource;
	private int currentTeiElementNumber;

	private MappingProvider posMappingProvider;

	private static final String ATTR_LEMMA = "lemma";
	private static final String ATTR_CASE = "case";
	private static final String ATTR_NUMBER = "number";
	private static final String ATTR_GENDER = "gender";
	private static final String ATTR_PERSON= "person";
	private static final String ATTR_TENSE = "tense";
	private static final String ATTR_MOOD = "mood";
	private static final String ATTR_DEGREE = "degree";
	private static final String ATTR_VOICE = "voice";

	private static final String ATTR_DEPINDEX = "depIndex";
	private static final String ATTR_DEPTYPE = "depType";
	private static final String ATTR_DEPHEAD = "depHead";

	private static final String TAG_DIVUNIT = "div";
	private static final String TAG_HEADUNIT = "head";
	private static final String TAG_PUBLICATIONSTMT = "publicationStmt";
	private static final String TAG_IDNO = "idno";
	

	int size = 0;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException
	{
		super.initialize(aContext);
		if (readPOS && !readToken) {
			throw new ResourceInitializationException(new IllegalArgumentException(
					"Setting readPOS to 'true' requires writeToken to be 'true' too."));
		}

		try {
			// Init with an empty iterator
			teiElementIterator = asList(new Element[0]).iterator();
			
			// Make sure we know about the first element;
			nextTeiElement();
		}
		catch (CollectionException e) {
			new ResourceInitializationException(e);
		}
		catch (IOException e) {
			new ResourceInitializationException(e);
		}

		posMappingProvider = MappingProviderFactory.createPosMappingProvider(mappingPosLocation,
				posTagset, getLanguage());
	}
	
	//	@Override
	//	public Progress[] getProgress()
	//	{
	//		return new Progress[] { new ProgressImpl(
	//				Long.valueOf(processed).intValue(),
	//				Long.valueOf(size).intValue(), Progress.ENTITIES) };
	//	}

	private void nextTeiElement() throws CollectionException, IOException
	{
		if (teiElementIterator == null) {
			currentTeiElement = null;
			return;
		}
		while (!teiElementIterator.hasNext() && super.hasNext()) {
			currentResource = nextFile();

			InputStream is = null;
			try {
				is = currentResource.getInputStream();

				if (currentResource.getPath().endsWith(".gz")) {
					is = new GZIPInputStream(is);
				}

				InputSource source = new InputSource(is);
				source.setPublicId(currentResource.getLocation());
				source.setSystemId(currentResource.getLocation());


				SAXReader reader = new SAXReader();
				Document xml = reader.read(source);

				final XPath teiPath = new Dom4jXPath("//TEI");
				//								teiPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");

				List<Element> teiElements = teiPath.selectNodes(xml);
				size = teiElements.size();
				//				System.out.printf("Found %d TEI elements in %s.%n", teiElements.size(),
				//						currentResource.getLocation());

				teiElementIterator = teiElements.iterator();
				currentTeiElementNumber = 0;
			}
			catch (DocumentException e) {
				throw new IOException(e);
			}
			catch (JaxenException e) {
				throw new IOException(e);
			}
			finally {
				closeQuietly(is);
			}
		}

		currentTeiElement = teiElementIterator.hasNext() ? teiElementIterator.next() : null;
		currentTeiElementNumber++;

		if (!super.hasNext() && !teiElementIterator.hasNext()) {
			// Mark end of processing.
			teiElementIterator = null;
		}
	}

	@Override
	public boolean hasNext()
			throws IOException, CollectionException
	{
		return teiElementIterator != null || currentTeiElement != null;
	}

	int processed = 0;
	@Override
	public void getNext(CAS aCAS)
			throws IOException, CollectionException
	{
		System.out.println(processed);
		initCas(aCAS, currentResource);

		// Set up language
		//		if (getConfigParameterValue(PARAM_LANGUAGE) != null) {
		aCAS.setDocumentLanguage("de");
		//		}

		// Configure mapping only now, because now the language is set in the CAS
		try {
			posMappingProvider.configure(aCAS);
		}
		catch (AnalysisEngineProcessException e1) {
			throw new IOException(e1);
		}

		InputStream is = null;


		try {


			JCas jcas = aCAS.getJCas();
			boolean first = true;
			//			while(DocumentMetaData.get(aCAS).getDocumentTitle().startsWith("Liste von") ||DocumentMetaData.get(aCAS).getDocumentTitle().startsWith("Diskussion") || first){
			first = false;
			// Create handler
			Handler handler = newSaxHandler();
			handler.setJCas(jcas);
			handler.setLogger(getLogger());

			// Parse TEI text
			SAXWriter writer = new SAXWriter(handler);
			writer.write(currentTeiElement);
			handler.endDocument();
			//			}

		}
		catch(CASRuntimeException e){
			System.out.println(currentResource.getPath());
			throw e;
		}
		catch (CASException e) {
			System.out.println(currentResource.getPath());
			throw new CollectionException(e);
		}
		catch (SAXException e) {
			System.out.println(currentResource.getPath());
			e.printStackTrace();
		}
		catch(NullPointerException e){
			e.printStackTrace();
			System.out.println(currentResource.getPath());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			closeQuietly(is);
		}

		// Move currentTeiElement to the next text
		nextTeiElement();
		processed++;
	}

	protected Handler newSaxHandler()
	{
		return new TeiHandler();
	}

	protected abstract static class Handler
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
		private boolean titleSet = false;
		private boolean inTextElement = false;
		private boolean captureText = true;
		private int paragraphStart = -1;
		private int sentenceStart = -1;
		private int tokenStart = -1;
		private int refStart = -1;
		private String refTyp = null;
		private String refTarget = null;
		private String refTitle = null;
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

		private String depIndex = null; 
		private String depType = null; 
		private String depHead = null;

		private int divStart = -1;
		private String divTyp = null;
		private String divId = null;
		private String divSection = null;
		private String divUser = null;
		private String divTimestamp = null;

		private int headStart = -1;
		private String headTyp = null;
		private String headId = null;
		private String headChildren = null;
		private String headParent = null;
		private String headRootEntries = null;

		private String pageURL = null;
		private String pageID = null;
		private String revisionID = null;
		private String namespaceID = null;
		private String namespace = null;
		private String timestamp = null;

		private String idnoType = null;
		private int idnoStart = -1;
		private int titleStart = -1;

		private int openTable = 0;
		private int openList = 0;

		private boolean inLiteratur = false;

		HashMap<Integer,Word>wordsInSentence = new HashMap<>();


		private Stack<ConstituentWrapper> constituents = new Stack<>();
		private Stack<NamedEntity> namedEntities = new Stack<>();

		private final StringBuilder buffer = new StringBuilder();
		private ArrayList<String>kategories = new ArrayList<>();

		@Override
		public void endDocument()
				throws SAXException
		{
			try{
				getJCas().setDocumentText(buffer.toString());
			}catch(Exception e){
				getJCas().release();
				e.printStackTrace();
			}
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
					documentId = currentResource.getPath()+"#"+currentTeiElementNumber;
				}
			}
			else if (!inTextElement && TAG_TITLE.equals(aName)) {
				captureText = true;
				titleStart = getBuffer().length();
			}
			else if (TAG_TEXT.equals(aName)) {
				captureText = true;
				inTextElement = true;
			}
			else if(TAG_PUBLICATIONSTMT.equals(aName)){
				captureText = true;
				kategories = new ArrayList<>();
			}
			else if(TAG_DIVUNIT.equals(aName)){
				divStart = getBuffer().length();
				divTyp = aAttributes.getValue(ATTR_TYPE);
				divId = aAttributes.getValue("id");
				divSection = aAttributes.getValue("section");
				divUser = aAttributes.getValue("user");
				divTimestamp = aAttributes.getValue("timestamp");
			}
			else if(TAG_HEADUNIT.equals(aName)){
				headStart = getBuffer().length();
				headTyp = aAttributes.getValue(ATTR_TYPE);
				headId = aAttributes.getValue("id");
				headParent = aAttributes.getValue("parent");
				headRootEntries = aAttributes.getValue("rootEntries");
				headChildren = aAttributes.getValue("children");
			}
			else if (inTextElement && (TAG_SUNIT.equals(aName) || 
					(utterancesAsSentences && TAG_U.equals(aName)))) {
				sentenceStart = getBuffer().length();
			}
			else if (inTextElement && TAG_PARAGRAPH.equals(aName)) {
				paragraphStart = getBuffer().length();
			}
			else if (readNamedEntity && inTextElement && TAG_RS.equals(aName)) {
				NamedEntity ne = new NamedEntity(getJCas());
				ne.setBegin(getBuffer().length());
				ne.setValue(aAttributes.getValue(ATTR_TYPE));
				namedEntities.push(ne);
			}
			else if (readConstituent && inTextElement && TAG_PHRASE.equals(aName)) {
				if (constituents.isEmpty()) {
					ROOT root = new ROOT(getJCas());
					root.setBegin(getBuffer().length());
					root.setConstituentType("ROOT");
					constituents.push(new ConstituentWrapper(root));
				}

				Constituent constituent = new Constituent(getJCas());
				constituent.setBegin(getBuffer().length());
				constituent.setConstituentType(aAttributes.getValue(ATTR_TYPE));
				constituent.setSyntacticFunction(aAttributes.getValue(ATTR_FUNCTION));
				constituents.push(new ConstituentWrapper(constituent));
			}
			else if (inTextElement
					&& (TAG_WORD.equals(aName) || TAG_CHARACTER.equals(aName) || TAG_MULTIWORD
							.equals(aName))) {
				tokenStart = getBuffer().length();




				if (StringUtils.isNotEmpty(aAttributes.getValue(ATTR_POS))) {
					posTag = aAttributes.getValue(ATTR_POS);
				}
				else {
					posTag = aAttributes.getValue(ATTR_TYPE);
				}
				lemma = aAttributes.getValue(ATTR_LEMMA);


				casee = aAttributes.getValue(ATTR_CASE);
				number = aAttributes.getValue(ATTR_NUMBER);
				gender = aAttributes.getValue(ATTR_GENDER);
				person = aAttributes.getValue(ATTR_PERSON);
				tense = aAttributes.getValue(ATTR_TENSE);
				mood = aAttributes.getValue(ATTR_MOOD);
				degree = aAttributes.getValue(ATTR_DEGREE);
				voice = aAttributes.getValue(ATTR_VOICE);

				depIndex = aAttributes.getValue(ATTR_DEPINDEX);
				depHead = aAttributes.getValue(ATTR_DEPHEAD);
				depType = aAttributes.getValue(ATTR_DEPTYPE);

			}else if(inTextElement && (aName.equals("ref"))){
				refStart = getBuffer().length();
				refTyp = aAttributes.getValue("type");
				refTarget = aAttributes.getValue("target");
				refTitle = aAttributes.getValue("title");
			}else if(TAG_IDNO.equals(aName)){
				idnoType = aAttributes.getValue("type");
				idnoStart = getBuffer().length();
			}else if(aName.equals("table")&&!takeTables){
				captureText = false;
				openTable++;
			}else if(aName.equals("list")&&!takeList){
				captureText = false;
				openList++;
			}
		}

		@Override
		public void endElement(String aUri, String aLocalName, String aName)
				throws SAXException
		{
			if(TAG_TEI_DOC.equals(aName)){
				inLiteratur = false;
			}
			if (!inTextElement && TAG_TITLE.equals(aName)) {
				DocumentMetaData meta = DocumentMetaData.get(getJCas());
				// Read only the first title and hope it is the main title
				if (!titleSet) {
					meta.setDocumentTitle(getBuffer().substring(titleStart));
					titleSet = true;
				}
				if(useTitleAsDocumentId)
					meta.setDocumentId(getBuffer().substring(titleStart));
				else
					meta.setDocumentId(documentId);
				getBuffer().setLength(0);
				captureText = false;
			}
			else if (TAG_TEXT.equals(aName)) {
				captureText = false;
				inTextElement = false;
			}
//			else if (captureText && TAG_DIVUNIT.equals(aName)) {
//				Div div = new Div(getJCas(), divStart, getBuffer().length());
//				div.setTyp(divTyp);
//				div.setId(divId);
//				div.setSection(divSection);
//				div.setUser(divUser);
//				div.setTimestamp(divTimestamp);
//				div.addToIndexes();
//				divStart = -1;
//			}
			else if (captureText && TAG_HEADUNIT.equals(aName)) {
				if(headStart >-1 &&(getBuffer().substring(headStart).trim().contains("Literatur") || 
						getBuffer().substring(headStart).trim().contains("Siehe auch")) && 
						!takeLiteratur){
					getBuffer().setLength(headStart);
					captureText = false;
					inLiteratur = true;
				}
				else{
					Head div = new Head(getJCas(), headStart, getBuffer().length());
					div.setTyp(headTyp);
					div.setId(headId);
					div.setChildren(headChildren);
					div.setParent(headParent);
					div.setRootEntries(headRootEntries);
					div.addToIndexes();
				}
				headStart = -1;
			}
			else if (inTextElement && (TAG_SUNIT.equals(aName) ||
					(utterancesAsSentences && TAG_U.equals(aName)))) {
				if (readSentence) {
					new Sentence(getJCas(), sentenceStart, getBuffer().length()).addToIndexes();
				}
				construct(getJCas(),wordsInSentence);
				wordsInSentence.clear();
				sentenceStart = -1;
			}
			else if (inTextElement && TAG_PARAGRAPH.equals(aName)) {
				if (readParagraph && getBuffer().length()>0) {
					try{
						new Paragraph(getJCas(), paragraphStart, getBuffer().length()).addToIndexes();
					}catch(NullPointerException|ClassCastException e){
						e.printStackTrace();
					}
				}
				paragraphStart = -1;
			}
			else if (readNamedEntity && inTextElement && TAG_RS.equals(aName)) {
				NamedEntity ne = namedEntities.pop();
				ne.setEnd(getBuffer().length());
				ne.addToIndexes();
			}
			else if (readConstituent && inTextElement && TAG_PHRASE.equals(aName)) {
				ConstituentWrapper wrapper = constituents.pop();
				wrapper.constituent.setEnd(getBuffer().length());
				if (!constituents.isEmpty()) {
					ConstituentWrapper parent = constituents.peek();
					wrapper.constituent.setParent(parent.constituent);
					parent.children.add(wrapper.constituent);
				}
				wrapper.constituent.setChildren(FSCollectionFactory.createFSArray(getJCas(),
						wrapper.children));
				wrapper.constituent.addToIndexes();

				// Close off the ROOT
				if (constituents.peek().constituent instanceof ROOT) {
					ConstituentWrapper rootWrapper = constituents.pop();
					rootWrapper.constituent.setEnd(getBuffer().length());
					rootWrapper.constituent.setChildren(FSCollectionFactory.createFSArray(
							getJCas(), rootWrapper.children));
					rootWrapper.constituent.addToIndexes();
				}
			}
			else if (inTextElement
					&& (TAG_WORD.equals(aName) || TAG_CHARACTER.equals(aName) || TAG_MULTIWORD
							.equals(aName))) {
				if (isNotBlank(getBuffer().substring(tokenStart, getBuffer().length()))) {
					Token token = new Token(getJCas(), tokenStart, getBuffer().length());
					trim(token);

					if (posTag != null && readPOS) {
						Type posTagType = posMappingProvider.getTagType(posTag);
						POS pos = (POS) getJCas().getCas().createAnnotation(posTagType,
								token.getBegin(), token.getEnd());
						pos.setPosValue(posTag);
						pos.addToIndexes();
						token.setPos(pos);
					}else{
						de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.O pos = new de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.O(getJCas(),
								token.getBegin(), token.getEnd());
						pos.setPosValue("O");
						pos.addToIndexes();
						token.setPos(pos);
					}

					if (lemma != null && readLemma) {
						Lemma l = new Lemma(getJCas(), token.getBegin(), token.getEnd());
						l.setValue(lemma);
						l.addToIndexes();
						token.setLemma(l);
					}else{
						Lemma l = new Lemma(getJCas(), token.getBegin(), token.getEnd());
						l.setValue(getBuffer().substring(tokenStart, getBuffer().length()));
						l.addToIndexes();
						token.setLemma(l);
					}

					// FIXME: if readToken is disabled, the JCas wrapper should not be generated
					// at all!
					if (readToken) {
						if (!constituents.isEmpty()) {
							ConstituentWrapper parent = constituents.peek();
							token.setParent(parent.constituent);
							parent.children.add(token);
						}

						token.addToIndexes();
					}

					if (casee != null || number != null || gender != null || person != null || tense != null || mood != null || 
							degree != null || voice != null ) {
						//						GrammaticalCategory cat = new GrammaticalCategory(getJCas(),token.getBegin(),token.getEnd());
						//						cat.setDegree(degree);
						//						cat.setCasus(casee);
						//						cat.setNumber(number);
						//						cat.setGender(gender);
						//						cat.setPerson(person);
						//						cat.setTense(tense);
						//						cat.setMood(mood);
						//						cat.setVoice(voice);
						//						cat.addToIndexes();

						MorphologicalFeatures morph = new MorphologicalFeatures(getJCas(),token.getBegin(),token.getEnd());
						morph.setCase(casee);
						morph.setDegree(degree);
						morph.setNumber(number);
						morph.setGender(gender);
						morph.setPerson(person);
						morph.setTense(tense);
						morph.setMood(mood);
						morph.setVoice(voice);
						morph.addToIndexes();

						token.setMorph(morph);

						casee = null;
						number = null;
						gender = null;
						person = null;
						tense = null;
						mood = null;
						degree = null;
						voice = null;
					}

					if(depIndex!=null)
						wordsInSentence.put(Integer.parseInt(depIndex),new Word(token,depIndex,depHead,depType));

				}

				tokenStart = -1;
			}
			else if(inTextElement && (aName.equals("ref"))){
				if(refTyp!= null && refTyp.equals("internal")){
					Wikify wiki = new Wikify(getJCas(),refStart,getBuffer().length());
					wiki.setLink(refTarget);
					wiki.addToIndexes();
				}else{
					Wikify wiki = new Wikify(getJCas(),refStart,getBuffer().length());
					wiki.setLink(refTarget);
					wiki.setTitle(refTitle);
					wiki.addToIndexes();
				}
			}
			else if(TAG_PUBLICATIONSTMT.equals(aName)){
				WikipediaInformation wiki = new WikipediaInformation(getJCas());
				wiki.setPageURL(pageURL);
				wiki.setPageID(pageID);
				wiki.setRevisionID(revisionID);
				wiki.setNamespace(namespace);;
				wiki.setNamespaceID(namespaceID);
				wiki.setTimestamp(timestamp);

				StringArray categories = new StringArray(getJCas(), kategories.size());
				for (int i = 0; i < kategories.size(); i++) {
					categories.set(i, kategories.get(i));
				}
				wiki.setCategories(categories);
				wiki.addToIndexes();
				captureText = false;
			}else if(TAG_IDNO.equals(aName) && idnoType!=null){
				switch (idnoType) {
				case "pageURL":
					pageURL = getBuffer().substring(idnoStart);
					break;
				case "pageID":
					pageID = getBuffer().substring(idnoStart);
					break;
				case "revisionID":
					revisionID = getBuffer().substring(idnoStart);
					break;
				case "namespaceID":
					namespaceID = getBuffer().substring(idnoStart);
					break;
				case "namespace":
					namespace = getBuffer().substring(idnoStart);
					break;
				case "timestamp":
					timestamp = getBuffer().substring(idnoStart);
					break;
				case "category":
					kategories.add(getBuffer().substring(idnoStart));
				default:
					break;
				}
				idnoStart = -1;
			}else if(aName.equals("table")){
				openTable--;
				if(openTable == 0 && !inLiteratur)
					captureText = true;
			}else if(aName.equals("list")){
				openList--;
				if(openList == 0 && !inLiteratur)
					captureText = true;
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

	private class Word{
		public int depIndex;
		public int depHead;
		public String depType;
		public Token token;

		public Word(Token token, String depIndex, String depHead, String depType){
			if(depHead != null)
				this.depHead = Integer.parseInt(depHead);
			if(depIndex != null)
				this.depIndex = Integer.parseInt(depIndex);
			this.depType = depType;
			this.token = token;
		}

		public String toString(){
			return "{"+token.getLemma().getValue()+","+depIndex+","+depHead+","+depType+"}";
		}
	}

	private void construct(JCas cas,HashMap<Integer,Word>wordsInSentence){
		for (Word word : wordsInSentence.values()) {
			if(word.depHead == 0){
				de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT dependency = new de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT(cas, word.token.getBegin(), word.token.getEnd());
				dependency.setDependencyType("--");
				dependency.setGovernor(word.token);
				dependency.setDependent(word.token);
				dependency.addToIndexes();
			}
			else{
				Dependency dependency = new Dependency(cas, word.token.getBegin(), word.token.getEnd());
				dependency.setDependencyType(word.depType);
				try{
					dependency.setGovernor(wordsInSentence.get(word.depHead).token);
				}catch(NullPointerException e){
					System.out.println(wordsInSentence);
				}
				dependency.setDependent(word.token);
				dependency.addToIndexes();
			}
		}
	}

	private static class ConstituentWrapper {
		public Constituent constituent;
		public List<Annotation> children = new ArrayList<Annotation>();

		public ConstituentWrapper(Constituent aConstituent)
		{
			constituent = aConstituent;
		}
	}
}