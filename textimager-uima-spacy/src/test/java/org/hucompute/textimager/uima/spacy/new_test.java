package org.hucompute.textimager.uima.spacy;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.*;


import java.io.FileWriter;
import java.io.IOException;

public class new_test {
    private static FileWriter file;

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws UIMAException {

        String bsp_text = "Dies ist ein Beispielsatz und er ist lang.";
        String lang = "de";

        JCas test_cas = JCasFactory.createText(bsp_text, lang);

        JSONObject input_obj = new JSONObject();
        input_obj.put("text", test_cas.getDocumentText());
        input_obj.put("lang", test_cas.getDocumentLanguage());

        try{
            file = new FileWriter("textimager-uima-spacy/jcasson.json");
            file.write(input_obj.toString());
        } catch (IOException e){
            e.printStackTrace();

        } finally {
            try{
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /* Copied from collect method */

        try {
            URL url = new URL("http://127.0.0.1:8000");
            InputStream is = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            String[] json_cut = jsonText.split("]");


            for (int i = 0; i < json_cut.length; i++){
                if (i == 0){
                    json_cut[i] = strip(json_cut[i], "[]");
                    json_cut[i] = "["+ json_cut[i] + "]";
                }
                else{
                    json_cut[i] = json_cut[i].substring(1);
                    json_cut[i] = strip(json_cut[i], "[]");
                    json_cut[i] = "["+ json_cut[i] + "]";

                }
                //System.out.println("Ohne Strip " + json_cut[i]);
                //System.out.println("Mit Strip " + json_cut[i]);
                System.out.println("Endprodukt " + json_cut[i]);
            }

            //System.out.print(Arrays.toString(json_cut));
            //JSONObject json = new JSONObject(jsonText);
            System.out.println("erstmal fertig");

            /*
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            System.out.print(out);
            out.flush();
            out.close();
             */
        }
        catch (IOException e) {
            e.printStackTrace();
        }






    }

    public static void collect() {


        try {
            URL url = new URL("http://127.0.0.1:8000");
            InputStream is = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            String[] json_cut = jsonText.split("]");


            for (int i = 0; i < json_cut.length; i++){
                if (i == 0){
                    json_cut[i] = strip(json_cut[i], "[]");
                    json_cut[i] = "["+ json_cut[i] + "]";
                }
                else{
                    json_cut[i] = json_cut[i].substring(1);
                    json_cut[i] = strip(json_cut[i], "[]");
                    json_cut[i] = "["+ json_cut[i] + "]";

                }
                //System.out.println("Ohne Strip " + json_cut[i]);
                //System.out.println("Mit Strip " + json_cut[i]);
                System.out.println("Endprodukt " + json_cut[i]);
            }

            //System.out.print(Arrays.toString(json_cut));
            //JSONObject json = new JSONObject(jsonText);
            System.out.println("erstmal fertig");

            /*
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            System.out.print(out);
            out.flush();
            out.close();
             */
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }
}

