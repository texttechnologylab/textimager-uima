package org.hucompute.textimager.uima.resha_turkish_stemmer;

import com.hrzafer.reshaturkishstemmer.Resha;
import com.hrzafer.reshaturkishstemmer.Stemmer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
* ReshaStemmerTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.1
*
* This class provide several test cases for turkish language.
*/
public class ReshaStemmerTest {

	/**
	 * Test with JUnit if the stem is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		Stemmer stemmer = Resha.Instance;

		String stem = stemmer.stem("kitapçıdaki");
		assertEquals(stem, "kitapçı");

		stem = stemmer.stem("İstanbul'da");
		assertEquals(stem, "İstanbul");

		//If a word is not in the dictionary it remains unstemmed.
		stem = stemmer.stem("xxx");
		assertEquals(stem, "xxx");
	}

}
