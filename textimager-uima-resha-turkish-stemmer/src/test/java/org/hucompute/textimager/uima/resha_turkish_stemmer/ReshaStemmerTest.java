package org.hucompute.textimager.uima.resha_turkish_stemmer;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hrzafer.reshaturkishstemmer.Resha;

/**
* ReshaStemmerTest
*
* @date 10.07.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Stemmer Test. Test if the stem is generated correctly.
*
*/
public class ReshaStemmerTest {
	
	/**
	 * Test with JUnit if the stem is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Use Resha-Turkish-Stemmer
		com.hrzafer.reshaturkishstemmer.Stemmer stemmer = Resha.Instance;
		
		String stem = stemmer.stem("kitapçıdaki");
		assertEquals(stem, "kitapçı");
		
		stem = stemmer.stem("İstanbul'da");
		assertEquals(stem, "İstanbul");

		//If a word is not in the dictionary it remains unstemmed.
		stem = stemmer.stem("xxx");
		assertEquals(stem, "xxx");
	}
	
}
