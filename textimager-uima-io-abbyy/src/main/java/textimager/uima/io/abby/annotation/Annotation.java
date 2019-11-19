package textimager.uima.io.abby.annotation;

import org.apache.uima.jcas.JCas;

public abstract class Annotation {
	
	public int start = 0;
	public int end = 0;
	
	public void setStartEnd(int pStart, int pEnd) {
		this.start = pStart;
		this.end = pEnd;
	}
	
	public org.apache.uima.jcas.tcas.Annotation wrap(JCas jCas) {
		return wrap(jCas, 0);
	}
	
	public org.apache.uima.jcas.tcas.Annotation wrap(JCas jCas, int offset) {
		return new org.apache.uima.jcas.tcas.Annotation(jCas, start + offset, end + offset);
	}
	
}