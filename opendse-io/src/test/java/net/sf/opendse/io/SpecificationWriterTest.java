package net.sf.opendse.io;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.Parameters;

public class SpecificationWriterTest {

	protected static File file;
	protected final static String fileName = "spec.xml";
	
	@Before
	public void setUp() {
		file = new File(fileName);
	}
	
	@After
	public void tearDown() {
		file.delete();
	}
	
	@Test
	public void testParameterWriting() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource res = new Resource("res");
		res.setAttribute("attribute", Parameters.select("default", "first", "second", "third"));
		arch.addVertex(res);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Specification beforeWriting = new Specification(appl, arch, mappings);
		SpecificationWriter writer = new SpecificationWriter();
		writer.write(beforeWriting, file);
		SpecificationReader reader = new SpecificationReader();
		Specification afterWriting = reader.read(file);
		Resource resAferWriting = afterWriting.getArchitecture().getVertex("res");
		assertEquals(res.getAttributes().firstEntry().getValue(), resAferWriting.getAttributes().firstEntry().getValue());
	}

}
