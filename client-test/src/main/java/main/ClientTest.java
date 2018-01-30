package main;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.hucompute.services.client.TextImagerClient;
import org.hucompute.services.util.XmlFormatter;

public class ClientTest {

	public static void main(String[] args) throws UIMAException, IOException {
		TextImagerClient client = new TextImagerClient();
//		//Remote
//		client.setConfigFile("src/main/resources/service_remote_properties.xml");
//		client.setServer("tcp://alba.hucompute.org:61617");
//		
		//Local
		client.setConfigFile("src/main/resources/service_properties.xml");
		client.setServer("tcp://localhost:61617");

		client.addOption("CountAnnotator", "de");

		
		CAS output = client.process("This is a test.");
		System.out.println(XmlFormatter.getPrettyString(output));

	}

}
