package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class DMTest {

	@Test
	public void test() {
		Dependency dep = new Dependency("dep");
		Task t = new Task("t");
		Resource res = new Resource("res");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m0", t, res);
		DM dm = new DM(dep, mapping);
		assertEquals(mapping, dm.getMapping());
		assertEquals(dep, dm.getDependency());
		assertEquals(dm, new DM(dep, mapping));
	}
}
