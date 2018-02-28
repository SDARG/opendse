package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CRTest {

	@Test
	public void test() {
		Task comm = new Communication("comm");
		Resource res = new Resource("res");
		CR cr = new CR(comm, res);
		assertEquals(comm, cr.getC());
		assertEquals(comm, cr.getCommunication());
		assertEquals(res, cr.getR());
		CR cr2 = new CR(comm, res);
		assertEquals(cr, cr2);
		assertNotEquals(cr, res);
		assertEquals(cr.hashCode(), cr2.hashCode());
	}
}
