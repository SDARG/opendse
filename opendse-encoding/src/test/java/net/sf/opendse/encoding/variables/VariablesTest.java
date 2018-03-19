package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

public class VariablesTest {

	@Test
	public void testDTTvar() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Task destinationTask = new Task("destination");
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
