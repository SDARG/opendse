package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

public class DTTTest {

	@Test(expected=IllegalArgumentException.class)
	public void testWrongInput() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Task destinationTask = new Task("destination");
		new DTT(dependency, sourceTask, destinationTask);
	}
	
	@Test
	public void test() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Communication destinationTask = new Communication("destination");
		DTT dttVar = new DTT(dependency, sourceTask, destinationTask);
		assertEquals(dependency, dttVar.getDependency());
		assertEquals(sourceTask, dttVar.getSourceTask());
		assertEquals(destinationTask, dttVar.getDestinationTask());
		assertEquals(dttVar, new DTT(dependency, sourceTask, destinationTask));
	}
	
	@Test
	public void testToString() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Communication destinationTask = new Communication("destination");
		DTT dttVar = new DTT(dependency, sourceTask, destinationTask);
		assertEquals("DTT[dependency,source,destination]", dttVar.toString());
	}
}
