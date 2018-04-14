package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class ColoredCommNodeTest {

	@Test
	public void test() {
		Task comm = new Task("comm");
		Resource res = new Resource("res");
		String color = "black";
		ColoredCommNode ccn = new ColoredCommNode(comm, res, color);
		assertEquals(comm, ccn.getCommunication());
		assertEquals(res, ccn.getResource());
		assertEquals(color, ccn.getColor());
	}
}
