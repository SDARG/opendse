package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CRTest {

	@Test(expected=IllegalArgumentException.class)
	public void testTaskInput() {
		new CR(new Task("task"), new Resource("resource"));
	}
	
	@Test
	public void test() {
		Communication task = new Communication("comm");
		Resource res = new Resource("resource");
		CR crVar = new CR(task, res);
		assertEquals(res, crVar.getResource());
		assertEquals(task, crVar.getCommunication());
		assertEquals(crVar, new CR(task, res));
	}
}
