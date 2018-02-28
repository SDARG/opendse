package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public class DDLRRTest {

	@Test
	public void test() {
		Dependency sourceDependency = new Dependency("srcDep");
		Dependency destinationDependency = new Dependency("destDep");
		Link link = new Link("link");
		Resource srcRes = new Resource("src");
		Resource destRes = new Resource("dest");
		DDLRR var = new DDLRR(sourceDependency, destinationDependency, link, srcRes, destRes);

		assertEquals(link, var.getLink());
		assertEquals(srcRes, var.getSrcRes());
		assertEquals(destRes, var.getDestRes());
		assertEquals(sourceDependency, var.getSourceDependency());
		assertEquals(destinationDependency, var.getDestinationDependency());

		DDLRR var2 = new DDLRR(sourceDependency, destinationDependency, link, srcRes, destRes);
		assertEquals(var, var2);
		assertNotEquals(var, destRes);
		assertEquals(var.hashCode(), var2.hashCode());
	}
}
