package org.hucompute.textimager.uima.turkish_deasciifier;

import static org.junit.Assert.assertEquals;

import org.hucompute.textimager.uima.turkish_deasciifier.TurkishDeasciifier;
import org.junit.Test;

/**
* DeasciifierTest
*
* @date 10.07.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Deasciifier Test. Test if the stem is generated correctly.
*
*/
public class TurkishDeasciifierTest {
	
	/**
	 * Test with JUnit if the stem is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Use Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		deasciifier.setAsciiString("Hadi bir masal uyduralim, icinde mutlu, doygun, telassiz durdugumuz.");
		// Test with JUnit
		assertEquals(deasciifier.convertToTurkish(), "Hadi bir masal uyduralım, içinde mutlu, doygun, telaşsız durduğumuz.");
	}
	
}
