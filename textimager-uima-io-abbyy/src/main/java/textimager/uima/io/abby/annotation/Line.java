package textimager.uima.io.abby.annotation;

import org.apache.uima.jcas.JCas;
import org.texttechnologylab.annotation.ocr.OCRLine;
import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

public class Line extends StructuralElement {
	
	public final int baseline;
	
	public Format OCRFormat; // FIXME: Evaluate whether there are lines with multiple <formatting> tags!
	
	public Line(int baseline, int top, int bottom, int left, int right) {
		super(top, bottom, left, right);
		this.baseline = baseline;
	}
	
	public Line(Attributes attributes) {
		super(attributes);
		this.baseline = Util.parseInt(attributes.getValue("baseline"));
	}
	
	@Override
	public OCRLine wrap(JCas jCas, int offset) {
		OCRLine ocrLine = new OCRLine(jCas, start + offset, end + offset);
		ocrLine.setBaseline(baseline);
		ocrLine.setTop(top);
		ocrLine.setBottom(bottom);
		ocrLine.setLeft(left);
		ocrLine.setRight(right);
		return ocrLine;
	}
}
