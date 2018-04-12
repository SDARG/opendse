package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CRRTest {

	@Test
	public void test() {
		Task comm = new Task("comm");
		Resource first = new Resource("first");
		Resource second = new Resource("second");
		CRR crr = new CRR(comm, first, second);
		assertEquals(comm, crr.getComm());
		assertEquals(first, crr.getFirst());
		assertEquals(second, crr.getSecond());
	}
}
