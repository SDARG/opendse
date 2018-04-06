package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class DDMTest {

	@Test
	public void test() {
		Dependency dep1 = new Dependency("d1");
		Dependency dep2 = new Dependency("d2");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", new Task("t"), new Resource("res"));

		DDM var = new DDM(dep1, dep2, m);
		assertEquals(dep1, var.getSrcDependency());
		assertEquals(dep2, var.getDestDependency());
		assertEquals(m, var.getMapping());
		assertEquals(var, new DDM(dep1, dep2, m));
	}
}
