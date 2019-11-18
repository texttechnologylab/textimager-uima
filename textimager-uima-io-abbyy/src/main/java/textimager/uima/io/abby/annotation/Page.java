package textimager.uima.io.abby.annotation;

import org.apache.uima.jcas.JCas;
import org.texttechnologylab.annotation.ocr.OCRPage;
import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

public class Page extends Annotation {
	private Integer width;
	private Integer height;
	private Integer resolution;
	private boolean originalCoords;
	
	public String pageId;
	public Integer pageNumber;
	
	public Page(Attributes attributes) {
		this.width = Util.parseInt(attributes.getValue("width"));
		this.height = Util.parseInt(attributes.getValue("height"));
		this.resolution = Util.parseInt(attributes.getValue("resolution"));
		this.originalCoords = Util.parseBoolean(attributes.getValue("originalCoords"));
	}
	
	@Override
	public OCRPage wrap(JCas jCas, int offset) {
		OCRPage ocrPage = new OCRPage(jCas, start + offset, end + offset);
		ocrPage.setWidth(width);
		ocrPage.setHeight(height);
		ocrPage.setResolution(resolution);
		ocrPage.setPageId(pageId);
		ocrPage.setPageNumber(pageNumber);
		return ocrPage;
	}
}
