package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Resource;

public class RTest {

	@Test
	public void test() {
		Resource res = new Resource("res");
		R rVar = new R(res);
		assertEquals(res, rVar.getResource());
		assertEquals(rVar, new R(res));
	}
}
