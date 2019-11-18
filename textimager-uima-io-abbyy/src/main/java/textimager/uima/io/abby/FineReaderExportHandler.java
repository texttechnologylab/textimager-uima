package textimager.uima.io.abby;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import textimager.uima.io.abby.annotation.*;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class FineReaderExportHandler extends DefaultHandler {
	// Pages
	public ArrayList<Page> pages = new ArrayList<>();
	private Page currPage = null;
	
	// Block
	public ArrayList<Block> blocks = new ArrayList<>();
	private Block currBlock = null;
	
	// Paragraphs
	public ArrayList<Paragraph> paragraphs = new ArrayList<>();
	private Paragraph currParagraph = null;
	
	// Lines
	public ArrayList<Line> lines = new ArrayList<>();
	private Line currLine = null;
	
	// Token
	public ArrayList<Token> tokens = new ArrayList<>();
	private Token currToken = null;
	public int blockTopMin = 0;
	public int charLeftMax = Integer.MAX_VALUE;
	
	// Switches
	private boolean character = false;
	private boolean characterIsAllowed = false;
	private boolean forceNewToken = false;
	public boolean lastTokenWasSpace = false;
	private boolean lastTokenWasHyphen = false;
	private boolean inLine = false;
	
	// Formatting
//	private String currLang = null;
//	private String currFont = null;
//	private String currFontSize = null;
	
	private Attributes currCharAttributes = null;
	
	// Statistics
	private int totalChars = 0;
	
	private static Pattern spacePattern = Pattern.compile("[\\s]+", Pattern.UNICODE_CHARACTER_CLASS);
	private static Pattern nonWordCharacter = Pattern.compile("[^\\p{Alnum}\\-¬]+", Pattern.UNICODE_CHARACTER_CLASS);
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case "page":
				currPage = new Page(attributes);
				currPage.start = totalChars;
				pages.add(currPage);
				break;
			case "block":
				currBlock = new Block(attributes);
				currBlock.start = totalChars;
				currBlock.valid = blockObeysRules(currBlock);
				blocks.add(currBlock);
				
				inLine = false;
				
				character = false;
				break;
			case "text":
				break;
			case "par":
				currParagraph = new Paragraph(attributes);
				currParagraph.start = totalChars;
				paragraphs.add(currParagraph);
				break;
			case "line":
				currLine = new Line(attributes);
				currLine.start = totalChars;
				lines.add(currLine);
				
				inLine = true;
				break;
			case "formatting":
				if (currLine != null)
					currLine.OCRFormat = new Format(attributes);
//				String attr = attributes.getValue("lang");
//				currLang = Strings.isNullOrEmpty(attr) ? null : attr;
//
//				attr = attributes.getValue("ff");
//				currFont = Strings.isNullOrEmpty(attr) ? null : attr;
//
//				attr = attributes.getValue("fs");
//				currFontSize = Strings.isNullOrEmpty(attr) ? null : attr;
				break;
			case "charParams":
				String wordStart = attributes.getValue("wordStart");
				
				if (currToken == null || (((wordStart != null && wordStart.equals("true")) || forceNewToken) && !lastTokenWasHyphen)) {
					addToken();
				}
				
				currCharAttributes = attributes; // TODO: use char obj
				character = true; // TODO: use char obj
				
				Char ocrChar = new Char(attributes);
				characterIsAllowed = charObeysRules(ocrChar);
				break;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case "page":
				setEnd(currPage);
				break;
			case "block":
				addSpace();
				setEnd(currBlock);
				break;
			case "text":
				addSpace();
				setEnd(currParagraph);
				setEnd(currLine);
				setEnd(currToken);
				break;
			case "par":
				addSpace();
				setEnd(currParagraph);
				break;
			case "line":
				// Add space instead of linebreak
				addSpace();
				setEnd(currLine);
		}
		character = false;
	}
	
	private void setEnd(Annotation Annotation) {
		if (Annotation != null)
			Annotation.end = totalChars;
	}
	
	/**
	 * @param ch
	 * @param start
	 * @param length should be one by XML schema definition, but isn't always.
	 * @throws SAXException
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (character && characterIsAllowed) {
			String currChars = new String(ch, start, length);
			if (spacePattern.matcher(currChars).matches()) {
				addSpace();
			} else if (nonWordCharacter.matcher(currChars).matches()) {
				// If the current characters are non-token characters
				// add a new token and reset last token information
				addNonWordToken(currChars, currCharAttributes);
			} else {
				// Add a new subtoken if there has been a ¬ and it was followed by a character
				if (lastTokenWasHyphen && !lastTokenWasSpace) {
					currToken.setContainsHyphen();
					currToken.addSubToken();
				}
				lastTokenWasSpace = false;
				
				// The hyphen character ¬ does not contribute to the total character count
				if (currChars.equals("¬")) {
					lastTokenWasHyphen = true;
				} else {
					lastTokenWasHyphen = false;
					currToken.addChar(currChars);
					currToken.addCharAttributes(currCharAttributes);
					totalChars += currChars.length();
				}
			}
			character = false;
		}
	}
	
	private void addNonWordToken(String currChars, Attributes currCharAttributes) {
		// If the current token already contains characters, create a new token for the non-word token
		if (currToken.length() > 0) {
			forceNewToken = true;
			addToken();
		}
		
		lastTokenWasSpace = false;
		lastTokenWasHyphen = false;
		
		currToken.addChar(currChars);
		currToken.addCharAttributes(currCharAttributes);
		totalChars += currChars.length();
		
		forceNewToken = true;
		addToken();
	}
	
	private void addSpace() {
		// Do not add spaces if the preceding token is a space, the ¬ hyphenation character or there has not been any token
		if (lastTokenWasSpace || lastTokenWasHyphen || currToken == null)
			return;
		
		// If the current token already contains characters, create a new token for the space
		if (currToken.length() > 0) {
			forceNewToken = true;
			addToken();
		}
		
		// Add the space character and increase token count
		currToken.addChar(" ");
		totalChars++;
		forceNewToken = true;
		lastTokenWasSpace = true;
	}
	
	private void addToken() {
		if (currToken == null) {
			createNewToken();
		} else if (forceNewToken || currToken.isSpace()) {
			currToken.end = totalChars;
			createNewToken();
		} else {
			currToken.addSubToken();
		}
	}
	
	private void createNewToken() {
		currToken = new Token();
		currToken.start = totalChars;
		tokens.add(currToken);
		
		forceNewToken = false;
	}
	
	/**
	 * Check if the current Block obeys the rules given for this type of article.
	 * TODO: dynamic rules from file
	 *
	 * @param OCRBlock BIOfid.OCR.Annotation.Block
	 * @return boolean True if the current Block is not null and obeys all rules.
	 */
	private boolean blockObeysRules(Block OCRBlock) {
		return OCRBlock != null && OCRBlock.blockType == Block.blockTypeEnum.Text && OCRBlock.top >= blockTopMin;
	}
	
	private boolean charObeysRules(Char ocrChar) {
		return ocrChar != null && ocrChar.left <= charLeftMax;
	}
}
