import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.texttechnologylab.annotation.ocr.OCRBlock;
import org.texttechnologylab.annotation.ocr.OCRPage;
import org.texttechnologylab.utilities.collections.CountMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 18.11.19.
 */
public class JCas2JSONConsumer extends JCasConsumer_ImplBase {
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		System.out.print("{\"JCas2JSON\": [");
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			CountMap<String> countMap = new CountMap<>();
			JCasUtil.selectAll(aJCas).forEach(top -> countMap.inc(top.getType().getShortName()));
			System.out.flush();
			System.out.printf("{\"id\": \"%s\",\"length\": %d,\"typeCount\": %s,\"pages\": [",
					DocumentMetaData.get(aJCas).getDocumentId(), aJCas.getDocumentText().length(), objectMapper.writeValueAsString(countMap));
			Map<OCRPage, Collection<OCRBlock>> ocrPageCollectionMap = JCasUtil.indexCovered(aJCas, OCRPage.class, OCRBlock.class);
			for (Iterator<OCRPage> iter = JCasUtil.select(aJCas, OCRPage.class).iterator(); iter.hasNext(); ) {
				OCRPage ocrPage = iter.next();
				System.out.printf("{\"id\": \"%s\",\"number\": %d,\"blocks\": [", ocrPage.getPageId(), ocrPage.getPageNumber());
				for (Iterator<OCRBlock> iterator = ocrPageCollectionMap.get(ocrPage).iterator(); iterator.hasNext(); ) {
					OCRBlock ocrBlock = iterator.next();
					System.out.printf("{\"blockType\": \"%s\",\"isValid\": \"%b\",\"begin\": %d,\"end\": %d,\"l\": %d,\"t\": %d,\"r\": %d,\"b\": %d,",
							ocrBlock.getBlockType(), ocrBlock.getValid(), ocrBlock.getBegin(), ocrBlock.getEnd(),
							ocrBlock.getTop(), ocrBlock.getLeft(), ocrBlock.getBottom(), ocrBlock.getRight());
					System.out.print("\"text\": " + objectMapper.writeValueAsString(ocrBlock.getCoveredText()) + "}");
					if (iterator.hasNext())
						System.out.print(",");
				}
				System.out.print("]}");
				if (iter.hasNext())
					System.out.print(",");
			}
			System.out.print("]},");
		} catch (JsonProcessingException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
	
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		System.out.println("\b]}");
	}
}
