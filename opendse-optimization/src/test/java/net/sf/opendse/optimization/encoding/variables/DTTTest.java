package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

public class DTTTest {

	@Test
	public void test() {
		Dependency dep = new Dependency("dep");
		Task src = new Task("src");
		Task dest = new Task("dest");

		DTT var = new DTT(dep, src, dest);
		assertTrue(var.getDependency() instanceof Dependency);
		assertTrue(var.getSourceTask() instanceof Task);
		assertTrue(var.getDestTask() instanceof Task);

		assertEquals(dep, var.getDependency());
		assertEquals(src, var.getSourceTask());
		assertEquals(dest, var.getDestTask());
	}

}
