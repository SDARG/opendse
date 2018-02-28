package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class MTest {

	@Test
	public void test() {
		Task t = new Task("t");
		Resource res = new Resource("res");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, res);
		M var = new M(m);
		assertEquals(m, var.getMapping());
	}

}
