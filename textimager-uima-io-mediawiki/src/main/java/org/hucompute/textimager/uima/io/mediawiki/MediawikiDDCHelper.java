package org.hucompute.textimager.uima.io.mediawiki;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONTokener;

public class MediawikiDDCHelper {

	private static HashMap<String, String> DDC_CLASSES;
	static {
		DDC_CLASSES = new HashMap<String, String>();
		try {
			JSONTokener tokener = new JSONTokener(MediawikiDDCHelper.class.getClassLoader().getResource("ddc3_map.json").openStream());
	        JSONObject ddc_map = new JSONObject(tokener);
	        Iterator<?> keys = ddc_map.keys();
	        while(keys.hasNext()) {
	            String key = (String) keys.next();
	            String name = ddc_map.getString(key);
	            DDC_CLASSES.put(key, name);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> getAllDDCClasses() {
		return DDC_CLASSES;
	}

	public static String getDDCClassName(String id) {
		String idFixed = id.replaceAll("ddc", "");
		if (DDC_CLASSES.containsKey(idFixed))
			return DDC_CLASSES.get(idFixed);

		return "[UNKNOWN_ID]";
	}
	
	public static int getDDCLevel(String id) {
		if (id == null) return -1;
		return id.substring(2, 3).equals("0") ? (id.substring(1, 2).equals("0") ? 1 : 2) : 3;
	}
}
