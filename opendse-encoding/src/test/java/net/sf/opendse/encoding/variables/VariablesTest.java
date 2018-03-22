package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class VariablesTest {

	@Test
	public void testCLRRvar() {
		Communication comm = new Communication("comm");
		Link link = new Link("link");
		Resource source = new Resource("source");
		Resource destination = new Resource("destination");
		CLRR clrrVar = Variables.var(comm, link, source, destination);
		assertEquals(clrrVar, Variables.var(comm, link, source, destination));
	}
	
	@Test
	public void testCRvar() {
		Communication task = new Communication("comm");
		Resource res = new Resource("resource");
		CR crVar = Variables.var(task, res);
		assertEquals(crVar, Variables.var(task, res));
	}
	
	@Test
	public void testMvar() {
		Task task = new Task("task");
		Resource res = new Resource("resource");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("mapping", task, res);
		M mVar = Variables.var(mapping);
		assertEquals(mapping, mVar.getMapping());
		assertEquals(mVar, Variables.var(mapping));
	}

	@Test
	public void testDTTvar() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Communication destinationTask = new Communication("destination");
		DTT dttVar = Variables.var(dependency, sourceTask, destinationTask);
		assertEquals(dttVar, Variables.var(dependency, sourceTask, destinationTask));
	}

	@Test
	public void testTvar() {
		Task task = new Task("task");
		T tVar = Variables.var(task);
		assertEquals(task, tVar.getTask());
		assertEquals(tVar, Variables.var(task));
	}

	@Test
	public void testP() {
		Variable mockVar = mock(Variable.class);
		Literal pLiteral = Variables.p(mockVar);
		assertTrue(pLiteral.phase());
		assertEquals(mockVar, pLiteral.variable());
		assertEquals(pLiteral, Variables.p(mockVar));
	}

	@Test
	public void testN() {
		Variable mockVar = mock(Variable.class);
		Literal nLiteral = Variables.n(mockVar);
		assertFalse(nLiteral.phase());
		assertEquals(mockVar, nLiteral.variable());
		assertEquals(nLiteral, Variables.n(mockVar));
	}
}
