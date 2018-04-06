package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

import static org.mockito.Mockito.mock;

public class VariablesTest {

	@Test
	public void testLvar() {
		Link link = new Link("link");
		L lVar = Variables.varL(link);
		assertEquals(lVar, Variables.varL(link));
	}

	@Test
	public void testRvar() {
		Resource res = new Resource("res");
		R rVar = Variables.varR(res);
		assertEquals(rVar, Variables.varR(res));
	}

	@Test
	public void testDDsRvar() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDsR var = Variables.varDDsR(mockFlow, resource);
		assertEquals(var, Variables.varDDsR(mockFlow, resource));
	}

	@Test
	public void testDDdRvar() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDdR var = Variables.varDDdR(mockFlow, resource);
		assertEquals(var, Variables.varDDdR(mockFlow, resource));
	}

	@Test
	public void testDDLRRvar() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Link link = new Link("link");
		Resource src = new Resource("src");
		Resource dest = new Resource("dest");
		DDLRR var = Variables.varDDLRR(mockFlow, link, src, dest);
		DirectedLink dLink = new DirectedLink(link, src, dest);
		assertEquals(var, Variables.varDDLRR(mockFlow, dLink));
	}

	@Test
	public void testDDRvar() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDR var = Variables.varDDR(mockFlow, resource);
		assertEquals(var, Variables.varDDR(mockFlow, resource));
	}

	@Test
	public void testCLRRvar() {
		Communication comm = new Communication("comm");
		Link link = new Link("link");
		Resource source = new Resource("source");
		Resource destination = new Resource("destination");
		CLRR clrrVar = Variables.varCLRR(comm, link, source, destination);
		assertEquals(clrrVar, Variables.varCLRR(comm, link, source, destination));
	}

	@Test
	public void testCRvar() {
		Communication task = new Communication("comm");
		Resource res = new Resource("resource");
		CR crVar = Variables.varCR(task, res);
		assertEquals(crVar, Variables.varCR(task, res));
	}

	@Test
	public void testMvar() {
		Task task = new Task("task");
		Resource res = new Resource("resource");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("mapping", task, res);
		M mVar = Variables.varM(mapping);
		assertEquals(mapping, mVar.getMapping());
		assertEquals(mVar, Variables.varM(mapping));
	}

	@Test
	public void testDTTvar() {
		Dependency dependency = new Dependency("dependency");
		Task sourceTask = new Task("source");
		Communication destinationTask = new Communication("destination");
		DTT dttVar = Variables.varDTT(dependency, sourceTask, destinationTask);
		assertEquals(dttVar, Variables.varDTT(dependency, sourceTask, destinationTask));
	}

	@Test
	public void testTvar() {
		Task task = new Task("task");
		T tVar = Variables.varT(task);
		assertEquals(task, tVar.getTask());
		assertEquals(tVar, Variables.varT(task));
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
