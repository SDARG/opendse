package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationAlternativeActivationTest {

	@Test
	public void test() {
		ApplicationAlternativeActivation aaa1 = new ApplicationAlternativeActivation("alternative", "1");
		assertEquals("alternative", aaa1.getAlternativeName());
		assertEquals("1", aaa1.getAlternativeId());
		
		ApplicationAlternativeActivation aaa2 = new ApplicationAlternativeActivation("alternative", "1");
		assertEquals(aaa1, aaa2);
	}

}
