import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.texttechnologylab.utilities.collections.CountMap;

/**
 * Created on 18.11.19.
 */
public class DummyCasConsumer extends JCasConsumer_ImplBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.printf("Document length: %d\n", aJCas.getDocumentText().length());
		CountMap<String> countMap = new CountMap<>();
		JCasUtil.selectAll(aJCas).forEach(top -> countMap.inc(top.getType().toString()));
		System.out.println(countMap);
		System.out.println(aJCas.getDocumentText());
	}
}
