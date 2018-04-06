package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class VariablesTest {

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
	public void testApplicationAlternativeActivation() {
		String alternativeName = "alternative";
		String alternativeId = "1";
		ApplicationAlternativeActivation aaa1 = Variables.var(alternativeName, alternativeId);
		ApplicationAlternativeActivation aaa2 = Variables.var(alternativeName, alternativeId);
		assertEquals(aaa1, aaa2);
	}

}
