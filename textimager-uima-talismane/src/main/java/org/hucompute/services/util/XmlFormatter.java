package org.hucompute.services.util;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Pretty-prints xml, supplied as a string.
 * <p/>
 * eg.
 * <code>
 * String formattedXml = new XmlFormatter().format("<tag><nested>hello</nested></tag>");
 * </code>
 */
public class XmlFormatter {

	public XmlFormatter() {
	}

	public static String format(String unformattedXml) {
		try {
			final Document document = parseXmlFile(unformattedXml);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			 transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//			    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//			    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			return(xmlString);


		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String printDocument(String unformattedXml) throws IOException, TransformerException {
		final Document document = parseXmlFile(unformattedXml);

	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		StreamResult result = new StreamResult(new StringWriter());
	    DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		return(xmlString);
	}

	private static Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getString(CAS jCas) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			XmiCasSerializer.serialize(jCas, baos);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}

	public static String getPrettyString(CAS jcas){
		return format(getString(jcas));
	}
}