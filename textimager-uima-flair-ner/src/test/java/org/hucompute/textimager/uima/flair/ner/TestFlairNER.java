package org.hucompute.textimager.uima.flair.ner;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hucompute.textimager.uima.flair.FlairNER;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class TestFlairNER {

	@BeforeClass
	public static void setUpClass() throws ResourceInitializationException {
	}

	@Test
	public void test() throws UIMAException, IOException {
		String text = "Paciente de 35 años que nos fue remitido al servicio de urgencias de nuestro hospital por urólogo de zona ante hallazgo ecográfico compatible con neoplasia de testículo.\n"
				+ "Destacaban entre sus antecedentes personales: criptorquidia bilateral que fue tratada con orquidopexia, a los 6 y 8 años respectivamente, en otro centro.\n"
				+ "Uretrotomía endoscópica a los 20 años.\n"
				+ "Había presentado cólicos nefríticos de repetición con expulsión de litiasis e incluso había precisado tratamiento con LEOC.\n"
				+ "Ureterolitectomía a los 28 años de edad.\n" + "Fumador de 1 paquete de cigarrillos/día.\n"
				+ "Un mes antes, consultó al urólogo de zona por esterilidad y nódulo en testículo izquierdo de meses de evolución que había aumentado de tamaño en las últimas semanas.\n"
				+ "El espermiograma presentaba una azoospermia total.\n"
				+ "La ecografía-doppler testicular fue informada como testículo derecho con masa de 19 x 23 mm en polo superior con flujo aumentado, compatible con tumor testicular y quiste de cordón izquierdo.\n"
				+ "En la exploración física se palpaba un testículo izquierdo sin alteraciones, con quiste de epidídimo en cabeza.\n"
				+ "El testículo derecho no se pudo explorar puesto que se encontraba en el canal inguinal.\n"
				+ "Se procedió a completar el estudio.\n"
				+ "La Rx de tórax era normal y en la TAC toraco-abdómino-pélvico no se observaban metástasis.\n"
				+ "La analítica tampoco mostraba alteraciones, tanto en los valores séricos hormonales como los marcadores tumorales (alfa-fetoproteina 2,2 ng/ml, beta-HCG 0,0 ng/ml).\n"
				+ "Se llevó a cabo una orquiectomía radical inguinal derecha y biopsia del testículo izquierdo vía inguinal.\n"
				+ "El informe macroscópico de la pieza nos fue informada como: testículo derecho de 3,2 x 3 cm.\n"
				+ "La superficie testicular es lisa sin observarse infiltración de túnica vaginalis.\n"
				+ "Al corte, se identifica un nódulo sólido bien delimitado de 1,5 cm de diámetro de coloración parda con área amarillenta central.\n"
				+ "El tejido testicular restante es de color anaranjado.\n"
				+ "El cordón espermático no presenta alteraciones.\n"
				+ "La biopsia del testículo izquierdo es un fragmento de 0,3 cm de coloración parda.\n"
				+ "El informe histopatológico refería: Tumor de células de Leydig de 3,2 x 3 cm sin objetivarse infiltración del epidídimo, túnica albugínea ni invasión vascular.\n"
				+ "Resto de parénquima testicular con atrofia, hiperplasia de células de Leydig en el intersticio y práctica ausencia de espermiogénesis.\n"
				+ "Bordes quirúrgicos libres.\n"
				+ "La biopsia del teste izquierdo fue informada como atrofia testicular con ausencia de espermiogénesis e hiperplasia de células de Leydig intersticial.\n"
				+ "A los 30 meses de controles evolutivos, el paciente presenta un buen estado general sin evidencia radiológica de metástasis y marcadores tumorales dentro de los valores de la normalidad.\n";
		JCas jCas = JCasFactory.createText(text);

		int last_index = 0;
		int index = text.indexOf("\n");
		while (index > 0) {
			Sentence sentence = new Sentence(jCas, last_index, index);
			jCas.addFsToIndexes(sentence);

			last_index = index + 1;
			index = text.indexOf("\n", last_index);
		}

		String home = System.getenv("HOME");
		String model_location = home + "/.textimager/models/PharmaCoNER-PCSE_mean-BPEmb-TF-w2v.pt";
		if (!Paths.get(model_location).toFile().exists()) {
			Files.copy(Paths.get(
					"/resources/public/stoeckel/projects/EsPharmaNER-REST/models/PharmaCoNER-PCSE_mean-BPEmb-TF-w2v.pt")
					.toFile(), Paths.get(model_location).toFile());
		}
		AnalysisEngine engine = AnalysisEngineFactory.createEngine(FlairNER.class, FlairNER.PARAM_LANGUAGE, "es",
				FlairNER.PARAM_MODEL_LOCATION, model_location);

		SimplePipeline.runPipeline(jCas, engine);
		JCasUtil.select(jCas, NamedEntity.class).forEach(ner -> {
			System.out.println(ner.getCoveredText() + ": " + ner);
		});
		assert JCasUtil.select(jCas, NamedEntity.class).size() > 0;
	}
}
