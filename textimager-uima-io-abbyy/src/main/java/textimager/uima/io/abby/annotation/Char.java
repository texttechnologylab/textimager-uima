package textimager.uima.io.abby.annotation;

import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

/**
 * Created on 21.01.2019.
 */
public class Char extends Annotation {
	public final int top;
	public final int bottom;
	public final int left;
	public final int right;
	
	public Char(Attributes attributes) {
		this.top = Util.parseInt(attributes.getValue("t"));
		this.bottom = Util.parseInt(attributes.getValue("b"));
		this.left = Util.parseInt(attributes.getValue("l"));
		this.right = Util.parseInt(attributes.getValue("r"));
	}
	
	// TODO: wrap & type for Char if needed!
}
