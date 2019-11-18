package textimager.uima.io.abby.annotation;

import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.texttechnologylab.annotation.ocr.OCRToken;
import org.xml.sax.Attributes;
import textimager.uima.io.abby.utility.Util;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Token extends Annotation {
	
	private ArrayList<ArrayList<String>> subTokenList;
	private ArrayList<String> charList;
	private ArrayList<Attributes> charAttributeList = new ArrayList<>(); // TODO: adjust to List<List> token structure
	
	public boolean isWordFromDictionary = false;
	public boolean isWordNormal = false;
	public boolean isWordNumeric = false;
	
	public int suspiciousChars = 0;
	
	private boolean containsHyphen = false;
	
	public Token() {
		charList = new ArrayList<>();
		
		subTokenList = new ArrayList<>();
		subTokenList.add(charList);
	}
	
	public void addSubToken() {
		charList = new ArrayList<>();
		subTokenList.add(charList);
	}
	
	public ArrayList<String> subTokenStrings() {
		return subTokenList.stream().map(cl -> String.join("", cl)).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public void addChar(String pChar) {
		charList.add(pChar);
	}
	
	public void addChar(int pCharPos, String pChar) {
		charList.add(pCharPos, pChar);
	}
	
	public void removeLastChar() {
		if (!charList.isEmpty())
			charList.remove(charList.size() - 1);
	}
	
	public void addCharAttributes(Attributes pCharAttributes) {
		charAttributeList.add(pCharAttributes);
		this.processAttributes(pCharAttributes);
	}
	
	public void addCharAttributes(int pCharPos, Attributes pCharAttributes) {
		charAttributeList.add(pCharPos, pCharAttributes);
		this.processAttributes(pCharAttributes);
	}
	
	public String getTokenString() {
		return subTokenList.stream().map(cl -> String.join("", cl)).collect(Collectors.joining(""));
	}
	
	public double getAverageCharConfidence() {
		Mean mean = new Mean();
		for (Attributes attributes : charAttributeList) {
			String charConfidence = attributes.getValue("charConfidence");
			if (charConfidence != null)
				mean.increment(Double.parseDouble(charConfidence));
		}
		return mean.getResult();
	}
	
	private void processAttributes(Attributes pAttributes) {
		if (Util.parseBoolean(pAttributes.getValue("suspicious")))
			suspiciousChars++;
		
		if (Util.parseBoolean(pAttributes.getValue("wordFromDictionary")))
			isWordFromDictionary = true;
		
		if (Util.parseBoolean(pAttributes.getValue("wordNormal")))
			isWordNormal = true;
		
		if (Util.parseBoolean(pAttributes.getValue("wordNumeric")))
			isWordNumeric = true;
	}
	
	public int length() {
		return charList.size();
	}
	
	public boolean isSpace() {
		return charList != null && charList.stream().allMatch(s -> s.matches("\\s*"));
	}
	
	public void setContainsHyphen() {
		this.containsHyphen = true;
	}
	
	public boolean containsHyphen() {
		return containsHyphen;
	}
	
	@Override
	public OCRToken wrap(JCas jCas, int offset) {
		OCRToken ocrToken = new OCRToken(jCas, start + offset, end + offset);
		StringList stringList = new StringList(jCas);
		Lists.reverse(subTokenList).stream().map(s -> String.join("", s)).forEach(stringList::push);
		ocrToken.setSubTokenList(stringList);
		ocrToken.setIsWordFromDictionary(isWordFromDictionary);
		ocrToken.setIsWordNormal(isWordNormal);
		ocrToken.setIsWordNumeric(isWordNumeric);
		ocrToken.setSuspiciousChars(suspiciousChars);
		ocrToken.setContainsHyphen(containsHyphen);
		return ocrToken;
	}
	
	public ArrayList<OCRToken> wrapSubtokens(JCas jCas, int offset) {
		ArrayList<OCRToken> subTokens = new ArrayList<>();
		if (subTokenList.size() > 1) {
			int localOffset = 0;
			for (ArrayList<String> subToken : subTokenList) {
				int subTokenLength = subToken.size();
				OCRToken ocrToken = new OCRToken(jCas, start + offset + localOffset, start + offset + localOffset + subTokenLength);
				StringList stringList = new StringList(jCas);
				Lists.reverse(subToken).forEach(stringList::push);
				ocrToken.setSubTokenList(stringList);
				ocrToken.setIsWordFromDictionary(isWordFromDictionary);
				ocrToken.setIsWordNormal(isWordNormal);
				ocrToken.setIsWordNumeric(isWordNumeric);
				ocrToken.setSuspiciousChars(suspiciousChars);
				ocrToken.setContainsHyphen(containsHyphen);
				subTokens.add(ocrToken);
				localOffset += subTokenLength;
			}
		}
		return subTokens;
	}
}
