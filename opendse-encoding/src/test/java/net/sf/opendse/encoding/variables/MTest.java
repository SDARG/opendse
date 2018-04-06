package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class MTest {

	@Test
	public void test() {
		Task task = new Task("task");
		Resource res = new Resource("resource");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("mapping", task, res);
		M mVar = new M(mapping);
		assertEquals(mapping, mVar.getMapping());
		assertEquals(mVar, new M(mapping));
	}

}
