package org.hucompute.textimager.uima.io.pdf;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Heading;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.io.pdf.Pdf2CasConverter;
import de.tudarmstadt.ukp.dkpro.core.io.pdf.SubstitutionTrieParser;
import de.tudarmstadt.ukp.dkpro.core.io.pdf.Trie;

/**
 * Collection reader for PDF files. Uses simple heuristics to detect headings and paragraphs.
 *
 * @author Richard Eckart de Castilho
 */
@TypeCapability(
		outputs = {
		"de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData" })
public class PdfReader
{
	public static final String BUILT_IN = "<built-in>";
	private static final String NOT_RESTRICTED = "-1";

	/**
	 * The location of the substitution table use to post-process the text extracted form the PDF,
	 * e.g. to convert ligatures to separate characters.
	 */
	public static final String PARAM_SUBSTITUTION_TABLE_LOCATION = "substitutionTableLocation";
	@ConfigurationParameter(name = PARAM_SUBSTITUTION_TABLE_LOCATION, mandatory = false, defaultValue = BUILT_IN)
	private String substitutionTableLocation = BUILT_IN;

	/**
	 * The type used to annotate headings.
	 */
	public static final String PARAM_HEADING_TYPE = "headingType";
	@ConfigurationParameter(name = PARAM_HEADING_TYPE, mandatory = false, defaultValue = BUILT_IN)
	private String headingType = BUILT_IN;

	/**
	 * The type used to annotate paragraphs.
	 */
	public static final String PARAM_PARAGRAPH_TYPE = "paragraphType";
	@ConfigurationParameter(name = PARAM_PARAGRAPH_TYPE, mandatory = false, defaultValue = BUILT_IN)
	private String paragraphType = BUILT_IN;

	/**
	 * The first page to be extracted from the PDF.
	 */
	public static final String PARAM_START_PAGE = "startPage";
	@ConfigurationParameter(name = PARAM_START_PAGE, mandatory = false, defaultValue = NOT_RESTRICTED)
	private int startPage = -1;

	/**
	 * The last page to be extracted from the PDF.
	 */
	public static final String PARAM_END_PAGE = "endPage";
	@ConfigurationParameter(name = PARAM_END_PAGE, mandatory = false, defaultValue = NOT_RESTRICTED)
	private int endPage = -1;

	private Trie<String> substitutionTable;

	public void initialize()
			throws ResourceInitializationException
	{

		if (BUILT_IN.equals(headingType)) {
			headingType = Heading.class.getName();
		}

		if (BUILT_IN.equals(paragraphType)) {
			paragraphType = Paragraph.class.getName();
		}

		if (substitutionTableLocation != null) {
			if (BUILT_IN.equals(substitutionTableLocation)) {
				substitutionTableLocation = "classpath:/de/tudarmstadt/ukp/dkpro/core/io/pdf/substitutionTable.xml";
			}

			InputStream is = null;
			try {
				URL url = ResourceUtils.resolveLocation(substitutionTableLocation, this, null);
				is = url.openStream();
				substitutionTable = SubstitutionTrieParser.parse(is);
			}
			catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
			finally {
				closeQuietly(is);
			}
		}
		else {
			substitutionTable = null;
		}
	}

	public JCas init(String input) throws UIMAException, IOException{
		InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		initialize();
		JCas jcas = JCasFactory.createJCas();
		try {
			final Pdf2CasConverter converter = new Pdf2CasConverter();
			converter.setSubstitutionTable(substitutionTable);
			converter.setHeadingType(headingType);
			converter.setParagraphType(paragraphType);
			if (startPage != Integer.parseInt(NOT_RESTRICTED)) {
				converter.setStartPage(startPage);
			}
			if (endPage != Integer.parseInt(NOT_RESTRICTED)) {
				converter.setEndPage(endPage);
			}
			converter.writeText(jcas.getCas(), is);
		}
		finally {
			closeQuietly(is);
		}
		return jcas;
	}

//	public void getNext(CAS aCAS)
//			throws IOException, CollectionException
//	{
//		Resource resource = nextFile();
//		initCas(aCAS, resource, null);
//
//		InputStream is = null;
//		try {
//			is = resource.getInputStream();
//			final Pdf2CasConverter converter = new Pdf2CasConverter();
//			converter.setSubstitutionTable(substitutionTable);
//			converter.setHeadingType(headingType);
//			converter.setParagraphType(paragraphType);
//			if (startPage != Integer.parseInt(NOT_RESTRICTED)) {
//				converter.setStartPage(startPage);
//			}
//			if (endPage != Integer.parseInt(NOT_RESTRICTED)) {
//				converter.setEndPage(endPage);
//			}
//			converter.writeText(aCAS, is);
//		}
//		finally {
//			closeQuietly(is);
//		}
//	}
}