package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationAlternativeTest {

	@Test
	public void test() {
		ApplicationAlternative alt = new ApplicationAlternative("function", "a");
		assertEquals("function", alt.getFunctionName());
		assertEquals("a", alt.getFunctionId());
	}
}
