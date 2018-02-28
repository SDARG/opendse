package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Link;

public class LTest {

	@Test
	public void test() {
		Link l = new Link("l");
		L lVar = new L(l);
		assertEquals(l, lVar.getLink());
	}

}
