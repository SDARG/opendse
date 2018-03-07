package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class VariablesTest {

	@Test
	public void testCR() {
		Task comm = new Communication("comm");
		Resource res = new Resource("res");

		CR first = Variables.var(comm, res);
		CR second = Variables.var(comm, res);
		assertEquals(first, second);
	}

	@Test
	public void testDM() {
		Dependency dep = new Dependency("dep");
		Task t = new Task("t");
		Resource res = new Resource("res");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, res);
		DM first = Variables.var(dep, m);
		DM second = Variables.var(dep, m);
		assertEquals(first, second);
	}

	@Test
	public void testDTTLRR() {
		Dependency sourceDependency = new Dependency("srcDep");
		Dependency destinationDependency = new Dependency("destDep");
		Link link = new Link("link");
		Resource srcRes = new Resource("src");
		Resource destRes = new Resource("dest");

		DDLRR first = Variables.var(sourceDependency, destinationDependency, link, srcRes, destRes);
		DDLRR second = Variables.var(sourceDependency, destinationDependency, link, srcRes, destRes);
		assertEquals(first, second);
	}

	@Test
	public void testDDM() {
		Dependency src = new Dependency("src");
		Dependency dest = new Dependency("dest");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", new Task("t"), new Resource("r"));

		DDM first = Variables.var(src, dest, m);
		DDM second = Variables.var(src, dest, m);

		assertEquals(first, second);
	}

	@Test
	public void testDTT() {
		Dependency dep = new Dependency("dep");
		Task src = new Task("src");
		Task dest = new Task("dest");

		DTT first = Variables.var(dep, src, dest);
		DTT second = Variables.var(dep, src, dest);
		assertEquals(first, second);
	}

	@Test
	public void testT() {
		Task test = new Task("test");
		T first = Variables.var(test);
		T second = Variables.var(test);
		assertEquals(first, second);
	}

	@Test
	public void testR() {
		Resource res = new Resource("res");
		R first = Variables.var(res);
		R second = Variables.var(res);
		assertEquals(first, second);
	}

	@Test
	public void testL() {
		Link l = new Link("link");
		L first = Variables.var(l);
		L second = Variables.var(l);
		assertEquals(first, second);
	}

	@Test
	public void testM() {
		Task t = new Task("t");
		Resource res = new Resource("res");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m1", t, res);
		M var1 = Variables.var(m);
		M var2 = Variables.var(m);
		assertEquals(var1, var2);
	}

	@Test
	public void testApplicationAlternativeActivation() {
		String alternativeName = "alternative";
		String alternativeId = "1";
		ApplicationAlternativeActivation aaa1 = Variables.var(alternativeName, alternativeId);
		ApplicationAlternativeActivation aaa2 = Variables.var(alternativeName, alternativeId);
		assertEquals(aaa1, aaa2);
	}

}
