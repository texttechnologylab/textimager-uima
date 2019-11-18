package textimager.uima.io.abby.annotation;

import org.apache.uima.jcas.JCas;
import org.texttechnologylab.annotation.ocr.OCRBlock;
import org.xml.sax.Attributes;

public class Block extends StructuralElement {
	
	public blockTypeEnum blockType; //  FIXME
	public final String blockName;
	public boolean valid;
	
	public enum blockTypeEnum {
		Text, Table, Picture, Barcode, Separator, SeparatorsBox, INVALID
	}
	
	public Block(int top, int bottom, int left, int right, String blockType, String blockName) {
		super(top, bottom, left, right);
		this.blockType = blockTypeEnum.valueOf(blockType);
		this.blockName = blockName;
	}
	
	public Block(Attributes attributes) {
		super(attributes);
		try {
			this.blockType = blockTypeEnum.valueOf(attributes.getValue("blockType"));
		} catch (IllegalArgumentException e) {
			//FIXME: THE WORST KIND OF PRACTICE
			this.blockType = blockTypeEnum.INVALID;
			System.err.printf("Unknown block type: %s!\n", attributes.getValue("blockType"));
		}
		this.blockName = attributes.getValue("blockName");
	}
	
	@Override
	public OCRBlock wrap(JCas jCas, int offset) {
		OCRBlock ocrBlock = new OCRBlock(jCas, start + offset, end + offset);
		ocrBlock.setTop(top);
		ocrBlock.setBottom(bottom);
		ocrBlock.setLeft(left);
		ocrBlock.setRight(right);
		ocrBlock.setBlockType(blockType.toString());
		ocrBlock.setBlockName(blockName);
		ocrBlock.setValid(valid);
		return ocrBlock;
	}
}
