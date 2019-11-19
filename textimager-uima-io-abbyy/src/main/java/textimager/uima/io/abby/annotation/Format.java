package textimager.uima.io.abby.annotation;

import org.apache.uima.jcas.JCas;
import org.texttechnologylab.annotation.ocr.OCRFormat;
import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

public class Format extends Annotation {
	public String lang;
	public String ff;
	public float fs;
	public boolean bold;
	public boolean italic;
	public boolean subscript;
	public boolean superscript;
	public boolean smallcaps;
	public boolean underline;
	public boolean strikeout;
	
	public Format(String lang, String ff, float fs) {
		this.lang = lang;
		this.ff = ff;
		this.fs = fs;
	}
	
	public Format(Attributes attributes) {
		this.lang = attributes.getValue("lang");
		this.ff = attributes.getValue("ff");
		this.fs = Util.parseFloat(attributes.getValue("fs"));
		this.bold = Util.parseBoolean(attributes.getValue("bold"));
		this.italic = Util.parseBoolean(attributes.getValue("italic"));
		this.subscript = Util.parseBoolean(attributes.getValue("subscript"));
		this.superscript = Util.parseBoolean(attributes.getValue("superscript"));
		this.smallcaps = Util.parseBoolean(attributes.getValue("smallcaps"));
		this.underline = Util.parseBoolean(attributes.getValue("underline"));
		this.strikeout = Util.parseBoolean(attributes.getValue("strikeout"));
	}
	
	@Override
	public OCRFormat wrap(JCas jCas, int offset) {
		OCRFormat ocrFormat = new OCRFormat(jCas, start + offset, end + offset);
		ocrFormat.setLang(lang);
		ocrFormat.setFf(ff);
		ocrFormat.setFs(fs);
		ocrFormat.setBold(bold);
		ocrFormat.setItalic(italic);
		ocrFormat.setSubscript(subscript);
		ocrFormat.setSuperscript(superscript);
		ocrFormat.setUnderline(underline);
		ocrFormat.setStrikeout(strikeout);
		return ocrFormat;
	}
}
