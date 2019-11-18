package textimager.uima.io.abby.annotation;

import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

public class StructuralElement extends Annotation {
	
	public final int top;
	public final int bottom;
	public final int left;
	public final int right;
	
	public StructuralElement(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}
	
	public StructuralElement(Attributes attributes) {
		this.top = Util.parseInt(attributes.getValue("t"));
		this.bottom = Util.parseInt(attributes.getValue("b"));
		this.left = Util.parseInt(attributes.getValue("l"));
		this.right = Util.parseInt(attributes.getValue("r"));
	}
}
