import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.texttechnologylab.annotation.ocr.OCRBlock;
import org.texttechnologylab.annotation.ocr.OCRPage;
import org.texttechnologylab.utilities.collections.CountMap;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 18.11.19.
 */
public class JCas2JSONWriter extends JCasConsumer_ImplBase {
	
	public static final String PARAM_TARGET_LOCATION = ComponentParameters.PARAM_TARGET_LOCATION;
	@ConfigurationParameter(name = PARAM_TARGET_LOCATION, mandatory = false)
	private String targetLocation;
	
	PrintStream printStream = System.out;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		if (targetLocation != null && !targetLocation.equals("")) {
			try {
				printStream = new PrintStream(targetLocation);
			} catch (FileNotFoundException e) {
				throw new ResourceInitializationException(e);
			}
		}
		printStream.print("{\"JCas2JSON\": [");
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			CountMap<String> countMap = new CountMap<>();
			JCasUtil.selectAll(aJCas).forEach(top -> countMap.inc(top.getType().getShortName()));
			printStream.printf("\n{\"id\": \"%s\",\"length\": %d,\"typeCount\": %s,\"pages\": [",
					DocumentMetaData.get(aJCas).getDocumentId(), aJCas.getDocumentText().length(), objectMapper.writeValueAsString(countMap));
			Map<OCRPage, Collection<OCRBlock>> ocrPageCollectionMap = JCasUtil.indexCovered(aJCas, OCRPage.class, OCRBlock.class);
			for (Iterator<OCRPage> iter = JCasUtil.select(aJCas, OCRPage.class).iterator(); iter.hasNext(); ) {
				OCRPage ocrPage = iter.next();
				printStream.printf("{\"id\": \"%s\",\"number\": %d,\"blocks\": [", ocrPage.getPageId(), ocrPage.getPageNumber());
				for (Iterator<OCRBlock> iterator = ocrPageCollectionMap.get(ocrPage).iterator(); iterator.hasNext(); ) {
					OCRBlock ocrBlock = iterator.next();
					printStream.printf("{\"blockType\": \"%s\",\"isValid\": \"%b\",\"begin\": %d,\"end\": %d,\"l\": %d,\"t\": %d,\"r\": %d,\"b\": %d,",
							ocrBlock.getBlockType(), ocrBlock.getValid(), ocrBlock.getBegin(), ocrBlock.getEnd(),
							ocrBlock.getTop(), ocrBlock.getLeft(), ocrBlock.getBottom(), ocrBlock.getRight());
					printStream.print("\"text\": " + objectMapper.writeValueAsString(ocrBlock.getCoveredText()) + "}");
					if (iterator.hasNext())
						printStream.print(",");
				}
				printStream.print("]}");
				if (iter.hasNext())
					printStream.print(",");
			}
			printStream.print("]},");
		} catch (JsonProcessingException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
	
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		printStream.println("{}]}");
		if (printStream != System.out)
			printStream.close();
	}
}
